package com.yeonieum.productservice.infrastructure.persistance.repository.product;

import com.yeonieum.productservice.infrastructure.persistance.entity.product.ProductAdvertisementService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAdvertisementServiceRepository extends JpaRepository<ProductAdvertisementService, Long> {
}

