package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveAdvertisementProductResponseDto;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.yeonieum.productservice.global.enums.ActiveStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT" +
            "new com.yeonieum.productservice.domain.product.dto.RetrieveTimeSaleProductResponseDto" +
            "(p.product_id, p.product_name, p.price, pts.discount_rate, pts.start_datetime, pts.end_datetime, pts.is_completed)" +
            "FROM product_time_sale pts " +
            "JOIN product p ON pts.product_id = p.id " +
            "WHERE p.customer_id = :customerId", nativeQuery = true)
    List<RetrieveTimeSaleResponse> findAllTimeSaleByCustomerId(@Param("customerId") Long customerId);

    @Query(value = "SELECT" +
            "new com.yeonieum.productservice.domain.product.dto.RetrieveTimeSaleProductResponseDto" +
            "(p.product_id, p.product_name, p.price, pts.discount_rate, pts.start_datetime, pts.end_datetime, pts.is_completed)" +
            "FROM product_time_sale pts " +
            "JOIN product p ON pts.product_id = p.id ", nativeQuery = true)
    List<RetrieveTimeSaleResponse> findAllTimeSaleProduct(Pageable pageable);


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
            "WHERE pc.productCategoryId = :categoryId " +
            "AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE " +
            "AND (:isCertification IS NULL OR p.isCertification = :isCertification)")
    Page<Product> findActiveProductsByCategory(@Param("categoryId") Long categoryId, @Param("isCertification") ActiveStatus isCertification, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.productId = :productId AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE")
    Optional<Product> findByIdAndIsActive(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE")
    Page<Product> findByProductNameContainingAndIsActive(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.productDetailCategory.productDetailCategoryId = :productDetailCategoryId " +
            "AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE " +
            "AND (:isCertification IS NULL OR p.isCertification = :isCertification)")
    Page<Product> findActiveProductsByDetailCategoryId(@Param("productDetailCategoryId") Long productDetailCategoryId,
                                                       @Param("isCertification") ActiveStatus isCertification,
                                                       Pageable pageable);


}
