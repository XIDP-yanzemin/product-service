package com.practice.productservice.controller;

import com.practice.productservice.aspect.LoginUser;
import com.practice.productservice.controller.request.AddProductRequest;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.controller.response.CommonPageModel;
import com.practice.productservice.controller.response.ProductResponseForPage;
import com.practice.productservice.dto.UserDto;
import com.practice.productservice.entity.ProductType;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CommonPageModel<ProductResponseForPage> listProductForPage(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) ProductType productType) {
        return productService.list(pageable, productType);
    }

    @GetMapping("/favorites")
    @ResponseStatus(HttpStatus.OK)
    public CommonPageModel<ProductResponseForPage> listFavoriteProducts(
            @PageableDefault Pageable pageable,
            @LoginUser UserDto userDto) {
        return productService.listFavoriteProducts(pageable, userDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(
            @LoginUser UserDto userDto,
            @RequestParam Long id) {
        productService.remove(userDto, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewProduct(
            @LoginUser UserDto userDto,
            @RequestBody @Valid AddProductRequest addProductRequest) {
        productService.add(userDto, addProductRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProductInfo(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest updateProductRequest) {
        productService.update(id, updateProductRequest);
    }

    @PostMapping("/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public void favoriteProduct(
            @LoginUser UserDto userDto,
            @RequestParam Long id) {
        productService.favorite(userDto, id);
    }

    @DeleteMapping("/favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteProductById(
            @LoginUser UserDto userDto,
            @RequestParam Long id) {
        productService.removeFavorite(userDto, id);
    }

    @PostMapping("/notification/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void buyProduct(@LoginUser UserDto userDto, @PathVariable Long productId) {
        productService.sendNotification(userDto, productId);
    }
}
