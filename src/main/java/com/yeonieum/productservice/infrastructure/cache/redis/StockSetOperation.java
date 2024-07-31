package com.yeonieum.productservice.infrastructure.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.ShippedStock;
import com.yeonieum.productservice.domain.productinventory.repository.ShippedStockRepository;
import com.yeonieum.productservice.domain.productinventory.repository.StockUsageRepository;
import com.yeonieum.productservice.infrastructure.cache.data.StockUsageCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class StockSetOperation {
    private final static String STOCK_USAGE_KEY = "stockusage:";
    private final static String SHIPPED_STOCK_KEY = "completedstock:";
    private final StockUsageRepository stockUsageRepository;
    private final ShippedStockRepository shippedStockRepository;
    @Qualifier("stockRedisTemplate")
    private final RedisTemplate<String, Object> stockRedisTemplate;


    @Transactional
    public Long getProductStock(Long productId) {
        String key = getStockUsageKey(productId);
        Integer stockUsageCount = 0;
        if(keyExists(key)) {
            return fetchAndCacheProductStock(productId);
        }
        return stockRedisTemplate.opsForSet().size(key);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<Long, Long> bulkGetProductStockUsage(List<Long> productIdList) throws InterruptedException {
        List<String> keys = productIdList.stream()
                .map(this::getStockUsageKey)
                .collect(Collectors.toList());

        // 파이프라인을 통해 재고 사용량을 한번에 조회해옴
        List<Object> redisResults = stockRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisSerializer<String> keySerializer = stockRedisTemplate.getStringSerializer();
            keys.forEach(key -> connection.setCommands().sCard(keySerializer.serialize(key)));
            return null;
        });

        //파이프라인을 통해 가져온 결과를 List<Integer> 에 할당
        List<Long> productStockList = redisResults.stream()
                .map(result -> result instanceof Long ? (Long) result : null)
                .collect(Collectors.toList());

        // productIdList와 productStockList를 이용하여 productId를 키로 갖고 재고사용 수량을 벨류로 갖는 맵 생성
        Map<Long, Long> productStockMap = new HashMap<>();
        for (int i = 0; i < productIdList.size(); i++) {
            productStockMap.put(productIdList.get(i), productStockList.get(i));
        }

        // Map 에서 null을 벨류로 갖는 키에 대해 fetchAndCacheProductStock 메서드를 호출하여 재고 사용량을 조회하고 캐싱
        for(Long productId : productIdList) {
            if(productStockMap.get(productId) == null) {
                productStockMap.put(productId, fetchAndCacheProductStock(productId));
            }
        }

        return productStockMap;
    }

    private Long fetchAndCacheProductStock(Long productId){
        Set<StockUsageCache> stockUsageCacheSet = stockUsageRepository.findShippedStockByProductId(productId);
        LocalDateTime shippedTime = LocalDateTime.now().withHour(14);
        Set<StockUsageCache> shippedStockCacheSet = shippedStockRepository.findShippedStockBeforeTodayShippedTime(productId, shippedTime);

        // 미처리된 재고사용 수량 조회
        stockUsageCacheSet.removeAll(shippedStockCacheSet);
        if (stockUsageCacheSet.isEmpty()) {
            // 빈 셋을 넣으면 키는 삽입되지만, 값은 들어가지 않음.
            setCacheExpiration(getStockUsageKey(productId), Collections.emptySet());
        } else {
            setCacheExpiration(getStockUsageKey(productId), stockUsageCacheSet);
        }

        return Long.valueOf(stockUsageCacheSet.size());
    }



    public void setCacheExpiration(String key, Set<StockUsageCache> stockUsageCache){
        if(stockUsageCache.isEmpty()) {
            stockRedisTemplate.opsForSet().add(key, "");
        } else {
            stockRedisTemplate.opsForSet().add(key, stockUsageCache.toArray());
        }
        LocalDateTime tomorrow14 = LocalDateTime.now().plusDays(1).withHour(14);
        long expirationSeconds = Duration.between(LocalDateTime.now(), tomorrow14).getSeconds();
        stockRedisTemplate.expire(key,expirationSeconds, TimeUnit.SECONDS);
    }



    /**
     * ShippedStock SADD연산
     *
     * @param
     */
    @Transactional(rollbackFor = {Exception.class})
    public void addShippedStock(Long productId, List<StockUsageCache> shippedStockCaches) {
        ShippedStock savedEntity = null;
        try {
            SetOperations<String, Object> setOps = stockRedisTemplate.opsForSet();
            String key = getShippedStockKey(productId);
            setOps.add(key, shippedStockCaches.toArray());


            shippedStockRepository.saveAll(shippedStockCaches.stream()
                    .map(StockUsageCache::toShippedStockEntity)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            stockRedisTemplate.opsForSet().remove(getShippedStockKey(productId), shippedStockCaches.toArray());
            // 메시지 발행
        }
    }

    /**
     * ShippedStock SREM 연산
     *
     * @param
     * @return
     */
    public void removeShippedStock(Long productId, List<StockUsageCache> stockUsageCaches) {
        stockRedisTemplate.opsForSet().remove(getShippedStockKey(productId) , stockUsageCaches.toArray());
    }


    /**
     * StockUsageCaches SADD 연산
     *
     * @param
     */
    @Transactional(rollbackFor = {Exception.class})
    public void addStockUsage(Long productId, List<StockUsageCache> stockUsageCaches) {
        try {
            stockRedisTemplate.opsForSet().add(getStockUsageKey(productId), stockUsageCaches.toArray());
            setCacheExpiration(getStockUsageKey(productId), new HashSet<>(stockUsageCaches));

            // writeThrough
            stockUsageRepository.saveAll(stockUsageCaches.stream().map(StockUsageCache::toEntity).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            stockRedisTemplate.opsForSet().remove(getStockUsageKey(productId), stockUsageCaches.toArray());
        }
    }

    /**
     * StockUsage SREM 연산
     *
     * @param
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public Long removeStockUsage(Long productId, String orderDetailId, List<StockUsageCache>  stockUsageCaches) {
        try {
            SetOperations<String, Object> setOps = stockRedisTemplate.opsForSet();
            String key = getStockUsageKey(productId);
            Long popCnt = setOps.remove(key, stockUsageCaches.toArray());

            stockUsageRepository.deleteByProductIdAndOrderDetailId(productId, orderDetailId);
            return popCnt;
        } catch(Exception e) {
            stockRedisTemplate.opsForSet().add(getStockUsageKey(productId), stockUsageCaches.toArray());
            e.printStackTrace();
        }
        return null;
    }


    /**
     * StockUsage 총 사용량 반환
     *
     * @param productId
     * @return
     */
    public Long totalStockUsageCount(Long productId) {
        try {
            return getProductStock(productId);
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Long, Long> bulkTotalStockUsageCount(List<Long> productIdList) {
        try {
            return bulkGetProductStockUsage(productIdList);
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

    public boolean keyExists(String key) {
        return Boolean.TRUE.equals(stockRedisTemplate.hasKey(key));
    }
}