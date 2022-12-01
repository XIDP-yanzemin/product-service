package com.practice.productservice.repository;

import com.practice.productservice.entity.Product;
import com.practice.productservice.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByType(Type type, Pageable pageable);
}
