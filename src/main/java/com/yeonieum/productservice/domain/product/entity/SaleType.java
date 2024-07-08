package com.yeonieum.productservice.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "sale_type")
public class SaleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_type_id")
    private Long saleTypeId;

    @OneToMany(mappedBy = "saleType")
    @Builder.Default
    private List<Product> productList = new ArrayList<>();

    @Column(name = "type_name", nullable = false)
    private String typeName;
}
