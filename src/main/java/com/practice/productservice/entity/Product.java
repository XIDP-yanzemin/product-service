package com.practice.productservice.entity;

import com.practice.productservice.controller.request.AddProductRequest;
import com.practice.productservice.controller.request.UpdateProductRequest;
import com.practice.productservice.controller.response.ListUserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

@Entity(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String productName;

    private BigDecimal price;

    private String description;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private List<Image> imageList;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    public static Product buildProductFrom(ListUserResponse user,
                                           AddProductRequest addProductRequest,
                                           List<Image> imageList,
                                           PostType postType) {
        return builder()
                .productName(addProductRequest.getName())
                .price(addProductRequest.getPrice())
                .description(addProductRequest.getDescription())
                .amount(addProductRequest.getAmount())
                .productType(addProductRequest.getProductType())
                .userId(user.getId())
                .imageList(imageList)
                .postType(postType)
                .build();
    }

    public void updateProduct(UpdateProductRequest updateProductRequest) {
        setProductName(updateProductRequest.getName());
        setPrice(updateProductRequest.getPrice());
        setAmount(updateProductRequest.getAmount());
        setDescription(updateProductRequest.getDescription());
        setProductType(updateProductRequest.getProductType());
    }
}
