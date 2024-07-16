package com.yeonieum.productservice.cache.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.ShippedStock;
import com.yeonieum.productservice.domain.productinventory.entity.StockUsage;
import com.yeonieum.productservice.domain.productinventory.repository.ShippedStockRepository;
import com.yeonieum.productservice.domain.productinventory.repository.StockUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 캐시 기본전략 : write through
 */
@Service
@RequiredArgsConstructor
public class StockRedisSetOperation {
    private final static String STOCK_USAGE_KEY = "stockusage:";
    private final static String SHIPPED_STOCK_KEY = "shippedstock:";
    private final StockUsageRepository stockUsageRepository;
    private final ShippedStockRepository shippedStockRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Set<Object> getProductStock(Long productId) {
        String key = getStockUsageKey(productId);

        // Redis에서 재고 조회
        Set<Object> productStockSet = redisTemplate.opsForSet().members(key);

        if (productStockSet == null || productStockSet.isEmpty()) {
            productStockSet = fetchAndCacheProductStock(productId);
        }

        return productStockSet;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Set<Object>> bulkGetProductStock(List<Long> productIdList) throws InterruptedException {
        List<String> keys = productIdList.stream()
                .map(this::getStockUsageKey)
                .collect(Collectors.toList());

        // 파이프라인을 통해 재고 사용량을 한번에 조회해옴
        List<Object> redisResults = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
            keys.forEach(key -> connection.setCommands().sMembers(keySerializer.serialize(key)));
            return null;
        });

        //파이프라인을 통해 가져온 결과를 List<Set> 에 할당
        List<Set<Object>> productStockSets = redisResults.stream()
                .map(result -> result instanceof Set ? (Set<Object>) result : Collections.emptySet())
                .collect(Collectors.toList());

        // 레디스에 존재하지 않은 키에 대해서 레포지토리 -> 레디스에 올림
        List<Set<Object>> responseList = new ArrayList<>();
        for (int i = 0; i < productIdList.size(); i++) {
            Long productId = productIdList.get(i);
            Set<Object> productStockSet = productStockSets.get(i);
            if (productStockSet.isEmpty()) {
                productStockSet = fetchAndCacheProductStock(productId);
            }
            responseList.add(productStockSet);
        }

        return responseList;
    }

    private Set<Object> fetchAndCacheProductStock(Long productId){
        List<StockUsageDto> stockUsageList = stockUsageRepository.findShippedStockByProductId(productId);
        LocalDateTime shippedTime = LocalDateTime.now().withHour(14);
        List<StockUsageDto> shippedStockDtoList = shippedStockRepository.findShippedStockBeforeTodayShippedTime(productId, shippedTime);

        Set<Object> productStockSet = new HashSet<>(stockUsageList);
        productStockSet.removeAll(shippedStockDtoList);

        productStockSet.forEach(stockUsageDto -> setCacheExpiration(getStockUsageKey(productId), (StockUsageDto) stockUsageDto));
        if (stockUsageList.isEmpty()) {
            setCacheExpiration(getStockUsageKey(productId), new StockUsageDto(productId, null, 0));
        }

        return productStockSet;
    }



    public void setCacheExpiration(String key, StockUsageDto stockUsageDto){
        try {
            redisTemplate.opsForSet().add(key, objectMapper.writeValueAsString(stockUsageDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        LocalDateTime tomorrow14 = LocalDateTime.now().plusDays(1).withHour(14);
        long expirationSeconds = Duration.between(LocalDateTime.now(), tomorrow14).getSeconds();
        redisTemplate.expire(key,expirationSeconds, TimeUnit.SECONDS);
    }


    public Set<Object> getShippedStock(Long productId) {
        // Redis에서 조회
        String key = getShippedStockKey(productId);
        Set<Object> shippedStockSet = redisTemplate.opsForSet().members(key);

        if (shippedStockSet == null) {
            LocalDateTime shippedTime = LocalDateTime.now().withHour(14);
            getShippedStock(productId);
            List<ShippedStockDto> shippedStockList = shippedStockRepository.findShippedStockAfterTodayShippedTime(productId, shippedTime);
            // 캐시에서 조회로 변경해야함

            shippedStockSet = new HashSet<>(shippedStockList);
            for(Object shippedStockDto : shippedStockSet) {

                redisTemplate.opsForSet().add(key, shippedStockDto);
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
            String key = getStockUsageKey(stockUsageDto.getProductId());
            Long result = redisTemplate.opsForSet().add(key, objectMapper.writeValueAsString(stockUsageDto));

            LocalDateTime tomorrow14 = LocalDateTime.now().plusSeconds(60);
            long expirationSeconds = Duration.between(LocalDateTime.now(), tomorrow14).getSeconds();
            redisTemplate.expire(key,expirationSeconds, TimeUnit.SECONDS);
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
            Set<Object> set = getProductStock(productId);
            int orderPendingAmount = 0;
            for (Object stock : set) {
                ObjectMapper objectMapper = new ObjectMapper();
                StockUsageDto stockUsageDto = objectMapper.convertValue(stock, StockUsageDto.class);
                orderPendingAmount += stockUsageDto.getQuantity();
            }
            return orderPendingAmount;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Integer> bulkTotalStockUsageCount(List<Long> productIdList) {
        try {
            List<Integer> orderPendingAmountList = new ArrayList<>();
            List<Set<Object>> setList = bulkGetProductStock(productIdList);
            for(Set<Object> set : setList) {
                int orderPendingAmount = 0;
                for (Object stock : set) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    StockUsageDto stockUsageDto = objectMapper.convertValue(stock, StockUsageDto.class);
                    orderPendingAmount += stockUsageDto.getQuantity();
                }
                orderPendingAmountList.add(orderPendingAmount);
            }
            return orderPendingAmountList;
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }



    public String getStockUsageKey(Long productId) {
        return STOCK_USAGE_KEY + productId;
    }

    public String getShippedStockKey(Long productId) {
        return SHIPPED_STOCK_KEY + productId;
    }
}