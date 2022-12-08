package com.practice.productservice.repository;

import com.practice.productservice.entity.UserProductRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductRelationRepository extends JpaRepository<UserProductRelation, Long> {

    Optional<UserProductRelation> findByUserIdAndProductId(Long userId, Long productId);

    List<UserProductRelation> findByUserId(Long userId);
}
