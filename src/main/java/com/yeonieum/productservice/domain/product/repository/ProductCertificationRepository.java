package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductCertificationRepository extends JpaRepository<ProductCertification, Long> {
    @Query("SELECT pc FROM ProductCertification pc WHERE pc.product.productId = :productId")
    ProductCertification findByProductId(@Param("productId") Long productId);
}
