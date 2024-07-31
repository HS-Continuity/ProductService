package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.entity.StockUsage;
import com.yeonieum.productservice.infrastructure.cache.data.StockUsageCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface StockUsageRepository extends JpaRepository<StockUsage, String> {
    @Query("SELECT new com.yeonieum.productservice.infrastructure.cache.data.StockUsageCache(s.orderDetailId, s.productId, s.id) " +
            "FROM StockUsage s WHERE s.productId = :productId")
    Set<StockUsageCache> findShippedStockByProductId(@Param("productId") Long productId);

    void deleteByProductIdAndOrderDetailId(Long productId, String orderDetailId);
    List<StockUsage> findAllByproductId(Long productId);
}
