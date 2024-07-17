package com.yeonieum.productservice.domain.product.entity;

import com.yeonieum.productservice.global.converter.ActiveStatusConverter;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_time_sale")
public class ProductTimesale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_time_sale_id")
    private Long productTimesaleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_status_id", nullable = false)
    private ServiceStatus serviceStatus;

    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDatetime;

    @Column(name = "discount_rate")
    private int discountRate;

    @Convert(converter = ActiveStatusConverter.class)
    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private ActiveStatus isCompleted = ActiveStatus.INACTIVE;

    public void changeIsCompleted(ActiveStatus isCompleted) {
        this.isCompleted = isCompleted;
    }

}
