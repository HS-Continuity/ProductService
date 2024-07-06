package com.yeonieum.productservice.domain.product.dto.memberservice;

import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RegisterAdvertisementRequestDto {
    Long productId;
    String productName;
    LocalDate startDate;
    LocalDate endDate;
    ActiveStatus isCompleted;
}
