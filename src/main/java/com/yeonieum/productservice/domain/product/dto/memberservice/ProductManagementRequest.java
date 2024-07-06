package com.yeonieum.productservice.domain.product.dto.memberservice;

import lombok.Builder;
import lombok.Getter;

public class ProductManagementRequest {
    @Getter
    public static class RegisterNormalProduct
            extends RegisterDto implements NormalProduct {
        @Builder
        public RegisterNormalProduct(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate, int personalizedDiscountRate) {

            super(customerId, mainCategoryId, subCategoryId, productName, description, image, price, origin, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate, personalizedDiscountRate);
        }
    }

    @Getter

    public static class RegisterEcoFriendlyProductDto
            extends RegisterDto implements CertifiedProduct {
        Certification certification;
        @Builder
        public RegisterEcoFriendlyProductDto(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate, int personalizedDiscountRate, Certification certification) {
            super(customerId, mainCategoryId, subCategoryId, productName, description, image, price, origin, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate, personalizedDiscountRate);
            this.certification = certification;
        }
    }

    @Getter
    public static abstract class RegisterDto {
        // 판매타입도 넣어야함.

        Long customerId;
        // 정규식, ~자 이내
        Long mainCategoryId;
        // 정규식, ~자 이내
        Long subCategoryId;
        // ~자 이내, 정규식
        String productName;
        // ~자 이내
        String description;
        // customerId + productname
        String image;
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
        // 0 ~ 99사이
        int personalizedDiscountRate;

        public RegisterDto(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate, int personalizedDiscountRate) {
            this.customerId = customerId;
            this.mainCategoryId = mainCategoryId;
            this.subCategoryId = subCategoryId;
            this.productName = productName;
            this.description = description;
            this.image = image;
            this.price = price;
            this.origin = origin;
            this.isPageVisibility = isPageVisibility;
            this.isRegularSale = isRegularSale;
            this.baseDiscountRate = baseDiscountRate;
            this.regularDiscountRate = regularDiscountRate;
            this.personalizedDiscountRate = personalizedDiscountRate;
        }
    }

    @Getter
    @Builder
    public static class ModifyDto {
        // ~자 이내, 정규식
        String productName;
        // ~자 이내
        String description;

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
        // 0 ~ 99사이
        int personalizedDiscountRate;

        // 이미지 , 상세이미지는 수정 안됨?
    }

    @Getter
    @Builder
    public static class Certification {
        // ~자 이내, 정규식
        String name;
        String serialNumber;
        // ~자 이내, 정규식
        String imageName;
    }

    public static interface CertifiedProduct {}

    public static interface NormalProduct {}
}
