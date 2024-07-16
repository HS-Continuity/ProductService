package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AvailableProductInventoryRequest {

    @Getter
    @NoArgsConstructor
    public static class IncreaseStockUsageDto {
        Long orderId;
        Long productId;
        int quantity;
        String memberId;
    }

    @Getter
    @NoArgsConstructor
    public static class IncreaseStockUsageList {
        List<IncreaseStockUsageDto> increaseStockUsageDtoList;
    }

    @Getter
    @Builder
    public static class DecreaseStockUsageDto {
        Long orderId;
        Long productId;
        int quantity;
    }
}
