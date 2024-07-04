package com.yeonieum.productservice.infrastructure.persistance.repository.product;

import com.yeonieum.productservice.infrastructure.persistance.entity.product.ProductDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailImageRepository extends JpaRepository<ProductDetailImage, Long> {
}
