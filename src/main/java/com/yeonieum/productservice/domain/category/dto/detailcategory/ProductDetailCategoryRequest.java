package com.yeonieum.productservice.domain.category.dto.detailcategory;

import lombok.*;

public class ProductDetailCategoryRequest {

    @Getter
    @Builder
    public static class RegisterDetailCategoryDto {

        private Long productCategoryId;
        private String detailCategoryName;
        private int shelfLifeDay;
    }

    @Getter
    @Builder
    public static class ModifyDetailCategoryDto {

        private String detailCategoryName;
        private int shelfLifeDay;
    }
}
