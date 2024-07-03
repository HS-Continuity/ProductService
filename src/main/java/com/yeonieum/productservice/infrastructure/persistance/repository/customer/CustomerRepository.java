package com.yeonieum.productservice.infrastructure.persistance.repository.customer;

import com.yeonieum.productservice.infrastructure.persistance.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
