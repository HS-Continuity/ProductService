package com.yeonieum.productservice.domain.review.exception;

import com.yeonieum.productservice.global.exceptions.code.CustomExceptionCode;

public enum ProductReviewExceptionCode implements CustomExceptionCode {

    REVIEW_ALREADY_EXISTS(6000, "이미 해당 회원이 작성한 리뷰가 존재합니다."),
    PRODUCT_REVIEW_NOT_FOUND(6001, "존재하지 않는 상품리뷰 ID 입니다.");

    private final int code;
    private final String message;

    ProductReviewExceptionCode(int code, String message) {
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
