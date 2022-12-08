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
public class CommonPageModel<T> {

    protected List<T> content;

    protected Integer pageNumber;

    protected Integer pageSize;

    protected Integer numberOfElements;

    public static CommonPageModel<ProductResponseForPage> from(Pageable pageable, List<ProductResponseForPage> responses) {
        return CommonPageModel.<ProductResponseForPage>builder()
                .content(responses)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .numberOfElements(responses.size())
                .build();
    }
}
