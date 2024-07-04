package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.SaleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleTypeRepository extends JpaRepository<SaleType, Long> {
}
