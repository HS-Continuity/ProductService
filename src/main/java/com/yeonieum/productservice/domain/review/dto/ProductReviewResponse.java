package com.yeonieum.productservice.domain.review.dto;

import lombok.*;

import java.time.LocalDateTime;

public class ProductReviewResponse {

    @Getter
    @Builder
    public static class RetrieveProductWithReviewsDto {
        private Long productReviewId;
        private String memberId;
        private LocalDateTime createDate;
        private String reviewContent;
        private String reviewImage;
        private double reviewScore;
    }
}
