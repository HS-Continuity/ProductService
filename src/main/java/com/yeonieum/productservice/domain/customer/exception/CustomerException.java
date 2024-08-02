package com.yeonieum.productservice.domain.customer.exception;

import com.yeonieum.productservice.global.exceptions.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CustomerException extends CustomException {
    public CustomerException(CustomerExceptionCode customerExceptionCode, HttpStatus status) {
        super(customerExceptionCode, status);
    }
}
