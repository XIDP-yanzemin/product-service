package com.practice.productservice.repository;

import com.practice.productservice.entity.UserProductRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProductRelationRepository extends JpaRepository<UserProductRelation, Long> {
}
