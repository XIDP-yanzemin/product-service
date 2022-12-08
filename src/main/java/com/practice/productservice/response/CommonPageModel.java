package com.practice.productservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
