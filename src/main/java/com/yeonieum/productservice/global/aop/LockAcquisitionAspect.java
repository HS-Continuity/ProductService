package com.yeonieum.productservice.global.aop;

import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.global.lock.Lock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.lang.reflect.Method;

@Aspect
@RequiredArgsConstructor
public class LockAcquisitionAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.yeonieum.productservice.global.lock.Lock) && args(increaseStockUsageDto)")
    public Object distributionLock(final ProceedingJoinPoint joinPoint, AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Lock lock = method.getAnnotation(Lock.class);

        String key_prefix = lock.keyPrefix();
        Long productId = increaseStockUsageDto.getProductId();

        String key = key_prefix + productId;
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
            return failResponse(increaseStockUsageDto);
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                e.printStackTrace();
            }
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
