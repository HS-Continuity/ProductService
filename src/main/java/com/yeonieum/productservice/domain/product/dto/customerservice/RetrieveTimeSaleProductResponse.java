package com.yeonieum.productservice.domain.product.dto.customerservice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetrieveTimeSaleProductResponse {
    private RetrieveTimeSaleResponse retrieveTimeSaleResponse;
    private double averageRating;
    private int reviewCount;

}
