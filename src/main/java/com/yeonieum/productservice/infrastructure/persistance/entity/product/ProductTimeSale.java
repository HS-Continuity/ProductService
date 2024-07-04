package com.yeonieum.productservice.infrastructure.persistance.entity.product;

import com.yeonieum.productservice.infrastructure.persistance.commons.converter.ActiveStatusConverter;
import com.yeonieum.productservice.infrastructure.persistance.commons.enums.ActiveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_time_sale")
public class ProductTimeSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_time_sale_id")
    private Long productTimeSaleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

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

}
