package com.yeonieum.productservice.domain.cart.dto;

import lombok.Builder;
import lombok.Getter;

public class CartProductRequest {

    @Getter
    @Builder
    public static class RegisterProductCartDto{

        private Long productId;
        private Long productCartId;
        private String memberId;
        private int quantity;
    }
}
