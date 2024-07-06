package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Getter;

import java.time.LocalDate;

public class ProductInventoryManagementRequest {
    /*
상품ID
입고날짜
재고량
소비기한
 */
    @Getter
    public static class RegisterDto {
        Long productId;
        LocalDate warehouseDate;
        int quantity;
        LocalDate expirationDate;
    }

    @Getter
    public static class ModifyDto {
        LocalDate warehousingDate;
        int quantity;
        LocalDate expirationDate;
    }
}
