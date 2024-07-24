package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

public class ProductManagementResponse {

    @Getter
    @Builder
    public static class OfOrderInformation {
        private Long productId;
        private String productName;
        private String name;
        private int originPrice;
        private int regularDiscountRate;
        private double finalPrice;
        private int quantity;
        @Builder.Default
        private boolean isAvailable = true;

        public void changeIsAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
        }

        public static OfOrderInformation convertedBy(Product product, int quantity) {
            return OfOrderInformation.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .name(product.getProductName())
                    .originPrice(product.getProductPrice())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .finalPrice(quantity * product.getProductPrice() * product.getRegularDiscountRate())
                    .quantity(quantity)
                    .build();
        }
    }



    @Getter
    @Builder
    public static class OfRetrieveProductOrder {
        private Long productId;
        private String productName;
        private String productImage;

        public static OfRetrieveProductOrder convertedBy(Product product) {
            return OfRetrieveProductOrder.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productImage(product.getProductImage())
                    .build();
        }
    }
    @Getter
    @Builder
    public static class OfRetrieve {
        private ActiveStatus isEcoFriendly;
        private String productName;
        // ~자 이내
        private String description;
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
        // 0 ~ 99사
        private int personalizedDiscountRate;

        public static OfRetrieve convertedBy(Product product) {
            return ProductManagementResponse.OfRetrieve.builder()
                    .isEcoFriendly(product.getIsCertification())
                    .productName(product.getProductName())
                    .description(product.getProductDescription())
                    .price(product.getProductPrice())
                    .origin(product.getProductOrigin())
                    .personalizedDiscountRate(product.getPersonalizedDiscountRate())
                    .baseDiscountRate(product.getBaseDiscountRate())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .isPageVisibility(product.getIsPageVisibility().getCode())
                    .isRegularSale(product.getIsRegularSale().getCode())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class OfRetrieveDetails {
        private String categoryName;
        private String detailCategoryName;
        private String productName;
        private String salesType;
        // ~자 이내
        private String description;
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
        // 0 ~ 99사
        private int personalizedDiscountRate;

        public static OfRetrieveDetails convertedBy(Product product) {

            return OfRetrieveDetails.builder()
                    .categoryName(product.getProductDetailCategory().getProductCategory().getCategoryName())
                    .detailCategoryName(product.getProductDetailCategory().getDetailCategoryName())
                    .salesType(product.getSaleType().getTypeName())
                    .productName(product.getProductName())
                    .description(product.getProductDescription())
                    .price(product.getProductPrice())
                    .origin(product.getProductOrigin())
                    .personalizedDiscountRate(product.getPersonalizedDiscountRate())
                    .baseDiscountRate(product.getBaseDiscountRate())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .isPageVisibility(product.getIsPageVisibility().getCode())
                    .isRegularSale(product.getIsRegularSale().getCode())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class OfRetrieveDetailImage {
        private Long productDetailImageId;
        private Long productId;
        private String imageName;

        public static OfRetrieveDetailImage convertedBy(ProductDetailImage detailImage, Long productId) {
            return ProductManagementResponse.OfRetrieveDetailImage.builder()
                    .imageName(detailImage.getDetailImage())
                    .productId(productId)
                    .productDetailImageId(detailImage.getProductDetailImageId())
                    .build();
        }
    }
}
