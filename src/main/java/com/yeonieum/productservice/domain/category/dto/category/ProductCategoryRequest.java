package com.yeonieum.productservice.domain.category.dto.category;


import lombok.*;

public class ProductCategoryRequest {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class RegisterCategoryDto {
        private String categoryName;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ModifyCategoryDto {
        private String categoryName;
    }
}
