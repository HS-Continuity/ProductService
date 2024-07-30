package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


public class TimesaleResponseForCustomer {
    @Getter
    @Builder
    public static class OfRetrieve {
        Long productId;
        Long timesaleId;
        String productName;
        int price;
        int discountRate;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        String serviceStatus;

        public static OfRetrieve convertedBy(ProductTimesale productTimesale) {
            return OfRetrieve.builder()
                    .productId(productTimesale.getProduct().getProductId())
                    .productName(productTimesale.getProduct().getProductName())
                    .startDateTime(productTimesale.getStartDatetime())
                    .endDateTime(productTimesale.getEndDatetime())
                    .discountRate(productTimesale.getDiscountRate())
                    .serviceStatus(productTimesale.getServiceStatus().getStatusName())
                    .timesaleId(productTimesale.getProductTimesaleId())
                    .build();
        }
    }
}