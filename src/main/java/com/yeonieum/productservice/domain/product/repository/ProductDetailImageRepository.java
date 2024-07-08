package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDetailImageRepository extends JpaRepository<ProductDetailImage, Long> {
    @Query("SELECT pdi FROM ProductDetailImage pdi WHERE pdi.product.productId = :productId")
    ProductDetailImage findByProductId(@Param("productId") Long productId);
}
