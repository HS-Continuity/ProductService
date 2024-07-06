package com.yeonieum.productservice.domain.product.entity;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.productinventory.entity.ProductInventory;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import com.yeonieum.productservice.global.converter.ActiveStatusConverter;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_type_id", nullable = false)
    private SaleType saleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_category_id", nullable = false)
    private ProductDetailCategory productDetailCategory;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 900)
    private String description;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private int price;

    @Builder.Default
    private int baseDiscountRate = 0;

    @Builder.Default
    private int regularDiscountRate = 0;

    @Builder.Default
    private int personalizeDiscountRate = 0;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(name = "is_regular_sale", nullable = false)
    @Builder.Default
    private ActiveStatus isRegularSale = ActiveStatus.ACTIVE;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(name = "is_page_visibility", nullable = false)
    @Builder.Default
    private ActiveStatus isPageVisibility = ActiveStatus.ACTIVE;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductReview> productReviewList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductDetailImage> productDetailImageList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductAdvertisementService> advertisementServiceList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductCertification> productCertificationList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductInventory> productInventoryList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductTimeSale> productTimeSaleList = new ArrayList<>();

}
