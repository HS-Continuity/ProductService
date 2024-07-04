package com.yeonieum.productservice.domain.review.repository;

import com.yeonieum.productservice.domain.review.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
}
