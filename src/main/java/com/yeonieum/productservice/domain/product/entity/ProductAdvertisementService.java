package com.yeonieum.productservice.domain.product.entity;

import com.yeonieum.productservice.global.converter.ActiveStatusConverter;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_advertisement_service")
public class ProductAdvertisementService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_advertisement_service_id")
    private Long productAdvertisementServiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "add_service_status_id", nullable = false)
    private AddServiceStatus addServiceStatus;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private ActiveStatus isCompleted = ActiveStatus.INACTIVE;

}
