package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleResponse;
import com.yeonieum.productservice.domain.product.entity.ProductTimeSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductTimeSaleRepository extends JpaRepository<ProductTimeSale, Long> {
    ProductTimeSale findByProduct_ProductId(Long productId);
    @Query("SELECT new com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleResponse(" +
            "p.productId, p.productName, p.productPrice, pts.discountRate, " +
            "pts.startDatetime, pts.endDatetime, pts.isCompleted) " +
            "FROM ProductTimeSale pts " +
            "JOIN pts.product p " +
            "WHERE pts.productTimeSaleId = :productTimeSaleId")
    RetrieveTimeSaleResponse findTimeSaleProduct(@Param("productTimeSaleId") Long productTimeSaleId);
}
