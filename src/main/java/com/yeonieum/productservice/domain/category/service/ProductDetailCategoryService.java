package com.yeonieum.productservice.domain.category.service;

import com.yeonieum.productservice.domain.category.dto.detailcategory.ProductDetailCategoryRequest;
import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDetailCategoryService {

    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    /**
     * 상품 상세 카테고리 등록
     * @param registerDetailCategoryDto
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean registerProductDetailCategory(ProductDetailCategoryRequest.RegisterDetailCategoryDto registerDetailCategoryDto) {
        Long productCategoryId = registerDetailCategoryDto.getProductCategoryId();
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID 입니다."));

        ProductDetailCategory productDetailCategory = ProductDetailCategory.builder()
                .productCategory(productCategory)
                .categoryDetailName(registerDetailCategoryDto.getCategoryDetailName())
                .shelfLifeDay(registerDetailCategoryDto.getShelfLifeDay())
                .build();

        productDetailCategoryRepository.save(productDetailCategory);

        return true;
    }

    /**
     * 상품 상세 카테고리 수정
     * @param modifyDetailCategoryDto
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean modifyDetailCategory(ProductDetailCategoryRequest.ModifyDetailCategoryDto modifyDetailCategoryDto) {
        Long productDetailCategoryId = modifyDetailCategoryDto.getProductDetailCategoryId();

        ProductDetailCategory existingDetailCategory = productDetailCategoryRepository.findById(productDetailCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상세 카테고리 ID 입니다."));

        existingDetailCategory.changeDetailCategoryName(modifyDetailCategoryDto.getCategoryDetailName());
        existingDetailCategory.changeDetailCategoryShelfLifeDay(modifyDetailCategoryDto.getShelfLifeDay());

        productDetailCategoryRepository.save(existingDetailCategory);

        return true;
    }

    /**
     * 상품 상세 카테고리 삭제
     * @param productDetailCategoryId
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean deleteDetailCategory(Long productDetailCategoryId){
        if (productDetailCategoryRepository.existsById(productDetailCategoryId)) {
            productDetailCategoryRepository.deleteById(productDetailCategoryId);
            return true;
        } else {
            throw new IllegalArgumentException("존재하지 않는 상세 카테고리 ID 입니다.");
        }
    }
}
