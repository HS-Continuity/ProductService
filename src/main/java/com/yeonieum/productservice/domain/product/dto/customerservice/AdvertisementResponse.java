package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class AdvertisementResponse {
    @Getter
    @Builder
    public static class OfRegister {
        private Long productId;
        private String productName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String serviceStatus;

        public static OfRegister convertedBy(ProductAdvertisementService productAdvertisementService) {
            return OfRegister.builder()
                    .productId(productAdvertisementService.getProduct().getProductId())
                    .productName(productAdvertisementService.getProduct().getProductName())
                    .endDate(productAdvertisementService.getEndDate())
                    .startDate(productAdvertisementService.getStartDate())
                    .serviceStatus(productAdvertisementService.getServiceStatus().getStatusName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OfRetrieve {
        Long productId;
        String productName;
        int price;
        int discountRate;
        String productImage;
        LocalDate startDate;
        LocalDate endDate;
        String serviceStatus;

        public static OfRetrieve convertedBy(ProductAdvertisementService productAdvertisementService) {
            Product product = productAdvertisementService.getProduct();
            return OfRetrieve.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .discountRate(product.getBaseDiscountRate())
                    .price(product.getProductPrice())
                    .productImage(product.getProductImage())
                    .startDate(productAdvertisementService.getStartDate())
                    .endDate(productAdvertisementService.getEndDate())
                    .serviceStatus(productAdvertisementService.getServiceStatus().getStatusName())
                    .build();
        }
    }
}