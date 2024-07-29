package com.yeonieum.productservice.domain.category.exception;

import com.yeonieum.productservice.global.exceptions.code.CustomExceptionCode;

public enum CategoryExceptionCode implements CustomExceptionCode {

    CATEGORY_NOT_FOUND(1300, "존재하지 않는 카테고리 ID 입니다."),
    CATEGORY_NAME_ALREADY_EXISTS(1400, "이미 존재하는 카테고리 이름입니다."),
    DETAIL_CATEGORY_NOT_FOUND(1500,"존재하지 않는 상세 카테고리 ID 입니다."),
    DETAIL_CATEGORY_NAME_ALREADY_EXISTS(1600, "이미 존재하는 상세 카테고리 이름입니다.");

    private final int code;
    private final String message;

    CategoryExceptionCode(int code, String message) {
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
