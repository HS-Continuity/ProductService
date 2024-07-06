package com.yeonieum.productservice.domain.product.dto.memberservice;

import lombok.Builder;

public class ProductManagementResponse {
    @Builder
    public static class RetrieveDto {
        String mainCategoryName;

        String subCategoryName;

        String productName;
        String salesType;
        // ~자 이내
        String description;
        // 0 이상 값
        int price;
        // 빈문자열, 공백, null 허용 안됨
        String origin;
        // 기본값 true
        char isPageVisibility;
        // 기본값 true
        char isRegularSale;
        // 0 ~ 99사이
        int baseDiscountRate;
        // 0 ~ 99사이
        int regularDiscountRate;
        // 0 ~ 99사
        int personalizedDiscountRate;
    }
}
