package com.yeonieum.productservice.infrastructure.persistance.entity.review;

import com.yeonieum.productservice.infrastructure.persistance.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_review")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_review_id")
    private Long productReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "review_content", length = 900)
    private String reviewContent;

    @Column(name = "review_image", length = 450)
    private String reviewImage;

    @Column(name = "review_score")
    private int reviewScore;
}
