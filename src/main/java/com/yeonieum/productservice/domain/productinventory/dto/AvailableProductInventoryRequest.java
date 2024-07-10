package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Builder;
import lombok.Getter;

public class AvailableProductInventoryRequest {

    @Getter
    @Builder
    public static class IncreaseStockUsageDto {
        Long orderId;
        Long productId;
        int quantity;
        String memberId;
    }

    @Getter
    @Builder
    public static class DecreaseStockUsageDto {
        Long orderId;
        Long productId;
        int quantity;
    }
}
