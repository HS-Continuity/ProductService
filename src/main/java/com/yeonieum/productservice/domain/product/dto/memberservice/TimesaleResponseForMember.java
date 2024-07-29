package com.yeonieum.productservice.domain.product.dto.memberservice;

import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TimesaleResponseForMember {

    @Getter
    @Builder
    public static class OfRetrieve {
        Long productTimesaleId;
        Long customerId;
        Long productId;
        String productName;
        int price;
        int discountRate;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        String serviceStatus;
        private double averageRating;
        private int reviewCount;
        @Builder.Default
        private boolean soldOut = false;

        public static OfRetrieve convertedBy(ProductTimesale timesale) {
            int originalPrice = timesale.getProduct().getProductPrice();
            int discountRate = timesale.getDiscountRate();
            int discountedPrice = (int) (originalPrice * (1 - discountRate / 100.0));

            return OfRetrieve.builder()
                    .productTimesaleId(timesale.getProductTimesaleId())
                    .customerId(timesale.getProduct().getCustomer().getCustomerId())
                    .productId(timesale.getProduct().getProductId())
                    .productName(timesale.getProduct().getProductName())
                    .startDateTime(timesale.getStartDatetime())
                    .endDateTime(timesale.getEndDatetime())
                    .averageRating(timesale.getProduct().getAverageScore())
                    .reviewCount(timesale.getProduct().getReviewCount())
                    .price(discountedPrice)
                    .discountRate(discountRate)
                    .serviceStatus(timesale.getServiceStatus().getStatusName()) // N+1 예상
                    .build();
        }

        public void changeSoldOut(boolean isSoldOut) {
            this.soldOut = isSoldOut;
        }
    }
}
