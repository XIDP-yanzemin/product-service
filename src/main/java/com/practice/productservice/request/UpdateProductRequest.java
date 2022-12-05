package com.practice.productservice.request;

import com.practice.productservice.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {

    @NotEmpty(message = "name should not be empty.")
    private String name;

    private String description;

    @NotNull(message = "price should not be null.")
    private BigDecimal price;

    @NotNull(message = "amount should not be null.")
    private Integer amount;

    @NotNull(message = "type should not be null.")
    private Type type;
}
