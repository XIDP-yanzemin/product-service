package com.practice.productservice.service;

import com.practice.productservice.dto.ListProductsResponse;
import com.practice.productservice.entity.Product;
import com.practice.productservice.repository.ProductRepository;
import com.practice.productservice.entity.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ListProductsResponse listProductByRequest(Pageable pageable, Type type) {
        if (Objects.isNull(type)) {
            Page<Product> all = productRepository.findAll(pageable);
            return ListProductsResponse.buildResponseFrom(pageable, all);
        }
        Page<Product> listByType = productRepository.findByType(type, pageable);
        return ListProductsResponse.buildResponseFrom(pageable, listByType);
    }

}
