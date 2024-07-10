package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("stockusage")
public class StockUsageDto {
    Long productId;
    Long orderId;
    int quantity;

    // [redis set에 value로 저장되는 객체, 속성값이 같으면 같은 객체]
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        StockUsageDto that = (StockUsageDto) o;
        return quantity == that.quantity && productId.equals(that.productId) && orderId.equals(that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, orderId, quantity);
    }
}
