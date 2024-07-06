package com.yeonieum.productservice.domain.category.dto.category;

import lombok.*;

import java.util.List;

public class ProductCategoryResponse {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class RetrieveAllCategoryDto {

        private Long productCategoryId;
        private String categoryName;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class RetrieveCategoryWithDetailsDto {

        private Long productCategoryId;
        private String categoryName;
        private List<ProductDetailCategoryDto> productDetailCategoryList;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ProductDetailCategoryDto {

        private Long productDetailCategoryId;
        private String categoryDetailName;
        private int shelfLifeDay;
    }
}
