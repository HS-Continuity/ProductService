package com.yeonieum.productservice.domain.productinventory.dto;


import com.yeonieum.productservice.domain.productinventory.entity.ProductInventory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
@Getter
@Builder
public class RetrieveProductInventoryResponse {
    Long productInventoryId;
    Long productId;
    String productName;
    LocalDate warehouseDate;
    int quantity;
    LocalDate expirationDate;

    public RetrieveProductInventoryResponse(Long productInventoryId, Long productId, String productName, LocalDate warehouseDate, int quantity, LocalDate expirationDate) {
        this.productInventoryId = productInventoryId;
        this.productId = productId;
        this.productName = productName;
        this.warehouseDate = warehouseDate;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

    public static RetrieveProductInventoryResponse toEntity(ProductInventory productInventory) {
        return RetrieveProductInventoryResponse.builder()
                .expirationDate(productInventory.getExpirationDate())
                .productId(productInventory.getProduct().getProductId())
                .productInventoryId(productInventory.getProductInventoryId())
                .productName(productInventory.getProduct().getProductName())
                .quantity(productInventory.getQuantity())
                .warehouseDate(productInventory.getWarehouseDate())
                .build();
    }

}
