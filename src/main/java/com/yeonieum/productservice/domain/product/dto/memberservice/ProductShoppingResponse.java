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
    public static class SearchProductInformationDto {

        private Long productId;
        private String productName;
        private String productDescription;
        private String productImage;
        private int baseDiscountRate;
        private int regularDiscountRate;
        private int productPrice;
        private int calculatedBasePrice;
        private char isRegularSale;
    }
}
