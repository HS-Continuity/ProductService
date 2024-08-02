package com.yeonieum.productservice.domain.productinventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "shipped_stock")
public class ShippedStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipped_stock_id")
    private Long shippedStockId;

    @Column(name = "order_detail_id", nullable = false)
    private String orderDetailId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "shipped_date_time", nullable = false)
    private LocalDateTime shippedDateTime;

}
