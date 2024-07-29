package com.yeonieum.productservice.domain.productinventory.dto;

import jakarta.validation.constraints.NotNull;
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

        @NotNull(message = "상품 아이디는 필수입니다.")
        Long productId;

        @NotNull(message = "입고날짜는 필수입니다.")
        LocalDate warehouseDate;

        int quantity;

        @NotNull(message = "상품 소비기한 입력은 필수입니다.")
        LocalDate expirationDate;
    }

    @Getter
    public static class ModifyDto {

        @NotNull(message = "입고날짜는 필수입니다.")
        LocalDate warehousingDate;

        int quantity;

        @NotNull(message = "상품 소비기한 입력은 필수입니다.")
        LocalDate expirationDate;
    }
}
