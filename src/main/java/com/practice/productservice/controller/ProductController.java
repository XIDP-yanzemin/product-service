package com.practice.productservice.controller;

import com.practice.productservice.entity.Type;
import com.practice.productservice.request.AddProductRequest;
import com.practice.productservice.request.BaseProductRequest;
import com.practice.productservice.request.UpdateProductRequest;
import com.practice.productservice.response.CommonPageModel;
import com.practice.productservice.response.ProductResponseForPage;
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

import javax.validation.Valid;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CommonPageModel<ProductResponseForPage> listProductForPage(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Type type) {
        return productService.list(pageable, type);
    }

    @GetMapping("/favorites")
    @ResponseStatus(HttpStatus.OK)
    public CommonPageModel<ProductResponseForPage> listFavoriteProducts(
            @PageableDefault Pageable pageable,
            @LoginUser UserDto userDto) {
        return productService.listFavoriteProducts(pageable, userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(
            @LoginUser UserDto userDto,
            @PathVariable Long id) {
        productService.remove(userDto, id);
    }

    @PostMapping("/sell-item")
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewProduct(
            @LoginUser UserDto userDto,
            @RequestBody @Valid AddProductRequest addProductRequest) {
        productService.add(userDto, addProductRequest);
    }

    @PostMapping("/buy-item")
    @ResponseStatus(HttpStatus.CREATED)
    public void addWantToBuyProduct(
            @RequestHeader String token,
            @RequestBody BaseProductRequest baseProductRequest
    ) {
        productService.addWantToBuyProduct(token, baseProductRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProductInfo(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest updateProductRequest) {
        productService.update(id, updateProductRequest);
    }

    @PostMapping("/add-favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public void favoriteProduct(
            @RequestHeader String token,
            @RequestParam Long id) {
        productService.favorite(token, id);
    }

    @DeleteMapping("/remove-favorites")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteProductById(
            @RequestHeader String token,
            @RequestParam Long id) {
        productService.removeFavorite(token, id);
    }


    @PostMapping("/buy-item/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void buyProduct(@LoginUser UserDto userDto, @PathVariable Long productId) {
        productService.buyProduct(userDto, productId);
    }

    @PostMapping("/sell-item/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void sellProduct(@LoginUser UserDto userDto, @PathVariable Long productId) {
        productService.sellProduct(userDto, productId);
    }

}
