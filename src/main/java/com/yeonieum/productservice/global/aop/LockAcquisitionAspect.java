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
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAcquisitionAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.yeonieum.productservice.global.lock.Lock)")
    public Object distributionLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Lock lock = method.getAnnotation(Lock.class);

        String key_prefix = lock.keyPrefix();
        //Long productId = increaseStockUsageDto.getProductId();
        AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto = (AvailableProductInventoryRequest.IncreaseStockUsageDto) joinPoint.getArgs()[0];

        String key = "stockusage:" + 50;
        RLock rLock = redissonClient.getLock(key);
        String lockName = rLock.getName();
        try {
            boolean available =
                    rLock.tryLock(
                            lock.waitTime(),
                            lock.leaseTime(),
                            lock.timeUnit());
            if (!available) {
                System.out.println("락획득실패");

                return failResponse(increaseStockUsageDto);
            }
            System.out.println("락성공");
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("인터럽션");
            return failResponse(increaseStockUsageDto);
        } finally {
            try {
                System.out.println("락해제");
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                System.out.println("이미 해제됨");
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
