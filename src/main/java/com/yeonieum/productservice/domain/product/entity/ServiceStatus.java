package com.yeonieum.productservice.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "service_status")
public class ServiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_status_id")
    private Long serviceStatusId;

    @Column(name = "status_name", nullable = false)
    private String statusName;

    @OneToMany(mappedBy = "serviceStatus")
    @Builder.Default
    private List<ProductAdvertisementService> productAdvertisementServiceList = new ArrayList<>();

    @OneToMany(mappedBy = "serviceStatus")
    @Builder.Default
    private List<ProductTimesale> productTimesaleList = new ArrayList<>();
}
