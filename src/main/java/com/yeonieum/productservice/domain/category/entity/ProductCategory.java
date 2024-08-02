package com.yeonieum.productservice.domain.category.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_category")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_category_id")
    private Long productCategoryId;

    @Column(name = "category_name", unique = true, nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductDetailCategory> productDetailCategoryList = new ArrayList<>();

    public void changeCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
