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
        Long productTimesaleId;
        Long customerId;
        Long productId;
        String productName;
        String productImage;
        int price;
        int discountPrice;
        int discountRate;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        String serviceStatus;
        private double averageRating;
        private int reviewCount;
        private ActiveStatus isCertification;

        @Builder.Default
        private boolean soldOut = false;

        public static OfRetrieve convertedBy(ProductTimesale timesale) {
            int originalPrice = timesale.getProduct().getProductPrice();
            int discountRate = timesale.getDiscountRate();
            int discountedPrice = (int) (originalPrice * (1 - discountRate / 100.0));

            return OfRetrieve.builder()
                    .productImage(timesale.getProduct().getProductImage())
                    .productTimesaleId(timesale.getProductTimesaleId())
                    .customerId(timesale.getProduct().getCustomer().getCustomerId())
                    .productId(timesale.getProduct().getProductId())
                    .productName(timesale.getProduct().getProductName())
                    .startDateTime(timesale.getStartDatetime())
                    .endDateTime(timesale.getEndDatetime())
                    .averageRating(timesale.getProduct().getAverageScore())
                    .reviewCount(timesale.getProduct().getReviewCount())
                    .price(originalPrice)
                    .discountRate(discountRate)
                    .discountPrice(discountedPrice)
                    .serviceStatus(timesale.getServiceStatus().getStatusName())
                    .isCertification(timesale.getProduct().getIsCertification())
                    .build();
        }

        public void changeSoldOut(boolean isSoldOut) {
            this.soldOut = isSoldOut;
        }
    }

}
