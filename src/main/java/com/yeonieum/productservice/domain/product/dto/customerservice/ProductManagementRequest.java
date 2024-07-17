package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.SaleType;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ProductManagementRequest {
    public static interface CertifiedProduct {} public static interface NormalProduct {}
    @Getter
    @NoArgsConstructor
    public static class OfRegisterNormalProduct extends OfRegister implements NormalProduct {
        @Builder
        public OfRegisterNormalProduct(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate, int personalizedDiscountRate) {
            super(customerId, mainCategoryId, subCategoryId, productName, description, image, price, origin, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate, personalizedDiscountRate);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OfDeleteDetailImageList {
       private List<Long> detailImageList;
    }

    @Getter
    @NoArgsConstructor
    public static class OfRegisterEcoFriendlyProduct
            extends OfRegister implements CertifiedProduct {
        private Certification certification;
        @Builder
        public OfRegisterEcoFriendlyProduct(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate, int personalizedDiscountRate, Certification certification) {
            super(customerId, mainCategoryId, subCategoryId, productName, description, image, price, origin, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate, personalizedDiscountRate);
            this.certification = certification;
        }
    }

    @Getter
    @NoArgsConstructor
    public static abstract class OfRegister {
        // 판매타입도 넣어야함.
        private Long customerId;
        // 정규식, ~자 이내
        private Long mainCategoryId;
        // 정규식, ~자 이내
        private Long subCategoryId;
        // ~자 이내, 정규식
        private String productName;
        // ~자 이내
        private String description;
        // customerId + productname
        //private String image;
        // 0 이상 값
        private int price;
        // 빈문자열, 공백, null 허용 안됨
        private String origin;
        // 기본값 true
        private char isPageVisibility;
        // 기본값 true
        private char isRegularSale;
        // 0 ~ 99사이
        private int baseDiscountRate;
        // 0 ~ 99사이
        private int regularDiscountRate;
        // 0 ~ 99사이
        private int personalizedDiscountRate;

        public OfRegister(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate, int personalizedDiscountRate) {
            this.customerId = customerId;
            this.mainCategoryId = mainCategoryId;
            this.subCategoryId = subCategoryId;
            this.productName = productName;
            this.description = description;
            //this.image = image;
            this.price = price;
            this.origin = origin;
            this.isPageVisibility = isPageVisibility;
            this.isRegularSale = isRegularSale;
            this.baseDiscountRate = baseDiscountRate;
            this.regularDiscountRate = regularDiscountRate;
            this.personalizedDiscountRate = personalizedDiscountRate;
        }

        public Product toEntity(SaleType saleType, Customer customer, ProductDetailCategory productDetailCategory, String imageUrl) {
            return Product.builder()
                    .saleType(saleType)
                    .customer(customer)
                    .productDetailCategory(productDetailCategory)
                    .productName(this.getProductName())
                    .productPrice(this.getPrice())
                    .baseDiscountRate(this.getBaseDiscountRate())
                    .regularDiscountRate(this.getRegularDiscountRate())
                    .personalizedDiscountRate(this.getPersonalizedDiscountRate())
                    .productDescription(this.getDescription())
                    .productOrigin(this.getOrigin())
                    .productImage(imageUrl)
                    .isPageVisibility(ActiveStatus.fromCode(this.getIsPageVisibility()))
                    .isRegularSale(ActiveStatus.fromCode(this.getIsRegularSale()))
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OfModify {
        // ~자 이내, 정규식
        private String productName;
        // ~자 이내
        private String description;

        private int price;
        // 빈문자열, 공백, null 허용 안됨
        private String origin;
        // 기본값 true
        private char isPageVisibility;
        // 기본값 true
        private char isRegularSale;
        // 0 ~ 99사이
        private int baseDiscountRate;
        // 0 ~ 99사이
        private int regularDiscountRate;
        // 0 ~ 99사이
        private int personalizedDiscountRate;

        // 이미지 , 상세이미지는 수정 안됨?
    }

    @Getter
    @NoArgsConstructor
    public static class Certification {
        // ~자 이내, 정규식
        private String name;
        private String serialNumber;
        private MultipartFile image;
        // ~자 이내, 정규식

        public void setImage(MultipartFile image) {
            this.image = image;
        }
        public ProductCertification toEntity(Product product, String imageName) {
            return ProductCertification.builder()
                    .product(product)
                    .certificationImage(imageName)
                    .certificationName(this.getName())
                    .certificationNumber(this.getSerialNumber())
                    .build();
        }
    }
}
