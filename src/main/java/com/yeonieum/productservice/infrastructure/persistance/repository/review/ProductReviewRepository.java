package com.yeonieum.productservice.infrastructure.persistance.repository.review;

import com.yeonieum.productservice.infrastructure.persistance.entity.review.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
}
