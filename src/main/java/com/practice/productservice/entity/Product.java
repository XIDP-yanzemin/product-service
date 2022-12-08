package com.practice.productservice.entity;

import com.practice.productservice.client.ListUserResponse;
import com.practice.productservice.request.AddProductRequest;
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

    public void updateProductInfo(UpdateProductRequest updateProductRequest, Product product) {
        product.setProductName(updateProductRequest.getName());
        product.setPrice(updateProductRequest.getPrice());
        product.setAmount(updateProductRequest.getAmount());
        product.setDescription(updateProductRequest.getDescription());
        product.setType(updateProductRequest.getType());
    }
}
