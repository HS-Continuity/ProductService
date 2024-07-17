package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.messaging.message.TimesaleEventMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TimesaleRequestForCustomer {
    @Getter
    @NoArgsConstructor
    public static class OfRegister {
        Long productId;
        LocalDateTime startTime;
        LocalDateTime endTime;
        int discountRate;

        public ProductTimesale toEntity(Product product) {
            return ProductTimesale.builder()
                    .product(product)
                    .discountRate(this.getDiscountRate())
                    .startDatetime(this.getStartTime())
                    .endDatetime(this.getEndTime())
                    .isCompleted(ActiveStatus.ACTIVE) // 기본값
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
        ActiveStatus isCompleted;
    }
}
