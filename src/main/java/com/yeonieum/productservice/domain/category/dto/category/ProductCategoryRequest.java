package com.yeonieum.productservice.domain.category.dto.category;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductCategoryRequest {

    @Getter
    @NoArgsConstructor
    public static class RegisterCategoryDto {
        private String categoryName;

    }

    @Getter
    @NoArgsConstructor
    public static class ModifyCategoryDto {
        private String categoryName;
    }
}
