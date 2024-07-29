package com.yeonieum.productservice.domain.customer.exception;

import com.yeonieum.productservice.global.exceptions.code.CustomExceptionCode;

public enum CustomerExceptionCode implements CustomExceptionCode {

    CUSTOMER_NOT_FOUND(3000, "존재하지 않는 고객 ID 입니다.");

    private final int code;
    private final String message;

    CustomerExceptionCode(int code, String message) {
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
