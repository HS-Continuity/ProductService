package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import com.yeonieum.productservice.domain.customer.dto.CustomerResponse;
import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductShoppingService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductRepository productRepository;


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

        // 상품 정보를 DTO 리스트로 변환
        List<ProductShoppingResponse.OfSearchProductInformation> productInformationListDto = products.map(
                product -> ProductShoppingResponse.OfSearchProductInformation.convertedBy(product)).getContent();

        // 조회된 카테고리 정보를 기반으로 DTO 생성
        ProductShoppingResponse.OfRetrieveCategoryWithProduct categoryWithProductsDto
                = ProductShoppingResponse.OfRetrieveCategoryWithProduct.convertedBy(productCategory);

        // categoryWithProductsDto에 상품 정보 리스트를 설정
        categoryWithProductsDto.changeOfSearchProductInformationList(productInformationListDto);

        // Page 객체를 생성하여 반환
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

        // 상품 정보를 DTO 리스트로 변환
        List<ProductShoppingResponse.OfSearchProductInformation> productInformationListDto = products.map(
                product -> ProductShoppingResponse.OfSearchProductInformation.convertedBy(product)).getContent();

        // 조회된 상세 카테고리 정보를 기반으로 DTO 생성
        ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct detailCategoryWithProductsDto
                = ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct.convertedBy(productDetailCategory);

        // detailCategoryWithProductsDto에 상품 정보 리스트를 설정
        detailCategoryWithProductsDto.changeOfSearchProductInformationList(productInformationListDto);

        // Page 객체를 생성하여 반환
        return new PageImpl<>(Collections.singletonList(detailCategoryWithProductsDto), pageable, products.getTotalElements());
    }

    /**
     * 상품 상세 정보 조회
     * @param productId 상품 ID
     * @throws IllegalArgumentException 상품 ID가 존재하지 않는 경우
     * @return 상품의 상세 정보
     */
    @Transactional
    public ProductShoppingResponse.OfDetailProductInformation detailProductInformationDto(Long productId){
        Product targetProduct = productRepository.findByIdAndIsActive(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 ID 입니다."));

        return ProductShoppingResponse.OfDetailProductInformation.convertedBy(targetProduct);
    }

    /**
     * 키워드로 상품 조회
     * @return 조회된 상품 목록이 포함된 Page 객체
     */
    @Transactional
    public Page<ProductShoppingResponse.OfSearchProductInformation> retrieveKeywordWithProductsDto(
            String keyword, Pageable pageable){

        Page<Product> products;

        if (keyword == null || keyword.trim().isEmpty()) {
            products = productRepository.findAllByIsActive(pageable);
        } else {
            products = productRepository.findByProductNameContainingAndIsActive(keyword, pageable);
        }

        return products.map(product -> ProductShoppingResponse.OfSearchProductInformation.convertedBy(product));
    }

//    /**
//     * 업체별 상품 조회
//     * @param customerId 고객 ID
//     * @param detailCategoryId 상세 카테고리 ID
//     * @param pageable 페이징 정보
//     * @return 업체 상품들의 정보
//     */
//    @Transactional
//    public ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveCustomerWithProductsDto(Long customerId, Long detailCategoryId, Pageable pageable){
//
//        Page<Product> productsPage = productRepository.findByCustomerIdAndIsActiveAndCategoryId(customerId, detailCategoryId ,pageable);
//
//        List<ProductShoppingResponse.OfSearchProductInformation> searchProductInformationDtoList = productsPage.getContent().stream().map(product -> {
//
//            return ProductShoppingResponse.OfSearchProductInformation.builder()
//                    .productId(product.getProductId())
//                    .customerId(product.getCustomer().getCustomerId())
//                    .detailCategoryId(product.getProductDetailCategory().getProductDetailCategoryId())
//                    .productName(product.getProductName())
//                    .productDescription(product.getProductDescription())
//                    .productImage(product.getProductImage())
//                    .baseDiscountRate(product.getBaseDiscountRate())
//                    .regularDiscountRate(product.getRegularDiscountRate())
//                    .productPrice(product.getProductPrice())
//                    .calculatedBasePrice(product.getCalculatedBasePrice())
//                    .isRegularSale(product.getIsRegularSale().getCode())
//                    .reviewCount(product.getReviewCount())
//                    .averageScore(product.getAverageScore())
//                    .build();
//        }).collect(Collectors.toList());
//
//        return ProductShoppingResponse.RetrieveSearchWithProductsDto.builder()
//                .totalItems((int) productsPage.getTotalElements())
//                .totalPages(productsPage.getTotalPages())
//                .lastPage(productsPage.isLast())
//                .searchProductInformationDtoList(searchProductInformationDtoList)
//                .build();
//    }
}

