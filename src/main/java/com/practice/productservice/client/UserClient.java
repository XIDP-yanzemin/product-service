package com.practice.productservice.client;

import com.practice.productservice.controller.response.ListUserResponse;
import com.practice.productservice.interceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", configuration={FeignInterceptor.class})
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    ListUserResponse getUserById(@PathVariable Long id);

    @PostMapping("/api/v1/users/query")
    List<ListUserResponse> getUsersByIdList(@RequestBody List<Long> idList);
}
