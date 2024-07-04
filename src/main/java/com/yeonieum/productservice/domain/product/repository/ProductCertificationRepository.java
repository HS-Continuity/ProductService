package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCertificationRepository extends JpaRepository<ProductCertification, Long> {
}
