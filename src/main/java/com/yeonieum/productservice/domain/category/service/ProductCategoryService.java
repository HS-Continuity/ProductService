package com.yeonieum.productservice.domain.category.service;

import com.yeonieum.productservice.domain.category.dto.category.ProductCategoryRequest;
import com.yeonieum.productservice.domain.category.dto.category.ProductCategoryResponse;
import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    /**
     * 상품 카테고리 등록
     * @param registerCategoryDto
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean registerCategory(ProductCategoryRequest.RegisterCategoryDto registerCategoryDto) {
        ProductCategory productCategory = ProductCategory.builder()
                .categoryName(registerCategoryDto.getCategoryName())
                .build();

        productCategoryRepository.save(productCategory);

        return true;
    }

    /**
     * 상품 카테고리 전체 조회
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public List<ProductCategoryResponse.RetrieveAllCategoryDto> retrieveAllCategoryDtoList() {
        List<ProductCategory> categoryList = productCategoryRepository.findAll();

        return categoryList.stream()
                .map(category ->
                        new ProductCategoryResponse.RetrieveAllCategoryDto(
                            category.getProductCategoryId(),
                            category.getCategoryName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 상품 카테고리 수정
     * @param modifyCategoryDto
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean modifyCategory(ProductCategoryRequest.ModifyCategoryDto modifyCategoryDto) {
        Long productCategoryId = modifyCategoryDto.getProductCategoryId();

        ProductCategory existingCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID 입니다."));
        existingCategory.changeCategoryName(modifyCategoryDto.getCategoryName());

        productCategoryRepository.save(existingCategory);

        return true;
    }

    /**
     * 상품 카테고리 삭제
     * @param productCategoryId
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean deleteCategory(Long productCategoryId) {
        if (productCategoryRepository.existsById(productCategoryId)) {
            productCategoryRepository.deleteById(productCategoryId);
            return true;
        } else {
            throw new IllegalArgumentException("존재하지 않는 카테고리 ID 입니다.");
        }
    }

    /**
     * 상품 카테고리 조회시, 하위 상세카테고리 조회
     * @param productCategoryId
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public ProductCategoryResponse.RetrieveCategoryWithDetailsDto retrieveCategoryWithDetails(Long productCategoryId) {
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID 입니다."));

        List<ProductCategoryResponse.ProductDetailCategoryDto> detailCategoryDtoList = productCategory.getProductDetailCategoryList().stream()
                .map(detail -> ProductCategoryResponse.ProductDetailCategoryDto.builder()
                        .productDetailCategoryId(detail.getProductDetailCategoryId())
                        .categoryDetailName(detail.getCategoryDetailName())
                        .shelfLifeDay(detail.getShelfLifeDay())
                        .build())
                .collect(Collectors.toList());

        return ProductCategoryResponse.RetrieveCategoryWithDetailsDto.builder()
                .productCategoryId(productCategory.getProductCategoryId())
                .categoryName(productCategory.getCategoryName())
                .productDetailCategoryList(detailCategoryDtoList)
                .build();
    }
}
