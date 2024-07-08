package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.StockUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockUsageRepository extends JpaRepository<StockUsage, Long> {
    @Query(value = "SELECT product_id, order_id, quantity FROM stock_usage WHERE product_id = :productId", nativeQuery = true)
    List<StockUsageDto> findShippedStockByProductId(@Param("productId") Long productId);
    List<StockUsage> findAllByproductId(Long productId);
}
