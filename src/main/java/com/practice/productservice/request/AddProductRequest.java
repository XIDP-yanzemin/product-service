package com.practice.productservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AddProductRequest extends BaseProductRequest{

    @NotNull(message = "url should not be null.")
    private List<String> url;
}
