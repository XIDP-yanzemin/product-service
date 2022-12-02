package com.practice.productservice.exception;

public class ProductNotFound extends RuntimeException{
    public ProductNotFound() {
        super("product not exists.");
    }
}
