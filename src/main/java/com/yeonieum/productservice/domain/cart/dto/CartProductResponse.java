package com.yeonieum.productservice.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;

public class CartProductResponse {

    @Getter
    @Builder
    public static class RetrieveAllCartProduct{

        private Long cartProductId;
        private Long customerId;
        private String storeName;
        private Long productId;
        private String productName;
        private String productDescription;
        private String productImage;
        private int productPrice;
        private int quantity;
    }
}
