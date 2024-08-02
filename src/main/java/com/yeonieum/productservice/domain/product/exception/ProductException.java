package com.yeonieum.productservice.domain.product.exception;

import com.yeonieum.productservice.global.exceptions.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ProductException extends CustomException {

    public ProductException(ProductExceptionCode productExceptionCode, HttpStatus status) {
        super(productExceptionCode, status);
    }
}
