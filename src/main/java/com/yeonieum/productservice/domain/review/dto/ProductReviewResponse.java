package com.yeonieum.productservice.domain.review.dto;

import com.yeonieum.productservice.domain.customer.dto.CustomerResponse;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import lombok.*;

import java.time.LocalDateTime;

public class ProductReviewResponse {

    @Getter
    @Builder
    public static class OfRetrieveProductWithReview {
        private Long productReviewId;
        private String memberId;
        private LocalDateTime createDate;
        private String reviewContent;
        private String reviewImage;
        private double reviewScore;

        public static ProductReviewResponse.OfRetrieveProductWithReview convertedBy(ProductReview productReview) {
            return OfRetrieveProductWithReview.builder()
                    .productReviewId(productReview.getProductReviewId())
                    .memberId(productReview.getMemberId())
                    .createDate(productReview.getCreateDate())
                    .reviewContent(productReview.getReviewContent())
                    .reviewImage(productReview.getReviewImage())
                    .reviewScore(productReview.getReviewScore())
                    .build();
        }
    }
}
