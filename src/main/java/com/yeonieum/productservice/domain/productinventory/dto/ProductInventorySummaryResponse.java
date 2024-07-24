package com.yeonieum.productservice.domain.productinventory.dto;


public class ProductInventorySummaryResponse {
    Long productId;
    String productName;
    Long totalQuantity;

    public ProductInventorySummaryResponse(Long productId, String productName, Long totalQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }

    public ProductInventorySummaryResponse() {
    }
}
