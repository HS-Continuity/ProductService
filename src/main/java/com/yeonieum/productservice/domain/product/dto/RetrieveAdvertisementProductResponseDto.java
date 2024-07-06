package com.yeonieum.productservice.domain.product.dto;

import com.yeonieum.productservice.global.enums.ActiveStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RetrieveAdvertisementProductResponseDto {
    Long productId;
    String productName;
    LocalDate startDate;
    LocalDate endDate;
    ActiveStatus isCompleted;

    public RetrieveAdvertisementProductResponseDto(
            Long productId, String productName, LocalDate startDate, LocalDate endDate, char isCompleted) {
        this.productId = productId;
        this.productName = productName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCompleted = ActiveStatus.fromCode(isCompleted);
    }
}
