package com.yeonieum.productservice.domain.product.exception;

import com.yeonieum.productservice.global.exceptions.code.CustomExceptionCode;

public enum ProductExceptionCode implements CustomExceptionCode {

    PRODUCT_NOT_FOUND(4000, "존재하지 않는 상품 ID 입니다."),
    PRODUCT_ADVERTISEMENT_NOT_FOUNT(4001, "해당하는 신청 건이 존재하지 않습니다."),
    PRODUCT_ADVERTISEMENT_CANNOT_BE_CANCELED(4002, "취소할 수 없는 상태입니다."),
    PRODUCT_ADVERTISEMENT_CANNOT_BE_APPROVE(4003, "승인할 수 없는 상태입니다."),
    NOT_PRODUCT_OWNER(4004, "해당 상품의 소유자가 아닙니다."),
    PRODUCT_TIME_SALE_NOT_FOUNT(4005, "해당하는 신청 건이 존재하지 않습니다."),
    PRODUCT_TIME_SALE_CANNOT_BE_CANCELED(4006, "취소할 수 없는 상태입니다."),
    NO_PRODUCTS_IN_CATEGORY(4007, "해당 카테고리 내의 상품이 없습니다."),
    NO_PRODUCTS_IN_DETAIL_CATEGORY(4008, "해당 상세 카테고리 내의 상품이 없습니다."),
    NO_SEARCH_RESULTS(4009, "검색 결과가 없습니다. 다른 검색어를 입력해 주세요.");

    private final int code;
    private final String message;

    ProductExceptionCode(int code, String message) {
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
