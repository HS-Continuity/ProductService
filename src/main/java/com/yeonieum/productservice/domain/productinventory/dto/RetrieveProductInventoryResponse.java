package com.yeonieum.productservice.domain.productinventory.dto;


import java.time.LocalDate;

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

}
