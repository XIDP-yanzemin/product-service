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

    //todo 为撒加这个注解？
    @Enumerated(EnumType.STRING)
    private Type type;

    private List<Image> urls;

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
                .type(product.getType())
                .urls(product.getImageList())
                .build();
    }
}
