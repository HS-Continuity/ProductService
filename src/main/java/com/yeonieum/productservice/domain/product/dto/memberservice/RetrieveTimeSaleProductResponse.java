package com.yeonieum.productservice.domain.product.dto.memberservice;

import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetrieveTimeSaleProductResponse {
    private RetrieveTimeSaleResponse retrieveTimeSaleResponse;
    private double averageRating;
    private int reviewCount;
}
