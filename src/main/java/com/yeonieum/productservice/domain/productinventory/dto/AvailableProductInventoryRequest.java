package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Getter;

public class AvailableProductInventoryRequest {

    @Getter
    public static class IncreaseStockUsageDto {
        Long orderId;
        Long productId;
        int quantity;
        String memberId;
    }

    @Getter
    public static class DecreaseStockUsageDto {
        Long orderId;
        Long productId;
        int quantity;
    }
}
