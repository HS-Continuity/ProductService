package com.yeonieum.productservice.infrastructure.persistance.entity.cart;

import com.yeonieum.productservice.infrastructure.persistance.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "cart_product")
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_product_id")
    private Long cartProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_type_id", nullable = false)
    private CartType cartType;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    private int quantity;

}
