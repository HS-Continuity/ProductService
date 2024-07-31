package com.yeonieum.productservice.infrastructure.cache.data;

import com.yeonieum.productservice.domain.productinventory.entity.ShippedStock;
import com.yeonieum.productservice.domain.productinventory.entity.StockUsage;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@EqualsAndHashCode
public class StockUsageCache {
    private String orderDetailId;
    private Long productId;
    private int id;
    private String shippedAt;

    public StockUsageCache (String orderDetailId, Long productId, int id) {
        this.orderDetailId = orderDetailId;
        this.productId = productId;
        this.id = id;
    }


    public StockUsageCache (String orderDetailId, Long productId, int id, String shippedAt) {
        this.orderDetailId = orderDetailId;
        this.productId = productId;
        this.id = id;
        this.shippedAt = shippedAt;
    }

    public StockUsage toEntity() {
        return StockUsage.builder()
                .orderDetailId(orderDetailId)
                .productId(productId)
                .id(id)
                .build();
    }

    public ShippedStock toShippedStockEntity() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime shippedDateTime = LocalDateTime.parse(shippedAt.substring(0, 19), formatter);

        return ShippedStock.builder()
                .orderDetailId(orderDetailId)
                .productId(productId)
                .id(id)
                .shippedDateTime(shippedDateTime)
                .build();
    }
}

