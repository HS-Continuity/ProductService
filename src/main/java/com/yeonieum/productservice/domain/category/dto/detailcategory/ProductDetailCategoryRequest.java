package com.yeonieum.productservice.domain.category.dto.detailcategory;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductDetailCategoryRequest {

    @Getter
    @NoArgsConstructor
    public static class RegisterDetailCategoryDto {

        private Long productCategoryId;
        private String detailCategoryName;
        private int shelfLifeDay;
    }

    @Getter
    @NoArgsConstructor
    public static class ModifyDetailCategoryDto {

        private String detailCategoryName;
        private int shelfLifeDay;
    }
}
