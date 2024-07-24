package com.yeonieum.productservice.domain.productinventory.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AvailableProductInventoryResponse {
    String orderDetailId;
    Long productId;
    int quantity;
    @Builder.Default
    boolean isAvailableOrder = false;

    public void changeIsAvailableOrder(boolean status){
        this.isAvailableOrder = status;
    }
    public boolean getIsAvailableOrder(){
        return this.isAvailableOrder;
    }
}
