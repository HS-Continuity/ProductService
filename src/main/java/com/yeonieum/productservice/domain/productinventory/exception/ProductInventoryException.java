package com.yeonieum.productservice.domain.productinventory.exception;

import com.yeonieum.productservice.global.exceptions.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ProductInventoryException extends CustomException {

    public ProductInventoryException(ProductInventoryExceptionCode productInventoryExceptionCode, HttpStatus status) {
        super(productInventoryExceptionCode, status);
    }
}
