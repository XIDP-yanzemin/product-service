package com.practice.productservice.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListUserResponse {
    private Long id;

    private String username;

    private String email;

    private String cellphone;

    private String address;
}
