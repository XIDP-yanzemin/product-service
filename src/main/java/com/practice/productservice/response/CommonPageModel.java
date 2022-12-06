package com.practice.productservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonPageModel {

    private List<ProductResponseForPage> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Integer numberOfElements;

    public static CommonPageModel buildResponseFrom(Pageable pageable, List<ProductResponseForPage> productResponseForPages) {
        return builder()
                .content(productResponseForPages)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .numberOfElements(productResponseForPages.size())
                .build();
    }
}
