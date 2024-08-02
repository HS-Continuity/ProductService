package com.yeonieum.productservice.domain.productinventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "stock_usage")
public class StockUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_usage_id")
    private Long stockUsageId;
    @Column(name = "order_detail_id", nullable = false)
    private String orderDetailId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "id", nullable = false)
    private int id;
}
