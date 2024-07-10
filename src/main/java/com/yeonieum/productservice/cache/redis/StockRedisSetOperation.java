package com.yeonieum.productservice.cache.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryRequest;
import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.ShippedStock;
import com.yeonieum.productservice.domain.productinventory.entity.StockUsage;
import com.yeonieum.productservice.domain.productinventory.repository.ShippedStockRepository;
import com.yeonieum.productservice.domain.productinventory.repository.StockUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 캐시 기본전략 : write through
 */
@Component
@RequiredArgsConstructor
public class StockRedisSetOperation {
    private final static String STOCK_USAGE_KEY = "stockusage:";
    private final static String SHIPPED_STOCK_KEY = "shippedstock:";
    private final StockUsageRepository stockUsageRepository;
    private final ShippedStockRepository shippedStockRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;


    public Set<Object> getProductStock(Long productId) throws JsonProcessingException {
        // Redis에서 조회
        String key = "stockusage:50";
        Set<Object> productStockSet = redisTemplate.opsForSet().members("stockusage:50");
        productStockSet.size();
        if (productStockSet == null || productStockSet.size() == 0) {
            productStockSet = new HashSet<>();
            List<StockUsageDto> stockUsageList = stockUsageRepository.findShippedStockByProductId(productId);
            LocalDateTime shippedTime = LocalDateTime.now().withHour(14);
            List<StockUsageDto> shippedStockDtoList = shippedStockRepository.findShippedStockBeforeTodayShippedTime(productId, shippedTime);

            productStockSet = new HashSet<>(stockUsageList);
            productStockSet.removeAll(shippedStockDtoList);
            for(Object stockUsageDto : productStockSet) {
                redisTemplate.opsForSet().add(key, (StockUsageDto) stockUsageDto);
                LocalDateTime tomorrow14 = LocalDateTime.now().plusDays(1).withHour(14);
                long expirationSeconds = Duration.between(LocalDateTime.now(), tomorrow14).getSeconds();
                redisTemplate.expire(key,expirationSeconds, TimeUnit.SECONDS);
            }
        }

        return productStockSet;
    }


    public Set<Object> getShippedStock(Long productId) {
        // Redis에서 조회
        String key = getShippedStockKey(productId);
        Set<Object> shippedStockSet = redisTemplate.opsForSet().members(key);

        if (shippedStockSet == null) {
            shippedStockSet = new HashSet<>();
            LocalDateTime shippedTime = LocalDateTime.now().withHour(14);
            getShippedStock(productId);
            List<ShippedStockDto> shippedStockList = shippedStockRepository.findShippedStockAfterTodayShippedTime(productId, shippedTime);
            // 캐시에서 조회로 변경해야함


            shippedStockSet = new HashSet<>(shippedStockList);
            for(Object shippedStockDto : shippedStockSet) {
                redisTemplate.opsForValue().set(key, shippedStockDto);
                LocalDateTime tomorrow14 = LocalDateTime.now().plusDays(1).withHour(14);
                long expirationSeconds = Duration.between(LocalDateTime.now(), tomorrow14).getSeconds();

                redisTemplate.expire(key,expirationSeconds, TimeUnit.SECONDS);
            }
        }
        return shippedStockSet;
    }


    /**
     * ShippedStock SADD연산
     *
     * @param shippedStockDto
     */
    @Transactional(rollbackFor = {Exception.class})
    public void addShippedStock(ShippedStockDto shippedStockDto) {
        ShippedStock savedEntity = null;
        try {
            SetOperations<String, Object> setOps = redisTemplate.opsForSet();
            String key = getShippedStockKey(shippedStockDto.getProductId());
            setOps.add(key, shippedStockDto);

            ShippedStock shippedStock = ShippedStock.builder()
                    .productId(shippedStockDto.getProductId())
                    .orderId(shippedStockDto.getOrderId())
                    .quantity(shippedStockDto.getQuantity())
                    .shippedDateTime(shippedStockDto.getShippedTime())
                    .build();
            savedEntity = shippedStockRepository.save(shippedStock);
        } catch (Exception e) {
            // 캐시 삭제 및 데이터 삭제 + 메시지발행
            if(savedEntity != null) {
                shippedStockRepository.deleteById(savedEntity.getOrderId());
                removeShippedStock(shippedStockDto);
                // 메시지 발행
            }
        }
    }

    /**
     * ShippedStock SREM 연산
     *
     * @param shippedStockDto
     * @return
     */
    public Long removeShippedStock(ShippedStockDto shippedStockDto) {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        String key = getShippedStockKey(shippedStockDto.getProductId());
        Long popCnt = setOps.remove(key, shippedStockDto);
        return popCnt;
    }


    /**
     * StockUsage SADD 연산
     *
     * @param stockUsageDto
     */
    @Transactional(rollbackFor = {Exception.class})
    public void addStockUsage(StockUsageDto stockUsageDto) {
        StockUsage stockUsage = null;
        try {
            SetOperations<String, Object> setOps = redisTemplate.opsForSet();
            String key = getStockUsageKey(stockUsageDto.getProductId());
            Long result = setOps.add(key, stockUsageDto);
            System.out.println("결과 : " + result);
            stockUsage = StockUsage.builder()
                    .productId(stockUsageDto.getProductId())
                    .orderId(stockUsageDto.getOrderId())
                    .quantity(stockUsageDto.getQuantity())
                    .build();

            stockUsageRepository.save(stockUsage);
        } catch (Exception e) {
            if(stockUsage != null) {
                stockUsageRepository.deleteById(stockUsage.getOrderId());
            }
            removeStockUsageOnlyForRedis(stockUsageDto);
        }
    }

    /**
     * StockUsage SREM 연산
     *
     * @param stockUsageDto
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public Long removeStockUsage(StockUsageDto stockUsageDto) {
        try {
            SetOperations<String, Object> setOps = redisTemplate.opsForSet();
            String key = getStockUsageKey(stockUsageDto.getProductId());
            Long popCnt = setOps.remove(key, stockUsageDto);

            stockUsageRepository.deleteById(stockUsageDto.getOrderId());
            return popCnt;
        } catch(Exception e) {

            e.printStackTrace();
        }
        return null;
        // 레포지토리 저장하기
    }



    /**
     * StockUsage SREM 연산
     *
     * @param stockUsageDto
     * @return
     */
    public Long removeStockUsageOnlyForRedis(StockUsageDto stockUsageDto) {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        String key = getStockUsageKey(stockUsageDto.getProductId());
        Long popCnt = setOps.remove(key, stockUsageDto);
        // 레포지토리 저장하기
        return popCnt;
    }

    /**
     * StockUsage 총 사용량 반환
     *
     * @param productId
     * @return
     */
    public Integer totalStockUsageCount(Long productId) {
        try {
            getProductStock(productId);
            SetOperations<String, Object> setOps = redisTemplate.opsForSet();
            int orderPendingAmount = 0;
            String key = getStockUsageKey(productId);

            for (Object stock : setOps.members(key)) {
                ObjectMapper objectMapper = new ObjectMapper();
                StockUsageDto stockUsageDto = objectMapper.convertValue(stock, StockUsageDto.class);
                orderPendingAmount += stockUsageDto.getQuantity();
            }
            return orderPendingAmount;
        }catch (Exception e) {
            e.printStackTrace();
        }
       return 0;
    }



    public String getStockUsageKey(Long productId) {
        return STOCK_USAGE_KEY + "" + productId;
    }

    public String getShippedStockKey(Long productId) {
        return SHIPPED_STOCK_KEY + "" + productId;
    }
}