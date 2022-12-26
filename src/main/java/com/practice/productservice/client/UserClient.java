package com.practice.productservice.client;

import com.practice.productservice.controller.response.ListUserResponse;
import com.practice.productservice.interceptor.FeignInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8081/api/v1/users", configuration={FeignInterceptor.class})
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    ListUserResponse getUserById(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.POST, path = "/query")
    List<ListUserResponse> getUsersByIdList(@RequestBody List<Long> idList);
}
