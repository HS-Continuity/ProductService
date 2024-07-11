package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.review.repository.ProductReviewRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductShoppingService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;

    /**
     * 카테고리 조회시, 해당 (상세)카테고리의 상품 조회
     * @param productCategoryId 상품 카테고리 ID
     * @param isCertification 인증서 여부
     * @param pageable 페이징 정보
     * @throws IllegalArgumentException 존재하지 않는 상품 카테고리 ID인 경우
     * @throws IllegalArgumentException 해당 카테고리에 등록된 상품이 없는 경우
     * @return 카테고리에 포함되는 상품들의 정보
     */
    @Transactional
    public ProductShoppingResponse.RetrieveCategoryWithProductsDto retrieveCategoryWithProducts(Long productCategoryId, ActiveStatus isCertification, Pageable pageable) {
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 카테고리 ID 입니다."));

        Page<Product> productsPage = productRepository.findActiveProductsByCategory(productCategoryId, isCertification, pageable);

        if (productsPage.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리 내의 상품이 없습니다.");
        }

        List<ProductShoppingResponse.SearchProductInformationDto> searchProductInformationDtoList = productsPage.getContent().stream().map(product -> {
            int reviewCount = productReviewRepository.countByProductId(product.getProductId());
            double averageScore = productReviewRepository.findAverageScoreByProductId(product.getProductId());

            return ProductShoppingResponse.SearchProductInformationDto.builder()
                    .productId(product.getProductId())
                    .customerId(product.getCustomer().getCustomerId())
                    .detailCategoryId(product.getProductDetailCategory().getProductDetailCategoryId())
                    .productName(product.getProductName())
                    .productDescription(product.getProductDescription())
                    .productImage(product.getProductImage())
                    .baseDiscountRate(product.getBaseDiscountRate())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .productPrice(product.getProductPrice())
                    .calculatedBasePrice(product.getCalculatedBasePrice())
                    .isRegularSale(product.getIsRegularSale().getCode())
                    .reviewCount(reviewCount)
                    .averageScore(averageScore)
                    .build();
        }).collect(Collectors.toList());

        return ProductShoppingResponse.RetrieveCategoryWithProductsDto.builder()
                .productCategoryId(productCategoryId)
                .categoryName(productCategory.getCategoryName())
                .searchProductInformationDtoList(searchProductInformationDtoList)
                .totalItems((int) productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .lastPage(productsPage.isLast())
                .build();
    }

    /**
     * 상세 카테고리 조회시, 해당 카테고리의 상품 조회
     * @param productDetailCategoryId 상세 카테고리 ID
     * @param isCertification 인증서 여부
     * @param pageable 페이징 정보
     * @throws IllegalArgumentException 존재하지 않는 상세 카테고리 ID인 경우
     * @throws IllegalArgumentException 해당 상세 카테고리에 등록된 상품이 없는 경우
     * @return 상세 카테고리에 포함되는 상품들의 정보
     */
    @Transactional
    public ProductShoppingResponse.RetrieveDetailCategoryWithProductsDto retrieveDetailCategoryWithProducts(Long productDetailCategoryId, ActiveStatus isCertification, Pageable pageable) {

       ProductDetailCategory productDetailCategory = productDetailCategoryRepository.findById(productDetailCategoryId)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 상세 카테고리 ID 입니다."));

        Page<Product> productsPage = productRepository.findActiveProductsByDetailCategoryId(productDetailCategoryId, isCertification, pageable);

        if (productsPage.isEmpty()) {
            throw new IllegalArgumentException("해당 상세 카테고리에 내의 상품이 없습니다.");
        }

        List<ProductShoppingResponse.SearchProductInformationDto> productInformationDtoList = productsPage.getContent().stream()
                .map(product -> {
                    int reviewCount = productReviewRepository.countByProductId(product.getProductId());
                    double averageScore = productReviewRepository.findAverageScoreByProductId(product.getProductId());

                    return ProductShoppingResponse.SearchProductInformationDto.builder()
                            .productId(product.getProductId())
                            .customerId(product.getCustomer().getCustomerId())
                            .detailCategoryId(product.getProductDetailCategory().getProductDetailCategoryId())
                            .productName(product.getProductName())
                            .productDescription(product.getProductDescription())
                            .productImage(product.getProductImage())
                            .baseDiscountRate(product.getBaseDiscountRate())
                            .regularDiscountRate(product.getRegularDiscountRate())
                            .productPrice(product.getProductPrice())
                            .calculatedBasePrice(product.getCalculatedBasePrice())
                            .isRegularSale(product.getIsRegularSale().getCode())
                            .reviewCount(reviewCount)
                            .averageScore(averageScore)
                            .build();
                }).collect(Collectors.toList());

        return ProductShoppingResponse.RetrieveDetailCategoryWithProductsDto.builder()
                .productDetailCategoryId(productDetailCategoryId)
                .detailCategoryName(productDetailCategory.getDetailCategoryName())
                .shelfLifeDay(productDetailCategory.getShelfLifeDay())
                .searchProductInformationDtoList(productInformationDtoList)
                .totalItems((int) productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .lastPage(productsPage.isLast())
                .build();
    }


    /**
     * 상세 상품 조회
     * @param productId 상품 ID
     * @throws IllegalArgumentException 존재하지 않는 상품 ID인 경우
     * @return 상품의 정보
     */
    @Transactional
    public ProductShoppingResponse.DetailProductInformationDto detailProductInformationDto(Long productId){
        Product targetProduct = productRepository.findByIdAndIsActive(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID 입니다."));

        int reviewCount = productReviewRepository.countByProductId(productId);
        double averageScore = productReviewRepository.findAverageScoreByProductId(productId);

        return ProductShoppingResponse.DetailProductInformationDto.builder()
                .productId(targetProduct.getProductId())
                .customerId(targetProduct.getCustomer().getCustomerId())
                .detailCategoryId(targetProduct.getProductDetailCategory().getProductDetailCategoryId())
                .productName(targetProduct.getProductName())
                .productDescription(targetProduct.getProductDescription())
                .productImage(targetProduct.getProductImage())
                .origin(targetProduct.getProductOrigin())
                .baseDiscountRate(targetProduct.getBaseDiscountRate())
                .regularDiscountRate(targetProduct.getRegularDiscountRate())
                .personalizedDiscountRate(targetProduct.getPersonalizedDiscountRate())
                .productPrice(targetProduct.getProductPrice())
                .calculatedBasePrice(targetProduct.getCalculatedBasePrice())
                .calculatedRegularPrice(targetProduct.getCalculatedRegularPrice())
                .calculatedPersonalizedPrice(targetProduct.getCalculatedPersonalizedPrice())
                .isRegularSale((char) (targetProduct.getIsRegularSale() == ActiveStatus.ACTIVE ? 'T' : 'F'))
                .isCertification((char) (targetProduct.getIsCertification() == ActiveStatus.ACTIVE ? 'T' : 'F'))
                .reviewCount(reviewCount)
                .averageScore(averageScore)
                .build();
    }

    /**
     * 키워드로 상품 조회
     * @param keyword 상품 키워드(이름)
     * @param pageable 페이징 정보
     * @return 해당 키워드의 상품들 정보
     */
    @Transactional
    public ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveKeywordWithProductsDto(String keyword, Pageable pageable){

        Page<Product> productsPage = productRepository.findByProductNameContainingAndIsActive(keyword, pageable);

        List<ProductShoppingResponse.SearchProductInformationDto> searchProductInformationDtoList = productsPage.getContent().stream().map(product -> {
            int reviewCount = productReviewRepository.countByProductId(product.getProductId());
            double averageScore = productReviewRepository.findAverageScoreByProductId(product.getProductId());

            return ProductShoppingResponse.SearchProductInformationDto.builder()
                    .productId(product.getProductId())
                    .customerId(product.getCustomer().getCustomerId())
                    .detailCategoryId(product.getProductDetailCategory().getProductDetailCategoryId())
                    .productName(product.getProductName())
                    .productDescription(product.getProductDescription())
                    .productImage(product.getProductImage())
                    .baseDiscountRate(product.getBaseDiscountRate())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .productPrice(product.getProductPrice())
                    .calculatedBasePrice(product.getCalculatedBasePrice())
                    .isRegularSale(product.getIsRegularSale().getCode())
                    .reviewCount(reviewCount)
                    .averageScore(averageScore)
                    .build();
        }).collect(Collectors.toList());

        return ProductShoppingResponse.RetrieveSearchWithProductsDto.builder()
                .totalItems((int) productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .lastPage(productsPage.isLast())
                .searchProductInformationDtoList(searchProductInformationDtoList)
                .build();
    }

    /**
     * 업체별 상품 조회
     * @param customerId 고객 ID
     * @param detailCategoryId 상세 카테고리 ID
     * @param pageable 페이징 정보
     * @return 업체 상품들의 정보
     */
    @Transactional
    public ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveCustomerWithProductsDto(Long customerId, Long detailCategoryId, Pageable pageable){

        Page<Product> productsPage = productRepository.findByCustomerIdAndIsActiveAndCategoryId(customerId, detailCategoryId ,pageable);

        List<ProductShoppingResponse.SearchProductInformationDto> searchProductInformationDtoList = productsPage.getContent().stream().map(product -> {
            int reviewCount = productReviewRepository.countByProductId(product.getProductId());
            double averageScore = productReviewRepository.findAverageScoreByProductId(product.getProductId());

            return ProductShoppingResponse.SearchProductInformationDto.builder()
                    .productId(product.getProductId())
                    .customerId(product.getCustomer().getCustomerId())
                    .detailCategoryId(product.getProductDetailCategory().getProductDetailCategoryId())
                    .productName(product.getProductName())
                    .productDescription(product.getProductDescription())
                    .productImage(product.getProductImage())
                    .baseDiscountRate(product.getBaseDiscountRate())
                    .regularDiscountRate(product.getRegularDiscountRate())
                    .productPrice(product.getProductPrice())
                    .calculatedBasePrice(product.getCalculatedBasePrice())
                    .isRegularSale(product.getIsRegularSale().getCode())
                    .reviewCount(reviewCount)
                    .averageScore(averageScore)
                    .build();
        }).collect(Collectors.toList());

        return ProductShoppingResponse.RetrieveSearchWithProductsDto.builder()
                .totalItems((int) productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .lastPage(productsPage.isLast())
                .searchProductInformationDtoList(searchProductInformationDtoList)
                .build();
    }
}

