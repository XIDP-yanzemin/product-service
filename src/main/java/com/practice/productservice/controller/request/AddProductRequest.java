package com.practice.productservice.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AddProductRequest extends BaseProductRequest{

    private List<String> url;
}
