package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.StockUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockUsageRepository extends JpaRepository<StockUsage, Long> {
    @Query("SELECT new com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto(s.productId, s.orderId, s.quantity) " +
            "FROM StockUsage s WHERE s.productId = :productId")
    List<StockUsageDto> findShippedStockByProductId(@Param("productId") Long productId);


    List<StockUsage> findAllByproductId(Long productId);
}
