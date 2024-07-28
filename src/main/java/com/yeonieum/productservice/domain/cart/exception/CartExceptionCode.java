package com.yeonieum.productservice.domain.cart.exception;

import com.yeonieum.productservice.global.exceptions.code.CustomExceptionCode;

public enum CartExceptionCode implements CustomExceptionCode {
    CART_NOT_FOUND(1000, "존재하지 않는 장바구니타입 ID 입니다.");

    private final int code;
    private final String message;

    CartExceptionCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
