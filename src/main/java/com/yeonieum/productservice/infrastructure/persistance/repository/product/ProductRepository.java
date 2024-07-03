package com.yeonieum.productservice.infrastructure.persistance.repository.product;

import com.yeonieum.productservice.infrastructure.persistance.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
