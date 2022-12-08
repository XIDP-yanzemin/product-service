package com.practice.productservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // image exception
    IMAGE_EMPTY_EXCEPTION("image should not be empty."),

    IMAGE_SIZE_EXCEPTION("image size should not exceed 10MB."),

    IMAGE_TYPE_EXCEPTION("image type is invalid."),

    IMAGE_STATE_EXCEPTION("image state exception."),

    IMAGE_UPLOAD_EXCEPTION("image upload fails."),

    // product exception
    PRODUCT_NOT_FOUND("product not exists.");

    public final String message;
}
