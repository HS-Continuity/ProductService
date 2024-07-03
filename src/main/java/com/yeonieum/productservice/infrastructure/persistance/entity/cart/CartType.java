package com.yeonieum.productservice.infrastructure.persistance.entity.cart;

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

    @Column(name = "type_name", nullable = false)
    private String typeName;

}

