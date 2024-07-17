package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Builder;
import lombok.Getter;

public class StockUsageRequest {

    @Getter
    @Builder
    public static class OfIncreasing {
        Long orderId;
        Long productId;
        int quantity;
        String memberId;
    }

    @Getter
    @Builder
    public static class OfDecreasing {
        Long orderId;
        Long productId;
        int quantity;
    }
}
