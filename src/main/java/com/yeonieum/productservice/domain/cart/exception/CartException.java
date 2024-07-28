package com.yeonieum.productservice.domain.cart.exception;

import com.yeonieum.productservice.global.exceptions.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CartException extends CustomException {
    public CartException(CartExceptionCode cartExceptionCode, HttpStatus status) {
        super(cartExceptionCode, status);
    }
}
