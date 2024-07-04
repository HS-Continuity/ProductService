package com.yeonieum.productservice.domain.customer.repository;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
