package com.practice.productservice.repository;

import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByType(Type type, Pageable pageable);

    Page<Product> findByIdIn(List<Long> idList, Pageable pageable);
}
