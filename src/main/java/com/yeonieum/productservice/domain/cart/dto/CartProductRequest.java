package com.yeonieum.productservice.domain.cart.dto;

import com.yeonieum.productservice.domain.cart.entity.CartProduct;
import com.yeonieum.productservice.domain.cart.entity.CartType;
import com.yeonieum.productservice.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CartProductRequest {

    @Getter
    @NoArgsConstructor
    public static class OfRegisterProductCart {

        private Long productId;
        private Long cartTypeId;
        private String memberId;
        private int quantity;

        public CartProduct toEntity(Product product, CartType cartType, String memberId) {
            return CartProduct.builder()
                    .product(product)
                    .cartType(cartType)
                    .memberId(memberId)
                    .quantity(this.quantity)
                    .build();
        }
    }
}
