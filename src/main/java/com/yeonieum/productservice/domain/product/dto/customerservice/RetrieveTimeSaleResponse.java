package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RetrieveTimeSaleResponse {
    Long productId;
    String productName;
    int price;
    int discountRate;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    ActiveStatus isCompleted;
}
