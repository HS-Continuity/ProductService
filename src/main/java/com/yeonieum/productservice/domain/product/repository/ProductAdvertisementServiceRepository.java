package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductAdvertisementServiceRepository extends JpaRepository<ProductAdvertisementService, Long> {
    @Query("SELECT pas " +
            "FROM ProductAdvertisementService pas " +
            "JOIN pas.product p " +
            "WHERE pas.isCompleted = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE " +
            "ORDER BY FUNCTION('RAND') " +
            "LIMIT 3")
    List<ProductAdvertisementService> findRandomActiveAdvertisementProducts();

    @Query(value = "SELECT pas" +
            "FROM ProductAdvertisementService pas " +
            "JOIN product p ON pts.product_id = p.id " +
            "WHERE p.customer_id = :customerId", nativeQuery = true)
    List<ProductAdvertisementService> findAllAdvertisementProduct(@Param("customerId") Long customerId);
}

