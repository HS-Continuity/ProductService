package com.yeonieum.productservice.domain.image.service;

import com.yeonieum.productservice.domain.image.dto.ImageResponse;
import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductCertificationRepository;
import com.yeonieum.productservice.domain.product.repository.ProductDetailImageRepository;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ProductRepository productRepository;
    private final ProductDetailImageRepository productDetailImageRepository;
    private final ProductCertificationRepository productCertificationRepository;

    /**
     * 선택한 상품 조회시, 해당 상품의 상세 이미지 조회
     * @param productId 상품 ID
     * @throws ProductException 존재하지 않는 상품 ID인 경우
     * @return 상품 상세 이미지에 대한 정보
     */
    @Transactional
    public List<ImageResponse.OfRetrieveDetailImage> retrieveProductDetailImages(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        List<ProductDetailImage> productDetailImages = productDetailImageRepository.findByProductId(productId);

        return productDetailImages.stream().map(ImageResponse.OfRetrieveDetailImage::convertedBy).collect(Collectors.toList());
    }

    /**
     * 선택한 상품 조회시, 해당 상품의 인증서 정보 조회
     * @param productId 상품 ID
     * @throws ProductException 존재하지 않는 상품 ID인 경우
     * @return 상품 인증서에 대한 정보
     */
    @Transactional
    public ImageResponse.OfRetrieveCertification retrieveProductCertification(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        ProductCertification productCertification = productCertificationRepository.findByProductId(productId);

        return ImageResponse.OfRetrieveCertification.convertedBy(productCertification);
    }
}
