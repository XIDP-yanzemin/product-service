package com.practice.productservice.response;

import com.practice.productservice.client.ListUserResponse;
import com.practice.productservice.entity.Image;
import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    @Enumerated(EnumType.STRING)
    private Type type;

    private List<String> urls;

    public static ProductResponseForPage buildResponseFrom(List<Image> imageList, Product product) {
        return builder()
                .id(product.getId())
                .userId(product.getUserId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .amount(product.getAmount())
                .type(product.getType())
                .urls(imageList.stream()
                        .filter(image -> image.getProduct().getId().equals(product.getId()))
                        .map(Image::getUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ProductResponseForPage buildProductResponse(Long userId, ListUserResponse user, List<String> urls, Product product) {
        return builder()
                .id(product.getId())
                .userId(userId)
                .userName(user.getUsername())
                .email(user.getEmail())
                .cellphone(user.getCellphone())
                .address(user.getAddress())
                .productName(product.getProductName())
                .price(product.getPrice())
                .description(product.getDescription())
                .amount(product.getAmount())
                .type(product.getType())
                .urls(urls)
                .build();
    }
}
