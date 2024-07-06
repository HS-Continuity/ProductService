package com.yeonieum.productservice.domain.product.dto.memberservice;

import com.yeonieum.productservice.global.enums.ActiveStatus;

import java.time.LocalDateTime;

public class RetrieveTimeSaleProductResponseDto {
    Long productId;
    String productName;
    int price;
    int discountRate;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    ActiveStatus isCompleted;

    public RetrieveTimeSaleProductResponseDto(
            Long productId, String productName, int price, int discountRate, LocalDateTime startDateTime
            ,LocalDateTime endDateTime, char isCompleted) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.discountRate = discountRate;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isCompleted = ActiveStatus.fromCode(isCompleted);
    }
}
