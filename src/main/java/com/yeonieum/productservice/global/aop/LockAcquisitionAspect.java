package com.yeonieum.productservice.global.aop;

import com.yeonieum.productservice.cache.redis.StockRedisSetOperation;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import com.yeonieum.productservice.global.lock.Lock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAcquisitionAspect {

    private final RedissonClient redissonClient;
    private final StockSystemService stockSystemService;
    private final StockRedisSetOperation stockRedisSetOperation;


    @Around("@annotation(com.yeonieum.productservice.global.lock.Lock)")
    public Object distributionLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Lock lock = method.getAnnotation(Lock.class);


        StockUsageRequest.OfIncreasing ofIncreasing =
                (StockUsageRequest.OfIncreasing) joinPoint.getArgs()[0];
        String keyPrefix = lock.keyPrefix();
        Long productId = ofIncreasing.getProductId();

        int stockUsageCount = stockRedisSetOperation.totalStockUsageCount(ofIncreasing.getProductId());
        int stock = stockSystemService.retrieveProductStockAmount(ofIncreasing.getProductId());

        if(stock < stockUsageCount + ofIncreasing.getQuantity()) {
            return failResponse(ofIncreasing);
        }

        String key = keyPrefix + productId;
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean available =
                    rLock.tryLock(
                            lock.waitTime(),
                            lock.leaseTime(),
                            lock.timeUnit());
            if (!available) {
                return failResponse(ofIncreasing);
            }

            return joinPoint.proceed();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return failResponse(ofIncreasing);
        } catch (Exception e) {
            e.printStackTrace();
            return failResponse(ofIncreasing);
        } finally{
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                e.printStackTrace();
            }
        }
    }


    public AvailableProductInventoryResponse failResponse(StockUsageRequest.OfIncreasing ofIncreasing) {
        return AvailableProductInventoryResponse.builder()
                .productId(ofIncreasing.getProductId())
                .orderId(ofIncreasing.getOrderId())
                .quantity(ofIncreasing.getQuantity())
                .build();
    }
}
