package com.practice.productservice.controller;

import com.practice.productservice.dto.ListProductsResponse;
import com.practice.productservice.service.ProductService;
import com.practice.productservice.entity.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ListProductsResponse getProducts(
            // TODO: fix first page equals 0
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Type type) {
        return productService.listProductByRequest(pageable, type);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    // TODO: 2022/12/3 重新看一下 RESTful CRUD 的 uri 是什么，以及对应的 Response status 是什么
    public void removeProduct(@RequestParam Long id){
        productService.delete(id);
    }

}
