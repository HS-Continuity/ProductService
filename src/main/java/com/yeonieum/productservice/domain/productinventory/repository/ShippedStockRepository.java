package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.ShippedStock;
import com.yeonieum.productservice.infrastructure.cache.data.StockUsageCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ShippedStockRepository extends JpaRepository<ShippedStock, String> {

    @Query("SELECT new com.yeonieum.productservice.infrastructure.cache.data.StockUsageCache(s.orderDetailId, s.productId, s.id) " +
            "FROM ShippedStock s " +
            "WHERE s.productId = :productId AND s.shippedDateTime < :shippedTime")
    Set<StockUsageCache> findShippedStockBeforeTodayShippedTime(@Param("productId") Long productId, @Param("shippedTime") LocalDateTime shippedTime);
    @Query(value = "SELECT id, product_id, shipped_date_time, id FROM shipped_stock WHERE product_id = :productId AND shipped_date_time >= :shippedTime", nativeQuery = true)
    List<ShippedStockDto> findShippedStockAfterTodayShippedTime(@Param("productId") Long productId, @Param("shippedTime") LocalDateTime shippedTime);
}
