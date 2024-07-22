package com.yeonieum.productservice.domain.product.service.memberservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductShoppingService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 카테고리의 상품 목록 조회
     * @param productCategoryId 조회할 상품 카테고리 ID
     * @param isCertification 인증된 상품만 조회할지 여부
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기)
     * @throws IllegalArgumentException 카테고리 ID가 존재하지 않는 경우
     * @throws IllegalArgumentException 카테고리에 상품이 하나도 없는 경우
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    @Transactional
    public Page<ProductShoppingResponse.OfRetrieveCategoryWithProduct> retrieveCategoryWithProducts(
            Long productCategoryId,
            ActiveStatus isCertification,
            Pageable pageable) {

        // 카테고리 정보를 조회, 존재하지 않을 경우 예외 발생
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 카테고리 ID 입니다."));

        // 해당 카테고리의 상품을 조회, 인증 여부와 페이징 정보를 반영
        Page<Product> products = productRepository.findActiveProductsByCategory(productCategoryId, isCertification, pageable);

        // 조회된 상품이 없으면 예외 발생
        if (products.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리 내의 상품이 없습니다.");
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
     * @throws IllegalArgumentException 상세 카테고리 ID가 존재하지 않는 경우
     * @throws IllegalArgumentException 상세 카테고리에 상품이 하나도 없는 경우
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    @Transactional
    public Page<ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct> retrieveDetailCategoryWithProducts(
            Long productDetailCategoryId,
            ActiveStatus isCertification,
            Pageable pageable) {

        // 상세 카테고리 정보를 조회, 존재하지 않을 경우 예외 발생
        ProductDetailCategory productDetailCategory = productDetailCategoryRepository.findById(productDetailCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 상세 카테고리 ID 입니다."));

        // 해당 상세 카테고리의 상품을 조회, 인증 여부와 페이징 정보를 반영
        Page<Product> products = productRepository.findActiveProductsByDetailCategoryId(
                productDetailCategoryId, isCertification, pageable);

        // 조회된 상품이 없으면 예외 발생
        if (products.isEmpty()) {
            throw new IllegalArgumentException("해당 상세 카테고리 내의 상품이 없습니다.");
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
     * @throws IllegalArgumentException 상품 ID가 존재하지 않는 경우
     * @return 상품의 상세 정보
     */
    @Transactional
    public ProductShoppingResponse.OfDetailProductInformation detailProductInformation(Long productId){

        // 상품 정보를 조회, 존재하지 않을 경우 예외 발생
        Product targetProduct = productRepository.findByIdAndIsActive(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID 입니다."));

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
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    @Transactional
    public Page<ProductShoppingResponse.OfSearchProductInformation> retrieveFilteringProducts(
            String keyword, ActiveStatus isCertification, Pageable pageable) {

        // 모든 검색 결과를 담을 Set
        Set<Product> allProducts = new HashSet<>();

        // 키워드가 유효한 경우 처리
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 매칭되는 카테고리를 저장할 Set
            Set<ProductDetailCategory> matchingCategories = new HashSet<>();

            // 키워드를 분할
            List<String> keywords = splitKeywords(keyword);

            for (String singleKeyword : keywords) {
                // 각 키워드에 대해 카테고리 검색
                List<ProductDetailCategory> categories = productDetailCategoryRepository.findByNameContaining(singleKeyword);
                matchingCategories.addAll(categories);

                // 각 키워드에 대해 상품명 검색
                List<Product> productsByName = productRepository.findByNameContaining(singleKeyword);
                allProducts.addAll(productsByName);
            }

            // 매칭되는 카테고리가 있는 경우, 카테고리 내의 상품을 검색하여 결과에 추가
            if (!matchingCategories.isEmpty()) {
                List<Product> productsByCategory = productRepository.findByProductDetailCategoryIn(new ArrayList<>(matchingCategories), pageable).getContent();
                allProducts.addAll(productsByCategory);
            }

            // 검색 결과가 존재하는 경우에만 검색어 점수 증가
            if (!allProducts.isEmpty()) {
                keywords.forEach(this::recordSearchKeyword);
            } else {
                // 검색 결과가 없는 경우 예외를 던짐
                throw new IllegalStateException(keyword + "에 대한 검색결과가 없습니다. 다른 검색어를 입력해 주세요.");
            }

            // 중복 제거 후 페이징 처리
            List<Product> distinctProducts = allProducts.stream().distinct().collect(Collectors.toList());
            return new PageImpl<>(distinctProducts, pageable, distinctProducts.size())
                    .map(ProductShoppingResponse.OfSearchProductInformation::convertedBy);
        }

        // 키워드가 없고 인증 상태만 있는 경우, 인증 상태에 따라 검색
        if (isCertification != null) {
            return productRepository.findActiveCertifiableProductsByProductId(isCertification, pageable)
                    .map(ProductShoppingResponse.OfSearchProductInformation::convertedBy);
        }

        // 키워드와 인증 상태 모두 없는 경우 전체 상품 조회
        return productRepository.findAllByIsActive(pageable)
                .map(ProductShoppingResponse.OfSearchProductInformation::convertedBy);
    }

    /**
     * 입력된 문자열을 공백과 대문자 경계를 기준으로 분할
     * @param keyword 입력 문자열
     * @return 분할된 키워드 리스트
     */
    private List<String> splitKeywords(String keyword) {
        List<String> result = new ArrayList<>();

        // 입력 문자열을 공백("\\s+")을 기준으로 분할
        String[] parts = keyword.split("\\s+");

        for (String part : parts) {
            // 각 파트를 대문자 경계에서 추가로 분할 (예: "StockMarket" -> "Stock", "Market")
            result.addAll(Arrays.asList(part.split("(?<=.)(?=\\p{Lu})")));
        }
        return result;
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
}

