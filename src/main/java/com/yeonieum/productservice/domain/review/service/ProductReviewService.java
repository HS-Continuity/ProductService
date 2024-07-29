package com.yeonieum.productservice.domain.review.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.review.dto.ProductReviewRequest;
import com.yeonieum.productservice.domain.review.dto.ProductReviewResponse;
import com.yeonieum.productservice.domain.review.entity.ProductReview;
import com.yeonieum.productservice.domain.review.exception.ProductReviewException;
import com.yeonieum.productservice.domain.review.repository.ProductReviewRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.PRODUCT_NOT_FOUND;
import static com.yeonieum.productservice.domain.review.exception.ProductReviewExceptionCode.PRODUCT_REVIEW_NOT_FOUND;
import static com.yeonieum.productservice.domain.review.exception.ProductReviewExceptionCode.REVIEW_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 리뷰 등록
     * @param ofRegisterProductReview 상품 리뷰를 등록할 정보 DTO
     * @return 성공 여부
     * @throws ProductException 존재하지 않는 상품 ID인 경우
     * @throws ProductReviewException 해당 상품에 대한 회원의 리뷰가 이미 존재하는 경우
     */
    @Transactional
    public boolean registerProductReview(ProductReviewRequest.OfRegisterProductReview ofRegisterProductReview, @Nullable String imageUrl) {

        //상품을 구매한 회원인지에 대한 로직 필요

        Product product = productRepository.findById(ofRegisterProductReview.getProductId())
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (productReviewRepository.existsByMemberId(ofRegisterProductReview.getMemberId())) {
            throw new ProductReviewException(REVIEW_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        ProductReview productReview =  ofRegisterProductReview.toEntity(product, imageUrl);

        productReviewRepository.save(productReview);
        return true;
    }

    /**
     * 상품 리뷰 삭제
     * @param productReviewId 상품 리뷰 ID
     * @throws ProductReviewException 존재하지 않는 상품 리뷰 ID인 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean deleteProductReview(Long productReviewId) {

        if (productReviewRepository.existsById(productReviewId)) {
            productReviewRepository.deleteById(productReviewId);
            return true;
        } else {
            throw new ProductReviewException(PRODUCT_REVIEW_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 선택한 상품 조회시, 해당 상품의 리뷰 조회
     * @param productId 상품 ID
     * @throws ProductException 존재하지 않는 상품 ID인 경우
     * @return 상품리뷰에 대한 정보
     */
    @Transactional
    public Page<ProductReviewResponse.OfRetrieveProductWithReview> retrieveProductWithReviews(Long productId, Pageable pageable) {

        if (!productRepository.existsById(productId)) {
            throw new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        Page<ProductReview> productReviews = productReviewRepository.findByProductId(productId, pageable);

        return productReviews.map(ProductReviewResponse.OfRetrieveProductWithReview::convertedBy);
    }
}
