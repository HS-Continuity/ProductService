package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.productinventory.dto.ProductInventorySummaryResponse;
import com.yeonieum.productservice.domain.productinventory.dto.RetrieveProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.entity.ProductInventory;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    @Query(value = "SELECT pi " +
            "FROM ProductInventory pi " +
            "WHERE pi.product.productId = :productId")
    List<ProductInventory> findAllbyProductId(@Param("productId") Long productId , Pageable pageable);

    //@Cacheable(value = "availableQuantity", key = "#productId + '-' + #expirationDate", cacheManager = "servercacheManager")
    @Query(value = "SELECT COALESCE(SUM(pi.quantity), 0) FROM product_inventory pi WHERE pi.product_id = :productId AND pi.expiration_date > :expirationDate", nativeQuery = true)
    int findAvailableInventoryQuantityByProductIdAndExpirationDate(@Param("productId") Long productId, @Param("expirationDate") LocalDate expirationDate);

    @Query(
            value = "SELECT p.product_id AS productId, " +
                    "p.product_name AS productName, " +
                    "COALESCE(SUM(pi.quantity), 0) AS totalQuantity " +
                    "FROM product_inventory pi " +
                    "JOIN product p ON pi.product_id = p.product_id " +
                    "WHERE p.customer_id = :customerId " +
                    "AND pi.expiration_date > :today " +
                    "GROUP BY p.product_id, p.product_name",
            nativeQuery = true
    )
    Page<Object[]> findInventorySumByProductsAndExpirations(@Param("customerId") Long customerId, @Param("today") LocalDate today, Pageable pageable);
    @Query(value = "SELECT COALESCE(SUM(pi.quantity), 0) " +
            "FROM product_inventory pi " +
            "WHERE pi.product_id IN :productIds " +
            "AND pi.expiration_date IN :expirationDates", nativeQuery = true)
    List<Integer> bulkFindAvailableInventoryQuantityByProductIdAndExpirationDate(@Param("productIds") List<Long> productId, @Param("expirationDates") List<LocalDate> expirationDate);

}