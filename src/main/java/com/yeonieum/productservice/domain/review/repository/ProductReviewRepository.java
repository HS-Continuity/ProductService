package com.yeonieum.productservice.domain.review.repository;

import com.yeonieum.productservice.domain.review.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    boolean existsByMemberId(String memberId);

    @Query("SELECT COUNT(r) FROM ProductReview r " + "WHERE r.product.productId = :productId")
    int countByProductId(@Param("productId") Long productId);

    @Query("SELECT COALESCE(AVG(r.reviewScore), 0) FROM ProductReview r WHERE r.product.productId = :productId")
    double findAverageScoreByProductId(@Param("productId") Long productId);

    @Query("SELECT pr FROM ProductReview pr WHERE pr.product.productId = :productId")
    Page<ProductReview> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT count(r) > 0 FROM ProductReview r WHERE r.product.productId = :productId AND r.memberId = :memberId")
    boolean existsByProduct_IdAndMemberId(@Param("productId") Long productId, @Param("memberId") String memberId);
    boolean existsByProductReviewIdAndMemberId(Long productReviewId, String memberId);
}



