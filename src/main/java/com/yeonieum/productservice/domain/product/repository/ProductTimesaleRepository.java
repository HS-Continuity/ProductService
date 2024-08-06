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
            "JOIN FETCH pts.product p where pts.serviceStatus.statusName = 'IN_PROGRESS'")
    Page<ProductTimesale> findAllTimesaleProduct(Pageable pageable);

    @Query("SELECT pts FROM ProductTimesale pts " +
            "JOIN FETCH pts.product p " +
            "WHERE pts.productTimesaleId = :productTimesaleId")
    Optional<ProductTimesale> findByProductTimesaleId(@Param("productTimesaleId") Long productTimesaleId);

    // productId 중에서 servicestatus가 pending혹은 active인 것이 존재하는지 확인한는 쿼리
    @Query("SELECT COUNT(pts) > 0 FROM ProductTimesale pts " +
            "JOIN pts.product p " +
            "WHERE p.productId = :productId " +
            "AND pts.serviceStatus.statusName IN ('PENDING', 'APPROVE', 'IN_PROGRESS')")
    boolean existsByProductIdAndServiceStatus(@Param("productId") Long productId);

    @Query(value = "SELECT pts " +
            "FROM ProductTimesale  pts " +
            "JOIN Product p ON pts.product = p " +
            "WHERE p.customer.customerId = :customerId")
    List<ProductTimesale> findAllTimesaleByCustomerId(@Param("customerId") Long customerId);
}
