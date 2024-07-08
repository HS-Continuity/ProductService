package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@RedisHash("shippedstock")
public class ShippedStockDto {
    private Long productId;
    private Long orderId;
    private int quantity;
    private LocalDateTime shippedTime;

    // [redis set에 value로 저장되는 객체, 속성값이 같으면 같은 객체]
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        ShippedStockDto that = (ShippedStockDto) o;
        return quantity == that.quantity && productId.equals(that.productId) && orderId.equals(that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, orderId, quantity);
    }
}
