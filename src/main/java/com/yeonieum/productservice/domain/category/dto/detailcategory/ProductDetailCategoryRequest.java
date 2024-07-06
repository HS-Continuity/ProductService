package com.yeonieum.productservice.domain.category.dto.detailcategory;

import lombok.*;

public class ProductDetailCategoryRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class RegisterDetailCategoryDto {

        private Long productCategoryId;
        private String categoryDetailName;
        private int shelfLifeDay;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ModifyDetailCategoryDto {

        private String categoryDetailName;
        private int shelfLifeDay;
    }
}
