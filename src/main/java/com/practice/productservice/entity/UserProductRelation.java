package com.practice.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//todo 表名
@Entity(name = "user_product_relation")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProductRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long productId;

    public static UserProductRelation buildUserProductRelation(Long userId, Long productId) {
        return builder().userId(userId).productId(productId).build();
    }
}
