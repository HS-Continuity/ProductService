package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.productinventory.dto.RetrieveProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.entity.ProductInventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    @Query(value = "SELECT" +
            "new com.yeonieum.productservice.domain.product.dto.RetrieveProductInventoryResponse" +
            "(pi.product_inventory_id, pi.product_id, pi.product_name,pi.warehousing_date, pi.quantity, pi.expiration_date)" +
            "FROM product_inventory pi " +
            "WHERE pi.product_id = :productId",
            countQuery = "SELECT count(*) FROM product_inventory pi WHERE pi.product_id = :productId",
            nativeQuery = true)
    List<RetrieveProductInventoryResponse> findAllbyProductId(@Param("productId") Long productId , Pageable pageable);
}