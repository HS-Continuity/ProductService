package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class StockUsageRequest {

    @Getter
    @NoArgsConstructor
    public static class OfIncreasing {
        String orderDetailId;
        Long productId;
        int quantity;
        String memberId;
    }

    @Getter
    @NoArgsConstructor
    public static class IncreaseStockUsageList {
        List<OfIncreasing> ofIncreasingList;
    }

    @Getter
    @Builder
    public static class OfDecreasing {
        String orderDetailId;
        Long productId;
        int quantity;
    }
}
