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
        private Long productTimesaleId;
        private Long customerId;
        private Long productId;
        private String productName;
        private String productImage;
        private int price;
        private int discountPrice;
        private int discountRate;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String serviceStatus;
        private double averageRating;
        private int reviewCount;
        private ActiveStatus isCertification;
        private String storeName;
        private int deliveryFee;

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
                    .storeName(timesale.getProduct().getCustomer().getStoreName())
                    .deliveryFee(timesale.getProduct().getCustomer().getDeliveryFee())
                    .build();
        }

        public void changeSoldOut(boolean isSoldOut) {
            this.soldOut = isSoldOut;
        }
    }

}
