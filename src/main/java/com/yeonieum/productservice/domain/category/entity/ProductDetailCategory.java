package com.yeonieum.productservice.domain.category.entity;

import com.yeonieum.productservice.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_detail_category")
public class ProductDetailCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_detail_category_id")
    private Long productDetailCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "detail_category_name", unique = true, nullable = false)
    private String detailCategoryName;

    @Column(name = "shelf_life_day", nullable = false)
    private int shelfLifeDay;

    @OneToMany(mappedBy = "productDetailCategory")
    @Builder.Default
    private List<Product> productList = new ArrayList<>();

    public void changeDetailCategoryName(String detailCategoryName) {
        this.detailCategoryName = detailCategoryName;
    }

    public void changeDetailCategoryShelfLifeDay(int shelfLifeDay) {
        this.shelfLifeDay = shelfLifeDay;
    }
}
