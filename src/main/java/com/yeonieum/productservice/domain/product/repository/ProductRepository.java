package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.dto.memberservice.RetrieveAdvertisementProductResponseDto;
import com.yeonieum.productservice.domain.product.dto.memberservice.RetrieveTimeSaleProductResponseDto;
import com.yeonieum.productservice.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT" +
            "new com.yeonieum.productservice.domain.product.dto.RetrieveTimeSaleProductResponseDto" +
            "(p.product_id, p.product_name, p.price, pts.discount_rate, pts.start_datetime, pts.end_datetime, pts.is_completed)" +
            "FROM product_time_sale pts " +
            "JOIN product p ON pts.product_id = p.id " +
            "WHERE p.customer_id = :customerId", nativeQuery = true)
    List<RetrieveTimeSaleProductResponseDto> findAllTimeSaleProduct(@Param("customerId") Long customerId);

    @Query(value = "SELECT" +
            "new com.yeonieum.productservice.domain.product.dto.RetrieveAdvertisementProductResponseDto" +
            "(p.product_id, p.product_name,pts.start_date, pts.end_date, pts.is_completed)" +
            "FROM product_time_sale pts " +
            "JOIN product p ON pts.product_id = p.id " +
            "WHERE p.customer_id = :customerId", nativeQuery = true)
    List<RetrieveAdvertisementProductResponseDto> findAllAdvertisementProduct(@Param("customerId") Long customerId);
}

