package com.yeonieum.productservice.cache.redis;

import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockUsageService {
    private final StockRedisSetOperation stockRedisSetOperation;

    public void test(int available, Long productId, Long orderId, int quantity) {
        if (available >= stockRedisSetOperation.totalStockUsageCount(productId) + quantity) {
            stockRedisSetOperation.addStockUsage(new StockUsageDto(productId, orderId, quantity));
        }
    }

    /**
     * 주문 취소 이벤트 consume시 발생하는 재고사용량 감소
     * @param ofDecreasing
     * @return
     */
    @Transactional
    public boolean decreaseStockUsage(StockUsageRequest.OfDecreasing ofDecreasing) {
        Long productId = ofDecreasing.getProductId();
        Long orderId = ofDecreasing.getOrderId();
        int quantity = ofDecreasing.getQuantity();

        Long decreaseCount = stockRedisSetOperation.removeStockUsage(new StockUsageDto(productId, orderId, quantity));
        if(decreaseCount == 0) {
            return false;
        }
        return true;
    }


    /**
     * 주문 가능 시 재고사용량 증가 메서드
     * @param stockUsageDto
     * @param available
     */
    @Transactional
    public boolean increaseStockUsage(StockUsageDto stockUsageDto, int available) {
        Integer totalStockUsageCount = stockRedisSetOperation.totalStockUsageCount(stockUsageDto.getProductId());
        if (totalStockUsageCount == null) {
            totalStockUsageCount = 0;
        }

        if (available >= totalStockUsageCount + stockUsageDto.getQuantity()) {
            stockRedisSetOperation.addStockUsage(stockUsageDto);
            return true;
        } else {
            return false;
        }
    }
}
