package com.yeonieum.productservice.global.aop;

import com.yeonieum.productservice.cache.redis.StockRedisSetOperation;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryRequest;
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
    public Object distributionLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Lock lock = method.getAnnotation(Lock.class);


        AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto =
                (AvailableProductInventoryRequest.IncreaseStockUsageDto) joinPoint.getArgs()[0];
        String keyPrefix = lock.keyPrefix();
        Long productId = increaseStockUsageDto.getProductId();

        int stockUsageCount = stockRedisSetOperation.totalStockUsageCount(increaseStockUsageDto.getProductId());
        int stock = stockSystemService.retrieveProductStockAmount(increaseStockUsageDto.getProductId());

        if(stock < stockUsageCount + increaseStockUsageDto.getQuantity()) {
            return failResponse(increaseStockUsageDto);
        }


        String key = keyPrefix + productId;
        RLock rLock = redissonClient.getLock(key);
        String lockName = rLock.getName();
        try {
            boolean available =
                    rLock.tryLock(
                            lock.waitTime(),
                            lock.leaseTime(),
                            lock.timeUnit());
            if (!available) {
                return failResponse(increaseStockUsageDto);
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {

            e.printStackTrace();
            return failResponse(increaseStockUsageDto);
        } catch (Exception e) {

            e.printStackTrace();
        } finally{
            try {

                rLock.unlock();
            } catch (IllegalMonitorStateException e) {

                e.printStackTrace();
            }
            return null;
        }
    }


    public AvailableProductInventoryResponse failResponse(AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto) {
        return AvailableProductInventoryResponse.builder()
                .productId(increaseStockUsageDto.getProductId())
                .orderId(increaseStockUsageDto.getOrderId())
                .quantity(increaseStockUsageDto.getQuantity())
                .build();
    }
}
