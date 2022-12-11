package com.practice.productservice.constant;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Constant {

    public static final Instant INSTANT_NOW_AT_CURRENT_TIME_ZONE = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));

    public static final List<String> IMAGE_TYPES = new ArrayList<>();
    public static final String SELL_SUBJECT = "Someone is interested in selling you the item";
    public static final String SELL_EMAIL_BODY = "Good news! Someone is interested in selling you the item you want. Please contact the seller through this email: ";
    public static final String BUY_SUBJECT = "Someone is interested in buying your item";
    public static final String BUY_EMAIL_BODY = "Good news! Someone is interested in buying your item. Please contact the buyer through this email: ";
    public static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiT1JESU5BUllfVVNFUiIsImlkIjoyLCJleHAiOjE2NzExMTM2NDd9.3WwL4gcCbhwGrV38AdtkPCjcpV3wJpsNGTLYQvbB3Dk";

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
