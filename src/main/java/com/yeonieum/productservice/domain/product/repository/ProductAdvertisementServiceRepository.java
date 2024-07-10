package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.dto.memberservice.RetrieveAdvertisementProductResponse;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductAdvertisementServiceRepository extends JpaRepository<ProductAdvertisementService, Long> {
    @Query("SELECT NEW com.yeonieum.productservice.domain.product.dto.memberservice.RetrieveAdvertisementProductResponse(" +
            "p.productId, p.productName, p.productPrice, p.baseDiscountRate, p.productImage) " +
            "FROM ProductAdvertisementService pas " +
            "JOIN pas.product p " +
            "WHERE pas.isCompleted = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE " +
            "ORDER BY FUNCTION('RAND') " +
            "LIMIT 3")
    List<RetrieveAdvertisementProductResponse> findRandomActiveAdvertisementProducts();
}

