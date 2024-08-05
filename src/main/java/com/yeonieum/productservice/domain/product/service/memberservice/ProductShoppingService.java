package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.exception.CategoryException;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.ProductRepositoryCustomImpl;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.yeonieum.productservice.domain.category.exception.CategoryExceptionCode.*;
import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.*;

@Service
@RequiredArgsConstructor
public class ProductShoppingService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepositoryCustomImpl productRepositoryCustomImpl;

    /**
     * 카테고리의 상품 목록 조회
     * @param productCategoryId 조회할 상품 카테고리 ID
     * @param isCertification 인증된 상품만 조회할지 여부
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기)
     * @throws CategoryException 카테고리 ID가 존재하지 않는 경우
     * @throws ProductException 카테고리에 상품이 하나도 없는 경우
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    @Transactional
    public Page<ProductShoppingResponse.OfRetrieveCategoryWithProduct> retrieveCategoryWithProducts(
            Long productCategoryId,
            ActiveStatus isCertification,
            Pageable pageable) {

        // 카테고리 정보를 조회, 존재하지 않을 경우 예외 발생
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

        // 해당 카테고리의 상품을 조회, 인증 여부와 페이징 정보를 반영
        Page<Product> products = productRepository.findActiveProductsByCategory(productCategoryId, isCertification, pageable);

        // 조회된 상품이 없으면 예외 발생
        if (products.isEmpty()) {
            throw new ProductException(NO_PRODUCTS_IN_CATEGORY, HttpStatus.NOT_FOUND);
        }

        /// DTO 변환: 카테고리 정보 포함
        ProductShoppingResponse.OfRetrieveCategoryWithProduct categoryWithProductsDto
                = ProductShoppingResponse.OfRetrieveCategoryWithProduct.convertedBy(productCategory);

        // 상품 정보 DTO 리스트 변환
        List<ProductShoppingResponse.OfSearchProductInformation> searchProductInformationDtoList = products.stream()
                .map(ProductShoppingResponse.OfSearchProductInformation::convertedBy)
                .collect(Collectors.toList());

        // 상품 정보 리스트를 DTO에 설정
        categoryWithProductsDto.changeOfSearchProductInformationList(searchProductInformationDtoList);

        // DTO를 단일 리스트로 포장하고, 원래 페이지 정보를 사용하여 새 Page 객체 생성
        return new PageImpl<>(Collections.singletonList(categoryWithProductsDto), pageable, products.getTotalElements());
    }

    /**
     * 상세 카테고리의 상품 목록 조회
     * @param productDetailCategoryId 조회할 상세 카테고리의 ID
     * @param isCertification 인증된 상품만 조회할지 여부
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기)
     * @throws CategoryException 상세 카테고리 ID가 존재하지 않는 경우
     * @throws ProductException 상세 카테고리에 상품이 하나도 없는 경우
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    @Transactional
    public Page<ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct> retrieveDetailCategoryWithProducts(
            Long productDetailCategoryId,
            ActiveStatus isCertification,
            Pageable pageable) {

        // 상세 카테고리 정보를 조회, 존재하지 않을 경우 예외 발생
        ProductDetailCategory productDetailCategory = productDetailCategoryRepository.findById(productDetailCategoryId)
                .orElseThrow(() -> new CategoryException(DETAIL_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

        // 해당 상세 카테고리의 상품을 조회, 인증 여부와 페이징 정보를 반영
        Page<Product> products = productRepository.findActiveProductsByDetailCategoryId(
                productDetailCategoryId, isCertification, pageable);

        // 조회된 상품이 없으면 예외 발생
        if (products.isEmpty()) {
            throw new ProductException(NO_PRODUCTS_IN_DETAIL_CATEGORY, HttpStatus.NOT_FOUND);
        }

        /// DTO 변환: 상세 카테고리 정보 포함
        ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct detailCategoryWithProductsDto
                = ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct.convertedBy(productDetailCategory);

        // 상품 정보 DTO 리스트 변환
        List<ProductShoppingResponse.OfSearchProductInformation> productInformationListDto = products.getContent().stream()
                .map(ProductShoppingResponse.OfSearchProductInformation::convertedBy)
                .collect(Collectors.toList());

        // 상품 정보 리스트를 DTO에 설정
        detailCategoryWithProductsDto.changeOfSearchProductInformationList(productInformationListDto);

        // DTO를 단일 리스트로 포장하고, 원래 페이지 정보를 사용하여 새 Page 객체 생성
        return new PageImpl<>(Collections.singletonList(detailCategoryWithProductsDto), pageable, products.getTotalElements());
    }

    /**
     * 상품 상세 정보 조회
     * @param productId 상품 ID
     * @throws ProductException 상품 ID가 존재하지 않는 경우
     * @return 상품의 상세 정보
     */
    @Transactional
    public ProductShoppingResponse.OfDetailProductInformation detailProductInformation(Long productId){

        // 상품 정보를 조회, 존재하지 않을 경우 예외 발생
        Product targetProduct = productRepository.findByIdAndIsActive(productId)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));

        return ProductShoppingResponse.OfDetailProductInformation.convertedBy(targetProduct);
    }

    /**
     * 업체별 상품 조회
     * @param customerId 고객 ID
     * @param detailCategoryId 상세 카테고리 ID
     * @param pageable 페이징 정보
     * @return 업체 상품들의 정보
     */
    @Transactional
    public Page<ProductShoppingResponse.OfSearchProductInformation> retrieveCustomerWithProducts(Long customerId, Long detailCategoryId, Pageable pageable) {

        // 고객 ID와 상세 카테고리 ID로 상품 조회
        Page<Product> products = productRepository.findByCustomerIdAndIsActiveAndCategoryId(customerId, detailCategoryId, pageable);

        return products.map(ProductShoppingResponse.OfSearchProductInformation::convertedBy);
    }

    /**
     * 상품 필터링 조회 (키워드, 친환경)
     * @param keyword 상품 키워드(이름)
     * @param isCertification 인증서 유무
     * @param pageable 페이징 정보
     * @throws ProductException 검색결과가 없는 경우
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    @Transactional
    public Page<ProductShoppingResponse.OfSearchProductInformation> retrieveFilteringProducts(
            String keyword, ActiveStatus isCertification, Pageable pageable) {

        if (keyword != null && !keyword.trim().isEmpty()) {

            // 키워드를 띄어쓰기로 분할
            List<String> keywords = splitKeywords(keyword);

            // 모든 키워드에 대해 검색
            Page<Product> productsPage = productRepositoryCustomImpl.findByKeywords(keywords, pageable);

            // 검색 결과가 존재하는 경우에만 검색어 점수 증가
            if (productsPage.hasContent()) {
                keywords.forEach(this::recordSearchKeyword);
            } else {
                // 검색 결과가 없는 경우 예외를 던짐
                throw new ProductException(NO_SEARCH_RESULTS, HttpStatus.NOT_FOUND);
            }

            return productsPage.map(ProductShoppingResponse.OfSearchProductInformation::convertedBy);

        } else if (isCertification != null) {

            // 인증 상태만 있는 경우 검색
            return productRepository.findActiveCertifiableProductsByProductId(isCertification, pageable)
                    .map(ProductShoppingResponse.OfSearchProductInformation::convertedBy);
        } else {

            // 키워드와 인증 상태 모두 없는 경우 전체 상품 조회
            return productRepository.findAllByIsActive(pageable)
                    .map(ProductShoppingResponse.OfSearchProductInformation::convertedBy);
        }
    }

    /**
     * 입력된 문자열을 공백으로 분할
     * @param keyword 입력 문자열
     * @return 분할된 키워드 리스트
     */
    private List<String> splitKeywords(String keyword) {
        return List.of(keyword.split("\\s+")); // 공백을 기준으로 키워드 분할
    }

    /**
     * 상품 검색 순위를 조회하여 반환
     * @return 상품 검색 순위 리스트
     */
    @Transactional
    public List<ProductShoppingResponse.OfSearchRank> retrieveSearchRank() {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores("currentSearchRank", 0, 9)
                .stream()
                .map(scoredValue -> {
                    Double score = scoredValue.getScore();
                    String value = (String) scoredValue.getValue();
                    return ProductShoppingResponse.OfSearchRank.builder()
                            .searchName(value)
                            .searchScore(Math.round(score))
                            .rankChange(calculateRankChange(value))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 검색어 순위 변동 계산
     * @param keyword 검색어
     * @return 순위 변동 값 (양수는 순위 상승, 음수는 하락을 의미)
     */
    private int calculateRankChange(String keyword) {
        Double previousScore = redisTemplate.opsForZSet().score("previousSearchRank", keyword);
        if (previousScore == null) return 0; // 이전 점수가 없는 경우 변동 없음

        int previousRank = redisTemplate.opsForZSet().reverseRank("previousSearchRank", keyword).intValue();
        int currentRank = redisTemplate.opsForZSet().reverseRank("currentSearchRank", keyword).intValue();
        return previousRank - currentRank; // 순위 변동 계산
    }

    /**
     * 검색어 점수 증가
     * @param keyword 검색어
     */
    public void recordSearchKeyword(String keyword) {
        redisTemplate.opsForZSet().incrementScore("tempCurrentSearchRank", keyword, 1); // tempCurrentSearchRank에 점수 1 증가
    }

    /**
     * 주문서 상품 정보 조회
     */
    public ProductShoppingResponse.OfRetrieveOrderInformation retrieveOrderInformation(Long productId) {
        Product product = productRepository.findByIdInWithCustomer(productId);
        return ProductShoppingResponse.OfRetrieveOrderInformation.convertedBy(product);
    }

    /**
     * 주문서 상품 정보 벌크 조회
     * @param productIdList
     * @return
     */
    public Set<ProductShoppingResponse.OfRetrieveOrderInformation> retrieveOrderInformation(List<Long> productIdList) {
        Set<Product> productList = productRepository.findAllByIdInWithCustomer(productIdList);

        return productList.stream()
                .map(ProductShoppingResponse.OfRetrieveOrderInformation::convertedBy)
                .collect(Collectors.toSet());
    }
}

