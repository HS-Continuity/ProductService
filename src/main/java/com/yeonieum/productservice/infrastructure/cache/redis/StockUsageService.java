package com.yeonieum.productservice.infrastructure.cache.redis;

import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.infrastructure.cache.data.StockUsageCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockUsageService {
    private final StockSetOperation stockSetOperation;

    /**
     * 주문 취소 이벤트 consume시 발생하는 재고사용량 감소
     * @param ofDecreasing
     * @return
     */
    @Transactional
    public boolean decreaseStockUsage(StockUsageRequest.OfDecreasing ofDecreasing) {
//        Long decreaseCount = stockSetOperation.removeStockUsage(
//                new StockUsageDto(ofDecreasing.getProductId(),
//                        ofDecreasing.getOrderDetailId(),
//                        ofDecreasing.getQuantity())
//        );
//        if(decreaseCount == 0) {
//            return false;
//        }
        return true;
    }


    /**
     * 주문 가능 시 재고사용량 증가 메서드
     * @param stockUsageDto
     * @param available
     */
    @Transactional
    public boolean increaseStockUsage(StockUsageDto stockUsageDto, int available) {
        Long totalStockUsageCount = stockSetOperation.getProductStock(stockUsageDto.getProductId());
        //Integer totalStockUsageCount = stockRedisSetOperation.totalStockUsageCount(stockUsageDto.getProductId());
        if (totalStockUsageCount == null) {
            totalStockUsageCount = 0L;
        }

        List<StockUsageCache> stockUsageCaches = new ArrayList<>();
        for(int i = 0; i < stockUsageDto.getQuantity(); i++) {
            stockUsageCaches.add(StockUsageCache.builder()
                    .id(i)
                    .orderDetailId(stockUsageDto.getOrderDetailId())
                    .productId(stockUsageDto.getProductId()).build());
        }


        if (available >= totalStockUsageCount + stockUsageDto.getQuantity()) {
            stockSetOperation.addStockUsage(stockUsageDto.getProductId(), stockUsageCaches);
            return true;
        } else {
            return false;
        }
    }
}
