package com.yeonieum.productservice.domain.image.dto;

import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import lombok.Builder;
import lombok.Getter;

public class ImageResponse {

    @Getter
    @Builder
    public static class OfRetrieveDetailImage {

        private Long productDetailImageId;
        private String detailImage;

        public static OfRetrieveDetailImage convertedBy(ProductDetailImage productDetailImage) {
            return OfRetrieveDetailImage.builder()
                    .productDetailImageId(productDetailImage.getProductDetailImageId())
                    .detailImage(productDetailImage.getDetailImage())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OfRetrieveCertification {

        private Long productCertificationId;
        private String certificationName;
        private String certificationNumber;
        private String certificationImage;

        public static OfRetrieveCertification convertedBy(ProductCertification productCertification) {
            return OfRetrieveCertification.builder()
                    .productCertificationId(productCertification.getProductCertificationId())
                    .certificationName(productCertification.getCertificationName())
                    .certificationNumber(productCertification.getCertificationNumber())
                    .certificationImage(productCertification.getCertificationImage())
                    .build();
        }
    }
}
