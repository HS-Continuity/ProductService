package com.yeonieum.productservice.domain.review.dto;

import lombok.*;

import java.time.LocalDateTime;

public class ProductReviewRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class RegisterProductReviewDto {
        private Long productId;
        private String memberId;
        private LocalDateTime createDate;
        private String reviewContent;
        private String reviewImage;
        private int reviewScore;
    }
}
