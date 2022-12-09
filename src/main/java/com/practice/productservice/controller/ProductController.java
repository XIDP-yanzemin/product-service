package com.practice.productservice.controller;

import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import com.practice.productservice.request.AddProductRequest;
import com.practice.productservice.request.BaseProductRequest;
import com.practice.productservice.request.UpdateProductRequest;
import com.practice.productservice.response.CommonPageModel;
import com.practice.productservice.response.ProductResponseForPage;
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
    public CommonPageModel<ProductResponseForPage> listProductForPage(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Type type) {
        return productService.list(pageable, type);
    }

    @GetMapping("/favorites")
    @ResponseStatus(HttpStatus.OK)
    public CommonPageModel<ProductResponseForPage> listFavoriteProducts(
            @PageableDefault Pageable pageable,
            @RequestHeader String token) {
        return productService.listFavoriteProducts(pageable, token);
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

    @PostMapping("/sell-item")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseForPage addNewProduct(
            @RequestHeader String token,
            @RequestBody @Valid AddProductRequest addProductRequest) {
        return productService.add(token, addProductRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    // todo; support update image?
    public Product updateProductInfo(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest updateProductRequest) {
        return productService.update(id, updateProductRequest);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void favoriteProduct(
            @RequestHeader String token,
            @PathVariable Long id) {
        productService.favorite(token, id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavoriteProductById(
            @RequestHeader String token,
            @RequestParam Long id) {
        productService.removeFavorite(token, id);
    }

    @PostMapping("/buy-item")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseForPage addWantToBuyProduct(
            @RequestHeader String token,
            @RequestBody BaseProductRequest baseProductRequest
    ) {
        return productService.wantToBuy(token, baseProductRequest);
    }


    @PostMapping("/buy-item/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void buyProduct(@RequestHeader String token, @PathVariable Long productId) {
        productService.buyProduct(token, productId);
    }

    @PostMapping("/sell-item/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void sellProduct(@RequestHeader String token, @PathVariable Long productId) {
        productService.sellProduct(token, productId);
    }

}
