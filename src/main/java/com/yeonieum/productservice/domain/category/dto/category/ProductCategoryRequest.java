package com.yeonieum.productservice.domain.category.dto.category;


import lombok.*;

public class ProductCategoryRequest {
    @Getter
    @Builder
    public static class RegisterCategoryDto {
        private String categoryName;
    }

    @Getter
    @Builder
    public static class ModifyCategoryDto {
        private String categoryName;
    }
}
