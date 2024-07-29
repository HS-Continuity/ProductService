package com.yeonieum.productservice.domain.productinventory.exception;

import com.yeonieum.productservice.global.exceptions.code.CustomExceptionCode;

public enum ProductInventoryExceptionCode implements CustomExceptionCode {

    PRODUCT_INVENTORY_NOT_FOUND(5000, "해당하는 상품재고가 존재하지 않습니다.");

    private final int code;
    private final String message;

    ProductInventoryExceptionCode(int code, String message) {
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

