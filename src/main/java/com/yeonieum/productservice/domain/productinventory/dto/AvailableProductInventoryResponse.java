package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Builder;

@Builder
public class AvailableProductInventoryResponse {
    Long orderId;
    Long productId;
    int quantity;
    @Builder.Default
    boolean isAvailableOrder = false;


    public void changeIsAvailableOrder(boolean status){
        this.isAvailableOrder = status;
    }
}
