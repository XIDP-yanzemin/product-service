package com.practice.productservice.request;

import com.practice.productservice.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddProductRequest {

    @NotEmpty
    private String name;

    private String description;

    @NotNull(message = "price should not be null.")
    private BigDecimal price;

    @NotNull(message = "amount should not be null.")
    private Integer amount;

    @NotNull(message = "type should not be null.")
    private Type type;

    @NotNull(message = "url should not be null.")
    private List<String> urls;
}
