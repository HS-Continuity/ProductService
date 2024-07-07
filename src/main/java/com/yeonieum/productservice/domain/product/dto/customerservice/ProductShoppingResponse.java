package com.yeonieum.productservice.domain.product.dto.customerservice;

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
    }

    @Getter
    @Builder
    public static class SearchProductInformationDto {

        private Long productId;
        private String productName;
        private String productDescription;
        private String productImage;
        private int productPrice;
        private char isRegularSale;
    }
}
