package com.yeonieum.productservice.infrastructure.persistance.repository.cart;

import com.yeonieum.productservice.infrastructure.persistance.entity.cart.CartType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartTypeRepository extends JpaRepository<CartType, Long> {
}
