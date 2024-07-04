package com.yeonieum.productservice.domain.cart.repository;

import com.yeonieum.productservice.domain.cart.entity.CartType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartTypeRepository extends JpaRepository<CartType, Long> {
}
