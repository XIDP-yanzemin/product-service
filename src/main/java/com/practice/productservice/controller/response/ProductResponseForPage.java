package com.practice.productservice.controller.response;

import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseForPage {

    private Long id;

    private Long userId;

    private String userName;

    private String email;

    private String cellphone;

    private String address;

    private String productName;

    private BigDecimal price;

    private String description;

    private Integer amount;

    private ProductType productType;

    private List<Image> imageList;

    public static ProductResponseForPage buildProductResponseForPageFrom(Product product,
                                                                         ListUserResponse user) {
        return builder()
                .id(product.getId())
                .userId(user.getId())
                .userName(user.getUsername())
                .email(user.getEmail())
                .cellphone(user.getCellphone())
                .address(user.getAddress())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .amount(product.getAmount())
                .productType(product.getProductType())
                .imageList(product.getImageList())
                .build();
    }
}
