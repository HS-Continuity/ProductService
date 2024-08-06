package com.yeonieum.productservice.domain.product.dto.customerservice;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductAdvertisementService;
import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import com.yeonieum.productservice.infrastructure.messaging.message.AdvertisementEventMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class AdvertisementRequest {
    @Getter
    @NoArgsConstructor
    public static class OfRegister {

        private Long productId;

        @NotBlank(message = "상품이름은 필수입니다.")
        @Size(max = 80, message = "상품이름은 최대 80자까지 가능합니다.")
        private String productName;

        @NotNull(message = "광고 시작날짜 입력은 필수입니다.")
        private LocalDate startDate;

        @NotNull(message = "광고 종료날짜 입력은 필수입니다.")
        private LocalDate endDate;

        public ProductAdvertisementService toEntity(Product product, ServiceStatus status) {
            return ProductAdvertisementService.builder()
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .product(product)
                    .serviceStatus(status)
                    .build();
        }

        public AdvertisementEventMessage toEventMessage(Long id) {
            return AdvertisementEventMessage.builder()
                    .productId(id)
                    .startDate(this.getStartDate())
                    .endDate(this.getEndDate())
                    .build();
        }
    }
}
