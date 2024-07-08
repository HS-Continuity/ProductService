package com.yeonieum.productservice.messaging.message;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TimeSaleEventMessage {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long productId;
}
