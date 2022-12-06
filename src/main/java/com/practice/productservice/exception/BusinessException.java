package com.practice.productservice.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(ErrorCode errorCode){
        super(errorCode.message);
    }
}
