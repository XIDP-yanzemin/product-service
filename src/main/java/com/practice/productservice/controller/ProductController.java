package com.practice.productservice.controller;

import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import com.practice.productservice.request.AddProductRequest;
import com.practice.productservice.request.UpdateProductRequest;
import com.practice.productservice.response.CommonPageModel;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public CommonPageModel listProductForPage(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Type type) {
        return productService.list(pageable, type);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(@PathVariable Long id) {
        productService.remove(id);
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadImageResponse uploadImage(HttpServletRequest request,
                                           @RequestParam("image") MultipartFile[] files) {
        return productService.upload(request, files);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewProduct(
            @RequestHeader String token,
            @RequestBody @Valid AddProductRequest addProductRequest) {
        productService.add(token, addProductRequest);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Product updateProductInfo(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest updateProductRequest) {
        return productService.update(id, updateProductRequest);
    }
}
