package com.yeonieum.productservice.domain.category.dto.detailcategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductDetailCategoryRequest {

    @Getter
    @NoArgsConstructor
    public static class RegisterDetailCategoryDto {

        private Long productCategoryId;

        @NotBlank(message = "상세 카테고리 이름은 필수입니다.")
        @Size(max = 20, message = "상세 카테고리 이름은 최대 20자까지 가능합니다.")
        private String detailCategoryName;

        @NotNull(message = "상세 카테고리 소비기한 설정은 필수입니다.")
        private int shelfLifeDay;
    }

    @Getter
    @NoArgsConstructor
    public static class ModifyDetailCategoryDto {

        @NotBlank(message = "상세 카테고리 이름은 필수입니다.")
        @Size(max = 20, message = "상세 카테고리 이름은 최대 20자까지 가능합니다.")
        private String detailCategoryName;

        @NotNull(message = "상세 카테고리 소비기한 설정은 필수입니다.")
        private int shelfLifeDay;
    }
}
