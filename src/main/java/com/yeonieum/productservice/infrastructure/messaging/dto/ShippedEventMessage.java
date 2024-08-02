package com.yeonieum.productservice.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


import java.time.LocalDateTime;

@Getter
public class ShippedEventMessage {
    private String orderDetailId;
    private Long productId;
    private int quantity;
    private String shippedAt;

    @JsonCreator
    public ShippedEventMessage(
            @JsonProperty("orderDetailId") String orderDetailId,
            @JsonProperty("productId") Long productId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("shippedAt") String shippedAt) {
        this.orderDetailId = orderDetailId;
        this.productId = productId;
        this.quantity = quantity;
        this.shippedAt = shippedAt;
    }

    // Getters
    public String getOrderDetailId() { return orderDetailId; }
    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getShippedAt() { return shippedAt; }
}