package com.yeonieum.productservice.domain.review.dto;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

        @NotBlank(message = "상품리뷰 내용은 필수입니다.")
        @Size(max = 300, message = "상품이름은 최대 300자까지 가능합니다.")
        private String reviewContent;

        private double reviewScore;

        public ProductReview toEntity(String memberId, Product product, String reviewImageUrl) {
            return ProductReview.builder()
                    .product(product)
                    .memberId(memberId)
                    .createDate(this.createDate)
                    .reviewContent(this.reviewContent)
                    .reviewImage(reviewImageUrl)
                    .reviewScore(this.reviewScore)
                    .build();
        }
    }
}
