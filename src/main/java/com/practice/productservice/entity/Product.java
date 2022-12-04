package com.practice.productservice.entity;

import com.practice.productservice.request.AddProductRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity(name = "products")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private BigDecimal price;

    private String description;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private Type type;

    public static Product buildProductFrom(AddProductRequest addProductRequest) {
        return builder()
                .productName(addProductRequest.getName())
                .price(addProductRequest.getPrice())
                .description(addProductRequest.getDescription())
                .amount(addProductRequest.getAmount())
                .type(addProductRequest.getType())
                .build();
    }
}
