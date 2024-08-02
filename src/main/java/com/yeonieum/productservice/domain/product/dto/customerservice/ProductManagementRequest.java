package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.SaleType;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.validation.constraints.*;
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
        public OfRegisterNormalProduct(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate) {
            super(customerId, mainCategoryId, subCategoryId, productName, description, image, price, origin, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate);
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
        public OfRegisterEcoFriendlyProduct(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate, Certification certification) {
            super(customerId, mainCategoryId, subCategoryId, productName, description, image, price, origin, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate);
            this.certification = certification;
        }
    }

    @Getter
    @NoArgsConstructor
    public static abstract class OfRegister {

        private Long customerId;

        @NotNull(message = "상품 카테고리 설정은 필수입니다.")
        private Long mainCategoryId;

        @NotNull(message = "상품 상세 카테고리 설정은 필수입니다.")
        private Long subCategoryId;

        @NotBlank(message = "상품 이름 설정은 필수입니다.")
        @Size(max = 80, message = "상품 이름은 최대 80자까지 가능합니다.")
        private String productName;

        @NotBlank(message = "상품 설명은 필수입니다.")
        @Size(max = 300, message = "상품 설명은 최대 300자까지 가능합니다.")
        private String description;

        @NotNull(message = "상품 가격 설정은 필수입니다.")
        private int price;

        @NotBlank(message = "상품 원산지 설정은 필수입니다.")
        @Size(max = 30, message = "상품 설명은 최대 30자까지 가능합니다.")
        private String origin;

        @NotNull(message = "페이지노출여부 설정은 필수입니다.")
        private char isPageVisibility;

        @NotNull(message = "정기판매여부 설정은 필수입니다.")
        private char isRegularSale;

        @Min(value = 0, message = "기본 할인율은 0% 이상이어야 합니다.")
        @Max(value = 99, message = "기본 할인율은 99% 이하이어야 합니다.")
        private int baseDiscountRate;

        @Min(value = 0, message = "정기 할인율은 0% 이상이어야 합니다.")
        @Max(value = 99, message = "정기 할인율은 99% 이하이어야 합니다.")
        private int regularDiscountRate;

        public OfRegister(Long customerId, Long mainCategoryId, Long subCategoryId, String productName, String description, String image, int price, String origin, char isPageVisibility, char isRegularSale, int baseDiscountRate, int regularDiscountRate) {
            this.customerId = customerId;
            this.mainCategoryId = mainCategoryId;
            this.subCategoryId = subCategoryId;
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.origin = origin;
            this.isPageVisibility = isPageVisibility;
            this.isRegularSale = isRegularSale;
            this.baseDiscountRate = baseDiscountRate;
            this.regularDiscountRate = regularDiscountRate;
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

        @NotBlank(message = "상품 이름 설정은 필수입니다.")
        @Size(max = 80, message = "상품 이름은 최대 80자까지 가능합니다.")
        private String productName;

        @NotBlank(message = "상품 설명은 필수입니다.")
        @Size(max = 300, message = "상품 이름은 최대 300자까지 가능합니다.")
        private String description;

        @NotNull(message = "가격 설정은 필수입니다.")
        private int price;

        @NotBlank(message = "상품 원산지 설정은 필수입니다.")
        @Size(max = 30, message = "상품 이름은 최대 30자까지 가능합니다.")
        private String origin;

        @NotNull(message = "페이지노출여부 설정은 필수입니다.")
        private char isPageVisibility;

        @NotNull(message = "정기판매여부 설정은 필수입니다.")
        private char isRegularSale;

        @Min(value = 0, message = "기본 할인율은 0% 이상이어야 합니다.")
        @Max(value = 99, message = "기본 할인율은 99% 이하이어야 합니다.")
        private int baseDiscountRate;

        @Min(value = 0, message = "정기 할인율은 0% 이상이어야 합니다.")
        @Max(value = 99, message = "정기 할인율은 99% 이하이어야 합니다.")
        private int regularDiscountRate;
    }

    @Getter
    @NoArgsConstructor
    public static class Certification {

        @NotBlank(message = "친환경 인증서 첨부는 필수입니다.")
        @Size(max = 30, message = "친환경 인증서 이름은 최대 30자까지 가능합니다.")
        private String name;

        @NotBlank(message = "친환경 인증서 번호는 필수입니다.")
        @Size(max = 30, message = "친환경 인증서 번호는 최대 30자까지 가능합니다.")
        private String serialNumber;
        private MultipartFile image;

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
