package com.yeonieum.productservice.domain.category.service;

import com.yeonieum.productservice.domain.category.dto.detailcategory.ProductDetailCategoryRequest;
import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.exception.CategoryException;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.category.repository.ProductDetailCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.yeonieum.productservice.domain.category.exception.CategoryExceptionCode.*;

@Service
@RequiredArgsConstructor
public class ProductDetailCategoryService {

    private final ProductDetailCategoryRepository productDetailCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    /**
     * 상품 상세 카테고리 등록
     * @param registerDetailCategoryDto 상품 카테고리에 등록할 카테고리 정보 DTO
     * @throws CategoryException 존재하지 않는 상품 카테고리 ID인 경우
     * @throws CategoryException 이미 존재하는 상품 카테고리 이름일 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean registerProductDetailCategory(ProductDetailCategoryRequest.RegisterDetailCategoryDto registerDetailCategoryDto) {

        ProductCategory productCategory = productCategoryRepository.findById(registerDetailCategoryDto.getProductCategoryId())
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (productDetailCategoryRepository.existsByDetailCategoryName(registerDetailCategoryDto.getDetailCategoryName())) {
            throw new CategoryException(DETAIL_CATEGORY_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        ProductDetailCategory productDetailCategory = ProductDetailCategory.builder()
                .productCategory(productCategory)
                .detailCategoryName(registerDetailCategoryDto.getDetailCategoryName())
                .shelfLifeDay(registerDetailCategoryDto.getShelfLifeDay())
                .build();

        productDetailCategoryRepository.save(productDetailCategory);

        return true;
    }

    /**
     * 상품 카테고리 수정
     * @param detailCategoryId 상품 상세 카테고리 ID
     * @param modifyDetailCategoryDto 상품 상세 카테고리에 수정할 정보 DTO
     * @throws CategoryException 존재하지 않는 상세 카테고리 ID인 경우
     * @throws CategoryException 이미 존재하는 상세 카테고리 이름일 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean modifyDetailCategory(Long detailCategoryId, ProductDetailCategoryRequest.ModifyDetailCategoryDto modifyDetailCategoryDto) {

        ProductDetailCategory existingDetailCategory = productDetailCategoryRepository.findById(detailCategoryId)
                .orElseThrow(() -> new CategoryException(DETAIL_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (productDetailCategoryRepository.existsByDetailCategoryName(modifyDetailCategoryDto.getDetailCategoryName())) {
            throw new CategoryException(DETAIL_CATEGORY_NAME_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        existingDetailCategory.changeDetailCategoryName(modifyDetailCategoryDto.getDetailCategoryName());
        existingDetailCategory.changeDetailCategoryShelfLifeDay(modifyDetailCategoryDto.getShelfLifeDay());

        productDetailCategoryRepository.save(existingDetailCategory);

        return true;
    }

    /**
     * 상품 상세 카테고리 삭제
     * @param productDetailCategoryId 상품 상세 카테고리 ID
     * @throws CategoryException 존재하지 않는 상세 카테고리 ID인 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean deleteDetailCategory(Long productDetailCategoryId){
        if (productDetailCategoryRepository.existsById(productDetailCategoryId)) {
            productDetailCategoryRepository.deleteById(productDetailCategoryId);
            return true;
        } else {
            throw new CategoryException(DETAIL_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }
}
