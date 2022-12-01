package com.practice.productservice.dto;

import com.practice.productservice.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListProductsResponse {

    private List<Product> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Integer numberOfElements;

    public static ListProductsResponse buildResponseFrom(Pageable pageable, Page<Product> all) {
        return builder()
                .content(all.toList())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .numberOfElements(all.getNumberOfElements())
                .build();
    }
}
