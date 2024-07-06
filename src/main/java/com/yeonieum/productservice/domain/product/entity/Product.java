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

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_description", nullable = false, length = 900)
    private String productDescription;

    @Column(name = "product_image", nullable = false)
    private String productImage;

    @Column(name = "product_origin", nullable = false)
    private String productOrigin;

    @Column(name = "product_price", nullable = false)
    private int productPrice;

    @Builder.Default
    private int baseDiscountRate = 0;

    @Builder.Default
    private int regularDiscountRate = 0;

    @Builder.Default
    private int personalizedDiscountRate = 0;

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

    public void changeProductName(String productName) {
        this.productName = productName;
    }

    public void changeProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void changeProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void changeProductOrigin(String productOrigin) {
        this.productOrigin = productOrigin;
    }

    public void changeIsPageVisibility(ActiveStatus isPageVisibility) {
        this.isPageVisibility = isPageVisibility;
    }

    public void changeIsRegularSale(ActiveStatus isRegularSale) {
        this.isRegularSale = isRegularSale;
    }

    public void changeBaseDiscountRate(int baseDiscountRate) {
        this.baseDiscountRate = baseDiscountRate;
    }

    public void changeRegularDiscountRate(int regularDiscountRate) {
        this.regularDiscountRate = regularDiscountRate;
    }

    public void changePersonalizedDiscountRate(int personalizedDiscountRate) {
        this.personalizedDiscountRate = personalizedDiscountRate;
    }
}
