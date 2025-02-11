package com.yeonieum.productservice.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_certification")
public class ProductCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_certification_id")
    private Long productCertificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "certification_name", nullable = false)
    private String certificationName;

    @Column(name = "certification_number", nullable = false)
    private String certificationNumber;

    @Column(name = "certification_image", nullable = false, length = 900)
    private String certificationImage;

    public void changeCertificationImage(String certificationImage) {
        this.certificationImage = certificationImage;
    }
}

