package com.yeonieum.productservice.infrastructure.persistance.repository.product;

import com.yeonieum.productservice.infrastructure.persistance.entity.product.ProductCertification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCertificationRepository extends JpaRepository<ProductCertification, Long> {
}
