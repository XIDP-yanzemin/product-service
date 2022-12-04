package com.practice.productservice.controller;

import com.practice.productservice.entity.Type;
import com.practice.productservice.request.AddProductRequest;
import com.practice.productservice.response.ListProductsResponse;
import com.practice.productservice.response.UploadImageResponse;
import com.practice.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(@PathVariable Long id){
        productService.delete(id);
    }

    @PostMapping("/image")
    public UploadImageResponse uploadImage(HttpServletRequest request,
                                           @RequestParam("image") MultipartFile[] files) {
        return productService.upload(request, files);
    }
    @PostMapping
    public void submitProductInfo(@RequestBody @Valid AddProductRequest addProductRequest){
        productService.addNewProduct(addProductRequest);
    }
}
