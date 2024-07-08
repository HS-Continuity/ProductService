package com.yeonieum.productservice.domain.cart.entity;

import com.yeonieum.productservice.domain.product.entity.Product;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_type_id", nullable = false)
    private CartType cartType;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    private int quantity;

    public void changeProductQuantity(int productQuantity) {
        this.quantity = productQuantity;
    }

}
