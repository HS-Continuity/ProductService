package com.yeonieum.productservice.domain.product.dto.memberservice;

import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TimesaleResponseForMember {

    @Getter
    @Builder
    public static class OfRetrieve {
        Long productId;
        String productName;
        int price;
        int discountRate;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        String serviceStatus;
        private double averageRating;
        private int reviewCount;


        public static OfRetrieve convertedBy(ProductTimesale timesale) {
            return OfRetrieve.builder()
                    .productId(timesale.getProduct().getProductId())
                    .productName(timesale.getProduct().getProductName())
                    .startDateTime(timesale.getStartDatetime())
                    .endDateTime(timesale.getEndDatetime())
                    .averageRating(timesale.getProduct().getAverageScore())
                    .reviewCount(timesale.getProduct().getReviewCount())
                    .price(timesale.getProduct().getProductPrice())
                    .discountRate(timesale.getDiscountRate())
                    .serviceStatus(timesale.getServiceStatus().getStatusName()) // N+1 예상
                    .build();
        }
    }
}
