package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.messaging.message.AdvertisementEventMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public class AdvertisementRequest {
    @Getter
    @NoArgsConstructor
    public static class OfRegister {
        private Long productId;
        private String productName;
        private LocalDate startDate;
        private LocalDate endDate;
        private ActiveStatus isCompleted;

        public ProductAdvertisementService toEntity(Product product) {
            return ProductAdvertisementService.builder()
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .product(product)
                    .isCompleted(this.isCompleted)
                    .build();
        }


        public AdvertisementEventMessage toEventMessage() {
            return AdvertisementEventMessage.builder()
                    .productId(this.getProductId())
                    .startDate(this.getStartDate())
                    .endDate(this.getEndDate())
                    .build();
        }
    }
}

