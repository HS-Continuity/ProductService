package com.yeonieum.productservice.domain.cart.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "cartType")
    @Builder.Default
    private List<CartProduct> cartProduct = new ArrayList<>();

    @Column(name = "type_name", nullable = false)
    private String typeName;

}

