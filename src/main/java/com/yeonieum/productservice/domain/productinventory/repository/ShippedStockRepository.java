package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.ShippedStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShippedStockRepository extends JpaRepository<ShippedStock, Long> {
    @Query(value = "SELECT product_id, order_id, quantity FROM shipped_stock WHERE product_id = :productId AND shipped_date_time < :shippedTime", nativeQuery = true)
    List<StockUsageDto> findShippedStockBeforeTodayShippedTime(@Param("productId") Long productId, @Param("shippedTime") LocalDateTime shippedTime);
    @Query(value = "SELECT id, product_id, shipped_date_time, quantity FROM shipped_stock WHERE product_id = :productId AND shipped_date_time >= :shippedTime", nativeQuery = true)
    List<ShippedStockDto> findShippedStockAfterTodayShippedTime(@Param("productId") Long productId, @Param("shippedTime") LocalDateTime shippedTime);
}
