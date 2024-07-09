package com.yeonieum.productservice.domain.product.service.memberservice;

import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductShoppingService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductRepository productRepository;

    /**
     * 선택한 상세 카테고리 조회시, 해당 카테고리의 상품 조회
     * @param productDetailCategoryId
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public ProductShoppingResponse.RetrieveDetailCategoryWithProductsDto retrieveDetailCategoryWithProducts(Long productDetailCategoryId) {
        ProductDetailCategory productDetailCategory = productDetailCategoryRepository.findByIdWithProducts(productDetailCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상세 카테고리 ID 입니다."));

        List<ProductShoppingResponse.SearchProductInformationDto> productInformationDtoList = productDetailCategory.getProductList().stream()
                .map(product -> ProductShoppingResponse.SearchProductInformationDto.builder()
                        .productId(product.getProductId())
                        .productName(product.getProductName())
                        .productDescription(product.getProductDescription())
                        .productImage(product.getProductImage())
                        .productPrice(product.getProductPrice())
                        .isRegularSale(product.getIsRegularSale().getCode())
                        .build())
                .collect(Collectors.toList());

        return ProductShoppingResponse.RetrieveDetailCategoryWithProductsDto.builder()
                .productDetailCategoryId(productDetailCategory.getProductDetailCategoryId())
                .detailCategoryName(productDetailCategory.getDetailCategoryName())
                .shelfLifeDay(productDetailCategory.getShelfLifeDay())
                .searchProductInformationDtoList(productInformationDtoList)
                .build();
    }

    /**
     * 선택한 카테고리 조회시, 해당 카테고리의 상품 조회
     * @param productCategoryId
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public ProductShoppingResponse.RetrieveCategoryWithProductsDto retrieveCategoryWithProducts(Long productCategoryId) {
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 카테고리 ID 입니다."));

        List<Product> products = productRepository.findProductsByCategory(productCategoryId);
        List<ProductShoppingResponse.SearchProductInformationDto> productDtos = products.stream().map(product ->
                ProductShoppingResponse.SearchProductInformationDto.builder()
                        .productId(product.getProductId())
                        .productName(product.getProductName())
                        .productDescription(product.getProductDescription())
                        .productImage(product.getProductImage())
                        .productPrice(product.getProductPrice())
                        .isRegularSale(product.getIsRegularSale().getCode())
                        .build()
        ).collect(Collectors.toList());

        return ProductShoppingResponse.RetrieveCategoryWithProductsDto.builder()
                .productCategoryId(productCategory.getProductCategoryId())
                .categoryName(productCategory.getCategoryName())
                .searchProductInformationDtoList(productDtos)
                .build();
    }
}