package com.yeonieum.productservice.domain.review.exception;

import com.yeonieum.productservice.global.exceptions.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ProductReviewException extends CustomException {

    public ProductReviewException(ProductReviewExceptionCode productReviewExceptionCode, HttpStatus status) {
        super(productReviewExceptionCode, status);
    }
}
