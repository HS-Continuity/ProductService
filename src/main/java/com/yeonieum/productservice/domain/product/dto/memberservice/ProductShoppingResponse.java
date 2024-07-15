package com.yeonieum.productservice.domain.product.dto.memberservice;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ProductShoppingResponse {

    @Getter
    @Builder
    public static class OfRetrieveDetailCategoryWithProduct {

        private Long productDetailCategoryId;
        private String detailCategoryName;
        private int shelfLifeDay;
        @Builder.Default
        private List<OfSearchProductInformation> searchProductInformationDtoList = new ArrayList<>();

        public void changeOfSearchProductInformationList(List<OfSearchProductInformation> OfSearchProductInformation){
            this.searchProductInformationDtoList = OfSearchProductInformation;
        }

        public static OfRetrieveDetailCategoryWithProduct convertedBy(ProductDetailCategory detailCategory) {
            return OfRetrieveDetailCategoryWithProduct.builder()
                    .productDetailCategoryId(detailCategory.getProductDetailCategoryId())
                    .detailCategoryName(detailCategory.getDetailCategoryName())
                    .shelfLifeDay(detailCategory.getShelfLifeDay())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RetrieveCategoryWithProductsDto {

        private Long productCategoryId;
        private String categoryName;
        private List<OfSearchProductInformation> searchProductInformationDtoList;
        private int totalItems;
        private int totalPages;
        private boolean lastPage;
    }

    @Getter
    @Builder
    public static class RetrieveSearchWithProductsDto {

        private int totalItems;
        private int totalPages;
        private boolean lastPage;
        private List<OfSearchProductInformation> searchProductInformationDtoList;
    }

    @Getter
    @Builder
    public static class OfSearchProductInformation {

        private Long productId;
        private Long customerId;
        private Long detailCategoryId;
        private String productName;
        private String productDescription;
        private String productImage;
        private int baseDiscountRate;
        private int regularDiscountRate;
        private int productPrice;
        private int calculatedBasePrice;
        private char isRegularSale;
        private Integer reviewCount;
        private Double averageScore;
        @Builder.Default
        private boolean isSoldOut = false;

        public void changeIsSoldOut(boolean isSoldOut){
            this.isSoldOut = isSoldOut;
        }

        public static OfSearchProductInformation convertedBy(Product product) {
            return OfSearchProductInformation.builder()
                    .productId(product.getProductId())
                    .customerId(product.getCustomer().getCustomerId())
                    .detailCategoryId(product.getProductDetailCategory().getProductDetailCategoryId())
                    .productName(product.getProductName())
                    .productDescription(product.getProductDescription())
                    .productImage(product.getProductImage())
                    .baseDiscountRate(product.getBaseDiscountRate())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .productPrice(product.getProductPrice())
                    .calculatedBasePrice(product.getCalculatedBasePrice())
                    .isRegularSale(product.getIsRegularSale().getCode())
                    .reviewCount(product.getReviewCount())
                    .averageScore(product.getAverageScore())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DetailProductInformationDto {

        private Long productId;
        private Long customerId;
        private Long detailCategoryId;
        private String productName;
        private String productDescription;
        private String productImage;
        private String origin;
        private int baseDiscountRate;
        private int regularDiscountRate;
        private int personalizedDiscountRate;
        private int productPrice;
        private int calculatedBasePrice;
        private int calculatedRegularPrice;
        private int calculatedPersonalizedPrice;
        private char isRegularSale;
        private char isCertification;
        private Integer reviewCount;
        private Double averageScore;
    }
}
