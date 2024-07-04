package com.yeonieum.productservice.infrastructure.persistance.entity.product;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(mappedBy = "saleType", fetch = FetchType.LAZY)
    private Product product;

    @Column(name = "type_name", nullable = false)
    private String typeName;

}
