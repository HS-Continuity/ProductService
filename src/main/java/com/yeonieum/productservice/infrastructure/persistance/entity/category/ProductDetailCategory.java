package com.yeonieum.productservice.infrastructure.persistance.entity.category;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "category_detail_name", nullable = false)
    private String categoryDetailName;

    @Column(name = "shelf_life_day", nullable = false)
    private int shelfLifeDay;


}
