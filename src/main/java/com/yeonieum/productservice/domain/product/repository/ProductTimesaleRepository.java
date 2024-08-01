package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductTimesaleRepository extends JpaRepository<ProductTimesale, Long> {
    ProductTimesale findByProduct_ProductId(Long productId);
    @Query("SELECT pts FROM ProductTimesale pts " +
            "LEFT JOIN pts.product p WHERE pts.productTimesaleId = :timesaleId")
    ProductTimesale findByIdWithProduct(@Param("timesaleId") Long productTimesaleId);

    @Query("SELECT pts FROM ProductTimesale pts " +
            "JOIN FETCH pts.product p")
    Page<ProductTimesale> findAllTimesaleProduct(Pageable pageable);

    @Query("SELECT pts FROM ProductTimesale pts " +
            "JOIN FETCH pts.product p " +
            "WHERE pts.productTimesaleId = :productTimesaleId")
    Optional<ProductTimesale> findByProductTimesaleId(@Param("productTimesaleId") Long productTimesaleId);

    @Query(value = "SELECT pts " +
            "FROM ProductTimesale  pts " +
            "JOIN Product p ON pts.product = p " +
            "WHERE p.customer.customerId = :customerId")
    List<ProductTimesale> findAllTimesaleByCustomerId(@Param("customerId") Long customerId);
}
