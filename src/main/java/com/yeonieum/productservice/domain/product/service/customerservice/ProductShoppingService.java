package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductShoppingService {

    private final ProductDetailCategoryRepository productDetailCategoryRepository;

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

}