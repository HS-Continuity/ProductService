package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
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

        public ProductAdvertisementService toEntity(Product product, ServiceStatus status) {
            return ProductAdvertisementService.builder()
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .product(product)
                    .serviceStatus(status)
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


    @Getter
    @NoArgsConstructor
    public static class OfModifyStatus {

    }
}
