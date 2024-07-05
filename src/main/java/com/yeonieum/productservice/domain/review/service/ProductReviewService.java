package com.yeonieum.productservice.domain.review.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.review.dto.ProductReviewRequest;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import com.yeonieum.productservice.domain.review.repository.ProductReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 리뷰 등록
     * @param registerProductReviewDto
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean registerProductReview(ProductReviewRequest.RegisterProductReviewDto registerProductReviewDto){
        Product product = productRepository.findById(registerProductReviewDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품리뷰 ID 입니다."));

        ProductReview productReview = ProductReview.builder()
                .product(product)
                .memberId(registerProductReviewDto.getMemberId())
                .createDate(registerProductReviewDto.getCreateDate())
                .reviewContent(registerProductReviewDto.getReviewContent())
                .reviewImage(registerProductReviewDto.getReviewImage())
                .reviewScore(registerProductReviewDto.getReviewScore())
                .build();

        productReviewRepository.save(productReview);
        return true;
    }

    /**
     * 상품 리뷰 삭제
     * @param productReviewId
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean deleteProductReview(Long productReviewId){
        if (productReviewRepository.existsById(productReviewId)) {
            productReviewRepository.deleteById(productReviewId);
            return true;
        } else {
            throw new IllegalArgumentException("존재하지 않는 상품리뷰 ID 입니다.");
        }
    }
}
