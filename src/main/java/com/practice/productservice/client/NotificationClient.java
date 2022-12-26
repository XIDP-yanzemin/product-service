package com.practice.productservice.client;

import com.practice.productservice.controller.request.SendEmailRequest;
import com.practice.productservice.interceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "notification-service",
        url = "http://localhost:8082/api/v1/email",
        configuration = {FeignInterceptor.class})
public interface NotificationClient {

    @RequestMapping(method = RequestMethod.POST)
    void sendEmail(@RequestBody SendEmailRequest sendEmailRequest);
}
