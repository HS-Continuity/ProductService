package com.yeonieum.productservice.domain.product.dto.memberservice;

import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Getter;
import java.time.LocalDateTime;

public class TimeSaleRequest {
    @Getter
    public static class RegisterDto {
        Long productId;
        LocalDateTime startTime;
        LocalDateTime endTime;
        int discountRate;
    }

    @Getter
    public static class ModifyStatusDto {
        Long productId;
        ActiveStatus isCompleted;
    }
}
