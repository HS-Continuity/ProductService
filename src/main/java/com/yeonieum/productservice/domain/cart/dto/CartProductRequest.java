package com.yeonieum.productservice.domain.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class CartProductRequest {

    @Getter
    @NoArgsConstructor
    public static class RegisterProductCartDto{

        private Long productId;
        private Long cartTypeId;
        private String memberId;
        private int quantity;
    }
}
