package com.practice.productservice.exception;

import javax.security.sasl.AuthenticationException;

public class UserAuthenticationException extends AuthenticationException {
    public UserAuthenticationException(ErrorCode errorCode){
        super(errorCode.getMessage());
    }
}
