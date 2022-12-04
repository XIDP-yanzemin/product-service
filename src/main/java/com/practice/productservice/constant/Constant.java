package com.practice.productservice.constant;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final List<String> IMAGE_TYPES = new ArrayList<>();

    static {
        IMAGE_TYPES.add("image/jpeg");
        IMAGE_TYPES.add("image/jpg");
        IMAGE_TYPES.add("image/png");
        IMAGE_TYPES.add("image/bmp");
        IMAGE_TYPES.add("image/gif");
        IMAGE_TYPES.add("image/pjpeg");
        IMAGE_TYPES.add("image/x-png");

    }
}
