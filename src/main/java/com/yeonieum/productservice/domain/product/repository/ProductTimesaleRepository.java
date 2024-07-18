package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ProductTimesale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductTimesaleRepository extends JpaRepository<ProductTimesale, Long> {
    ProductTimesale findByProduct_ProductId(Long productId);
    @Query("SELECT pts FROM ProductTimesale pts " +
            "LEFT JOIN pts.product p WHERE pts.productTimesaleId = :timesaleId")
    ProductTimesale findByIdWithProduct(@Param("timesaleId") Long productTimesaleId);

    @Query("SELECT pts FROM ProductTimesale pts " +
            "JOIN FETCH pts.product p")
    Page<ProductTimesale> findAllTimesaleProduct(Pageable pageable);
}
