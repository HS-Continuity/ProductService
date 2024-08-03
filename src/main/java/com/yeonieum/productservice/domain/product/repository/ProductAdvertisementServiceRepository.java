package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.global.enums.ServiceStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductAdvertisementServiceRepository extends JpaRepository<ProductAdvertisementService, Long> {

    @Query("SELECT pas " +
            "FROM ProductAdvertisementService pas " +
            "JOIN pas.product p " +
            "WHERE pas.serviceStatus.statusName = :statusName " +
            "ORDER BY FUNCTION('RAND')")
    List<ProductAdvertisementService> findRandomActiveAdvertisements(@Param("statusName") String statusName);


    @Query(value = "SELECT pas.* " +
            "FROM product_advertisement_service pas " +
            "JOIN product p ON pas.product_id = p.product_id " +
            "WHERE p.customer_id = :customerId", nativeQuery = true)
    List<ProductAdvertisementService> findAllAdvertisementProduct(@Param("customerId") Long customerId);

    // Product와 fetchjoin을 통해 가져옴, productAdvertiesementId와 customerId로 조회
    @Query("SELECT pas " +
            "FROM ProductAdvertisementService pas " +
            "JOIN FETCH pas.product p " +
            "WHERE pas.productAdvertisementServiceId = :productAdvertisementId " +
            "AND p.customer.customerId = :customerId")
    ProductAdvertisementService findByIdAndCustomerId(@Param("productAdvertisementId") Long productAdvertisementId, @Param("customerId") Long customerId);


}

