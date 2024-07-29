package com.yeonieum.productservice.domain.review.dto;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import jakarta.annotation.Nullable;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class ProductReviewRequest {

    @Getter
    @NoArgsConstructor
    public static class OfRegisterProductReview {
        private Long productId;
        private String memberId;
        private LocalDateTime createDate;
        private String reviewContent;
        private double reviewScore;

        public ProductReview toEntity(Product product, String reviewImageUrl) {
            return ProductReview.builder()
                    .product(product)
                    .memberId(this.memberId)
                    .createDate(this.createDate)
                    .reviewContent(this.reviewContent)
                    .reviewImage(reviewImageUrl)
                    .reviewScore(this.reviewScore)
                    .build();
        }
    }
}
