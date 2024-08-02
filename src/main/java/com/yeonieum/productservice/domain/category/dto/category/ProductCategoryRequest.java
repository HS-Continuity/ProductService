package com.yeonieum.productservice.domain.category.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductCategoryRequest {

    @Getter
    @NoArgsConstructor
    public static class RegisterCategoryDto {

        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(max = 20, message = "카테고리 이름은 최대 20자까지 가능합니다.")
        private String categoryName;

    }

    @Getter
    @NoArgsConstructor
    public static class ModifyCategoryDto {

        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(max = 20, message = "카테고리 이름은 최대 20자까지 가능합니다.")
        private String categoryName;
    }
}
