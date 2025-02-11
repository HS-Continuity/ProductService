package com.yeonieum.productservice.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_detail_image")
public class ProductDetailImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_detail_image_id")
    private Long productDetailImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "detail_image", length = 450)
    private String detailImage;

    public void changeDetailImage(String detailImage) {
        this.detailImage = detailImage;
    }
}
