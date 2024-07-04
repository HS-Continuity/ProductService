package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailImageRepository extends JpaRepository<ProductDetailImage, Long> {
}
