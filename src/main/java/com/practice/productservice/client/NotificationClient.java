package com.practice.productservice.client;

import com.practice.productservice.controller.request.SendEmailRequest;
import com.practice.productservice.interceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service",
        configuration = {FeignInterceptor.class})
public interface NotificationClient {

    @PostMapping("/api/v1/notification/product")
    void sendEmail(@RequestBody SendEmailRequest sendEmailRequest);
}
