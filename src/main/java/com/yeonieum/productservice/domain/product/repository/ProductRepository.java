package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.yeonieum.productservice.global.enums.ActiveStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p " +
            "JOIN FETCH p.productDetailCategory pdc " +
            "JOIN FETCH pdc.productCategory pc " +
            "WHERE p.id = :productId")
    Optional<Product> findProductWithCategoryInfoByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT p.* FROM product p " +
            "JOIN customer c ON p.customer_id = c.customer_id " +
            "WHERE c.customer_id = :customerId " +
            "AND (:isEcoFriend IS NULL OR :isEcoFriend = '' OR p.is_certification = :isEcoFriend)", nativeQuery = true)
    Page<Product> findProductsWithCustomersByStatus(@Param("customerId") Long customerId,
                                                    @Param("isEcoFriend") Character isEcoFriend,
                                                    Pageable pageable);

    @Query(value = "SELECT pts" +
            "FROM product_time_sale pts " +
            "JOIN product p ON pts.product_id = p.id " +
            "WHERE p.customer_id = :customerId", nativeQuery = true)
    List<ProductTimesale> findAllTimesaleByCustomerId(@Param("customerId") Long customerId);


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

    @Query("SELECT p FROM Product p WHERE p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE")
    Page<Product> findAllByIsActive(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.productDetailCategory.productDetailCategoryId = :productDetailCategoryId " +
            "AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE " +
            "AND (:isCertification IS NULL OR p.isCertification = :isCertification)")
    Page<Product> findActiveProductsByDetailCategoryId(@Param("productDetailCategoryId") Long productDetailCategoryId,
                                                       @Param("isCertification") ActiveStatus isCertification,
                                                       Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.customer.customerId = :customerId " +
            "AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE " +
            "AND (:detailCategoryId IS NULL OR p.productDetailCategory.productDetailCategoryId = :detailCategoryId)")
    Page<Product> findByCustomerIdAndIsActiveAndCategoryId(@Param("customerId") Long customerId,
                                                           @Param("detailCategoryId") Long detailCategoryId,
                                                           Pageable pageable);
}
