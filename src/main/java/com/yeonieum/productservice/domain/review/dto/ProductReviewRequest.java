package com.yeonieum.productservice.domain.review.dto;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import lombok.*;

import java.time.LocalDateTime;

public class ProductReviewRequest {

    @Getter
    @NoArgsConstructor
    public static class OfRegisterProductReview {
        private Long productId;
        private String memberId;
        private LocalDateTime createDate;
        private String reviewContent;
        private String reviewImage;
        private int reviewScore;

        public ProductReview toEntity(Product product) {
            return ProductReview.builder()
                    .product(product)
                    .memberId(this.memberId)
                    .createDate(this.createDate)
                    .reviewContent(this.reviewContent)
                    .reviewImage(this.reviewImage)
                    .reviewScore(this.reviewScore)
                    .build();
        }
    }
}
