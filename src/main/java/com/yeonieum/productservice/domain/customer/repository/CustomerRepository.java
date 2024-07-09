package com.yeonieum.productservice.domain.customer.repository;

import com.yeonieum.productservice.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query(value = "SELECT c.* FROM customer c " +
            "JOIN product p ON c.customer_id = p.customer_id " +
            "WHERE c.customer_id = :customerId AND p.is_certification = :isEcoFriend", nativeQuery = true)
    Optional<Customer> findCustomersWithProductsByStatus(@Param("customerId") Long customerId, @Param("isEcoFriend") char isEcoFriend);
}
