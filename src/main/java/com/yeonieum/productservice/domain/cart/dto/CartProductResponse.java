package com.yeonieum.productservice.domain.cart.dto;

import com.yeonieum.productservice.domain.cart.entity.CartProduct;
import lombok.Builder;
import lombok.Getter;

public class CartProductResponse {

    @Getter
    @Builder
    public static class OfRetrieveCartProduct {

        private Long cartProductId;
        private Long customerId;
        private String storeName;
        private Long productId;
        private String productName;
        private String productDescription;
        private String productImage;
        private int productPrice;
        private int quantity;
        @Builder.Default
        private boolean isSoldOut = false;

        public void changeIsSoldOut(boolean isSoldOut){
            this.isSoldOut = isSoldOut;
        }

        public static CartProductResponse.OfRetrieveCartProduct convertedBy(CartProduct cartProduct, int finalPrice) {
            return OfRetrieveCartProduct.builder()
                    .cartProductId(cartProduct.getCartProductId())
                    .customerId(cartProduct.getProduct().getCustomer().getCustomerId())
                    .storeName(cartProduct.getProduct().getCustomer().getStoreName())
                    .productId(cartProduct.getProduct().getProductId())
                    .productName(cartProduct.getProduct().getProductName())
                    .productDescription(cartProduct.getProduct().getProductDescription())
                    .productImage(cartProduct.getProduct().getProductImage())
                    .productPrice(finalPrice)
                    .quantity(cartProduct.getQuantity())
                    .build();
        }
    }
}
