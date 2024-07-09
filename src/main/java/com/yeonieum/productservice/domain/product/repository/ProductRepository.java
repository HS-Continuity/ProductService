package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveAdvertisementProductResponseDto;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleProductResponseDto;
import com.yeonieum.productservice.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productReviewList WHERE p.productId = :productId")
    Optional<Product> findByIdWithReviews(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.productDetailCategory pdc " +
            "JOIN FETCH pdc.productCategory pc " +
            "WHERE pc.productCategoryId = :categoryId")
    List<Product> findProductsByCategory(@Param("categoryId") Long categoryId);
}