package com.practice.productservice.controller.request;


import com.practice.productservice.entity.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseProductRequest {

    @NotEmpty
    private String name;

    private String description;

    @NotNull(message = "price should not be null.")
    private BigDecimal price;

    @NotNull(message = "amount should not be null.")
    private Integer amount;

    @NotNull(message = "type should not be null.")
    private ProductType productType;
}
