package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import com.yeonieum.productservice.global.enums.ServiceStatusCode;
import com.yeonieum.productservice.infrastructure.messaging.message.TimesaleEventMessage;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TimesaleRequestForCustomer {
    @Getter
    @NoArgsConstructor
    public static class OfRegister {

        @NotNull(message = "상품 아이디는 필수입니다.")
        Long productId;

        @NotNull(message = "타임세일 시작시간 입력은 필수입니다.")
        LocalDateTime startTime;

        @NotNull(message = "타임세일 종료시간 입력은 필수입니다.")
        LocalDateTime endTime;

        @Min(value = 0, message = "타임세일 할인율은 0% 이상이어야 합니다.")
        @Max(value = 99, message = "타임세일 할인율은 99% 이하이어야 합니다.")
        int discountRate;

        public ProductTimesale toEntity(Product product, ServiceStatus serviceStatus) {
            return ProductTimesale.builder()
                    .product(product)
                    .discountRate(this.getDiscountRate())
                    .startDatetime(this.getStartTime())
                    .endDatetime(this.getEndTime())
                    .serviceStatus(serviceStatus) // 기본값
                    .build();
        }

        public TimesaleEventMessage toEventMessage() {
            return TimesaleEventMessage.builder()
                    .startDateTime(this.getStartTime())
                    .endDateTime(this.getEndTime())
                    .productId(this.getProductId())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OfModifyStatus {
        Long productId;
        ServiceStatusCode statusCode;
    }
}