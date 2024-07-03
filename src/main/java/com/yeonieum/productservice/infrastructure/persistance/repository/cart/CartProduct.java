package com.yeonieum.productservice.infrastructure.persistance.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartProduct extends JpaRepository<CartProduct, Long> {
}
