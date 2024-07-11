package com.yeonieum.productservice.domain.product.dto.memberservice;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ProductShoppingResponse {

    @Getter
    @Builder
    public static class RetrieveDetailCategoryWithProductsDto {

        private Long productDetailCategoryId;
        private String detailCategoryName;
        private int shelfLifeDay;
        private List<SearchProductInformationDto> searchProductInformationDtoList;
        private int totalItems;
        private int totalPages;
        private boolean lastPage;
    }

    @Getter
    @Builder
    public static class RetrieveCategoryWithProductsDto {

        private Long productCategoryId;
        private String categoryName;
        private List<SearchProductInformationDto> searchProductInformationDtoList;
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
        private List<SearchProductInformationDto> searchProductInformationDtoList;
    }

    @Getter
    @Builder
    public static class SearchProductInformationDto {

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
        private int reviewCount;
        private double averageScore;
        @Builder.Default
        private boolean isSoldOut = false;

        public void changeIsSoldOut(boolean isSoldOut){
            this.isSoldOut = isSoldOut;
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
        private int reviewCount;
        private double averageScore;
    }
}
