package com.yeonieum.productservice.domain.product.exception;

import com.yeonieum.productservice.global.exceptions.code.CustomExceptionCode;

public enum ProductExceptionCode implements CustomExceptionCode {

    PRODUCT_NOT_FOUND(1800, "존재하지 않는 상품 ID 입니다."),
    PRODUCT_ADVERTISEMENT_NOT_FOUNT(1900, "해당하는 신청 건이 존재하지 않습니다."),
    PRODUCT_ADVERTISEMENT_CANNOT_BE_CANCELED(2000, "취소할 수 없는 상태입니다."),
    PRODUCT_ADVERTISEMENT_CANNOT_BE_APPROVE(2100, "승인할 수 없는 상태입니다."),
    NOT_PRODUCT_OWNER(2200, "해당 상품의 소유자가 아닙니다."),
    PRODUCT_TIME_SALE_NOT_FOUNT(2300, "해당하는 신청 건이 존재하지 않습니다."),
    PRODUCT_TIME_SALE_CANNOT_BE_CANCELED(2400, "취소할 수 없는 상태입니다."),
    NO_PRODUCTS_IN_CATEGORY(2500, "해당 카테고리 내의 상품이 없습니다."),
    NO_PRODUCTS_IN_DETAIL_CATEGORY(2600, "해당 상세 카테고리 내의 상품이 없습니다."),
    NO_SEARCH_RESULTS(2700, "검색 결과가 없습니다. 다른 검색어를 입력해 주세요.");

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
