package com.yeonieum.productservice.infrastructure.persistance.entity.product;

import com.yeonieum.productservice.infrastructure.persistance.commons.converter.ActiveStatusConverter;
import com.yeonieum.productservice.infrastructure.persistance.commons.enums.ActiveStatus;
import com.yeonieum.productservice.infrastructure.persistance.entity.category.ProductDetailCategory;
import com.yeonieum.productservice.infrastructure.persistance.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.*;

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
    private int base_discount_rate = 0;

    @Builder.Default
    private int regular_discount_rate = 0;

    @Builder.Default
    private int personalize_discount_rate = 0;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(name = "is_regular_sale", nullable = false)
    @Builder.Default
    private ActiveStatus isRegularSale = ActiveStatus.ACTIVE;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(name = "is_page_visibility", nullable = false)
    @Builder.Default
    private ActiveStatus isPageVisibility = ActiveStatus.ACTIVE;

}
