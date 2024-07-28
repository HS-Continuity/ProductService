package com.yeonieum.productservice.domain.category.exception;

import com.yeonieum.productservice.global.exceptions.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CategoryException extends CustomException {
    public CategoryException(CategoryExceptionCode categoryExceptionCode, HttpStatus status) {
        super(categoryExceptionCode, status);
    }
}
