package com.practice.productservice.exception;

public class ProductNotFound extends RuntimeException{
    public ProductNotFound(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
