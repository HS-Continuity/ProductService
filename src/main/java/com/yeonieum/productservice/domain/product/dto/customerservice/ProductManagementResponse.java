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
        private int regularPrice;
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
                    .regularPrice(product.getCalculatedRegularPrice())
                    .finalPrice(product.getCalculatedRegularPrice())
                    .quantity(quantity)
                    .build();
        }
    }



    @Getter
    @Builder
    public static class OfRetrieveProductOrder { // 정기주문용 조회임
        private Long productId;
        private String productName;
        private String productImage;
        private String storeName;
        int originPrice;
        int finalPrice;

        public static OfRetrieveProductOrder convertedBy(Product product) {
            return OfRetrieveProductOrder.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productImage(product.getProductImage())
                    .originPrice(product.getProductPrice())
                    .finalPrice(product.getCalculatedRegularPrice())
                    .storeName(product.getCustomer().getStoreName())
                    .build();
        }
    }
    @Getter
    @Builder
    public static class OfRetrieve {

        private Long productId; //상품 ID

        private String detailCategoryName; //상세 카테고리 이름

        private ActiveStatus isEcoFriendly; //친환경상품 유무

        private String productName;//상품 이름
        // ~자 이내
        private String description; //상품 상세 설명
        // 0 이상 값
        private int price; //상품 가격

        // 빈문자열, 공백, null 허용 안됨
        private String origin; //상품 원산지
        // 기본값 true
        private ActiveStatus isPageVisibility; //페이지 노출 여부
        // 기본값 true
        private ActiveStatus isRegularSale; //정기주문 할인 가격
        // 0 ~ 99사이
        private int baseDiscountRate;//기본 할인율
        // 0 ~ 99사이
        private int regularDiscountRate;//정기 구매 할인율

        public static OfRetrieve convertedBy(Product product) {
            return OfRetrieve.builder()
                    .productId(product.getProductId())
                    .detailCategoryName(product.getProductDetailCategory().getDetailCategoryName())
                    .isEcoFriendly(product.getIsCertification())
                    .productName(product.getProductName())
                    .description(product.getProductDescription())
                    .price(product.getProductPrice())
                    .origin(product.getProductOrigin())
                    .baseDiscountRate(product.getBaseDiscountRate())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .isPageVisibility(product.getIsPageVisibility())
                    .isRegularSale(product.getIsRegularSale())
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

        private String image;

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
                    .image(product.getProductImage())
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

    @Getter
    @Builder
    public static class ProductOrderCount  {
        private Long productId;
        private Long orderCount;
    }

    @Getter
    @Builder
    public static class OfStatisticsProduct {
        private Long productId;
        private String productName;
        private String categoryName;
        private String image;
        private Long orderCount;
        private double averageScore;
    }
}
