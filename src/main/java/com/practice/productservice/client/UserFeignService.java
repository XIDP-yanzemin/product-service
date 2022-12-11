package com.practice.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8081/users")
public interface UserFeignService {

    //fix bug：need login
    //todo 不需要加 /client 和普通的 API 没有区别
    @RequestMapping(method = RequestMethod.GET, path = "/client/{id}")
    ListUserResponse getUserById(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.POST, path = "/client/query")
    List<ListUserResponse> getUsersByIdList(@RequestBody List<Long> idList);
}
