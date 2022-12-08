package com.practice.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "user-service", url = "http://localhost:8081/users")
public interface UserFeignService {

    //fix bug：need login
    @RequestMapping(method = RequestMethod.GET, path = "/client/{id}")
    ListUserResponse getUserById(@PathVariable Long id);
}
