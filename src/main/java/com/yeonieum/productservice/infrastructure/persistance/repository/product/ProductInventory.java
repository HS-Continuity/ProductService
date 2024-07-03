package com.yeonieum.productservice.infrastructure.persistance.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInventory extends JpaRepository<ProductInventory, Integer> {
}
