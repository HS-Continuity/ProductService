package com.yeonieum.productservice.domain.review.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.review.dto.ProductReviewRequest;
import com.yeonieum.productservice.domain.review.dto.ProductReviewResponse;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import com.yeonieum.productservice.domain.review.repository.ProductReviewRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 리뷰 등록
     * @param ofRegisterProductReview 상품 리뷰를 등록할 정보 DTO
     * @return 성공 여부
     * @throws IllegalArgumentException 존재하지 않는 상품 ID인 경우
     * @throws IllegalStateException    해당 상품에 대한 회원의 리뷰가 이미 존재하는 경우
     */
    @Transactional
    public boolean registerProductReview(ProductReviewRequest.OfRegisterProductReview ofRegisterProductReview, @Nullable String imageUrl) {

        //상품을 구매한 회원인지에 대한 로직 필요

        Product product = productRepository.findById(ofRegisterProductReview.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID 입니다."));

        if (productReviewRepository.existsByMemberId(ofRegisterProductReview.getMemberId())) {
            throw new IllegalStateException("이미 해당 회원이 작성한 리뷰가 존재합니다.");
        }

        ProductReview productReview =  ofRegisterProductReview.toEntity(product, imageUrl);

        productReviewRepository.save(productReview);
        return true;
    }

    /**
     * 상품 리뷰 삭제
     * @param productReviewId 상품 리뷰 ID
     * @throws IllegalArgumentException 존재하지 않는 상품 리뷰 ID인 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean deleteProductReview(Long productReviewId) {

        if (productReviewRepository.existsById(productReviewId)) {
            productReviewRepository.deleteById(productReviewId);
            return true;
        } else {
            throw new IllegalArgumentException("존재하지 않는 상품리뷰 ID 입니다.");
        }
    }

    /**
     * 선택한 상품 조회시, 해당 상품의 리뷰 조회
     * @param productId 상품 ID
     * @throws IllegalArgumentException 존재하지 않는 상품 ID인 경우
     * @return 상품리뷰에 대한 정보
     */
    @Transactional
    public Page<ProductReviewResponse.OfRetrieveProductWithReview> retrieveProductWithReviews(Long productId, Pageable pageable) {

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("존재하지 않는 상품 ID 입니다.");
        }
        Page<ProductReview> productReviews = productReviewRepository.findByProductId(productId, pageable);

        return productReviews.map(ProductReviewResponse.OfRetrieveProductWithReview::convertedBy);
    }
}
