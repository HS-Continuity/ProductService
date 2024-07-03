package com.yeonieum.productservice.infrastructure.persistance.repository.product;

import com.yeonieum.productservice.infrastructure.persistance.entity.product.ProductTimeSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTimeSaleRepository extends JpaRepository<ProductTimeSale, Long> {
}
