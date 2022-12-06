package com.practice.productservice.repository;

import com.practice.productservice.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Modifying
    @Query("DELETE FROM products_image_info p WHERE p.product.id= ?1")
    void deleteByProductId(Long productId);

    List<Image> findByProductIdIn(List<Long> idList);
}
