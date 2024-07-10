package com.yeonieum.productservice.domain.product.dto.memberservice;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetrieveAdvertisementProductResponse {
    Long productId;
    String productName;
    int price;
    int discountRate;
    String productImage;
}
