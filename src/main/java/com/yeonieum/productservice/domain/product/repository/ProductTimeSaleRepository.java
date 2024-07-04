package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductTimeSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTimeSaleRepository extends JpaRepository<ProductTimeSale, Long> {
}
