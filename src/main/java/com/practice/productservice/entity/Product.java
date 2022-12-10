package com.practice.productservice.entity;

import com.practice.productservice.client.ListUserResponse;
import com.practice.productservice.request.AddProductRequest;
import com.practice.productservice.request.BaseProductRequest;
import com.practice.productservice.request.UpdateProductRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String productName;

    private BigDecimal price;

    private String description;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private Type type;
    //todo 在这种场景下，一般会在查询 product 的时候机会一起把 image 一起查询出来，所以会把 1-n 放在 product 里面

    public static Product buildProductFrom(ListUserResponse user, AddProductRequest addProductRequest) {
        return builder()
                .productName(addProductRequest.getName())
                .price(addProductRequest.getPrice())
                .description(addProductRequest.getDescription())
                .amount(addProductRequest.getAmount())
                .type(addProductRequest.getType())
                .userId(user.getId())
                .build();
    }

    public static Product buildProductFrom(BaseProductRequest baseProductRequest, Long userId) {
        return builder()
                .userId(userId)
                .productName(baseProductRequest.getName())
                .price(baseProductRequest.getPrice())
                .description(baseProductRequest.getDescription())
                .price(baseProductRequest.getPrice())
                .amount(baseProductRequest.getAmount())
                .type(baseProductRequest.getType())
                .build();
    }

    public void updateProductInfo(UpdateProductRequest updateProductRequest, Product product) {
        product.setProductName(updateProductRequest.getName());
        product.setPrice(updateProductRequest.getPrice());
        product.setAmount(updateProductRequest.getAmount());
        product.setDescription(updateProductRequest.getDescription());
        product.setType(updateProductRequest.getType());
    }
}
