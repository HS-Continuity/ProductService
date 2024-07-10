package com.yeonieum.productservice.domain.product.entity;

import com.yeonieum.productservice.domain.review.entity.ProductReview;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "add_service_status")
public class AddServiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "add_service_status_id")
    private Long addServiceStatusId;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    @OneToMany(mappedBy = "addServiceStatus")
    @Builder.Default
    private List<ProductAdvertisementService> productAdvertisementServiceList = new ArrayList<>();

    @OneToMany(mappedBy = "addServiceStatus")
    @Builder.Default
    private List<ProductTimeSale> productTimeSaleList = new ArrayList<>();
}
