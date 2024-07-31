package com.yeonieum.productservice.infrastructure.messaging.message;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
public class AdvertisementEventMessage {
    private Long productId;
    private LocalDate startDate;
    private LocalDate endDate;
}
