package com.yeonieum.productservice.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "cart_type")
public class CartType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_type_id")
    private Long cartTypeId;

    @OneToOne(mappedBy = "cartType", fetch = FetchType.LAZY)
    private CartProduct cartProduct;

    @Column(name = "type_name", nullable = false)
    private String typeName;

}

