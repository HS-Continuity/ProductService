package com.yeonieum.productservice.domain.category.service;

import com.yeonieum.productservice.domain.category.dto.category.ModifyCategoryDto;
import com.yeonieum.productservice.domain.category.dto.category.RegisterCategoryDto;
import com.yeonieum.productservice.domain.category.dto.category.RetrieveAllCategoryDto;
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
    public ProductCategory registerCategory(RegisterCategoryDto registerCategoryDto) {
        ProductCategory productCategory = ProductCategory.builder()
                .categoryName(registerCategoryDto.getCategoryName())
                .build();

        return productCategoryRepository.save(productCategory);
    }

    /**
     * 상품 카테고리 전체 조회
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public List<RetrieveAllCategoryDto> retrieveAllCategoryDtoList() {
        List<ProductCategory> categoryList = productCategoryRepository.findAll();

        return categoryList.stream()
                .map(category ->
                        new RetrieveAllCategoryDto(
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
    public ProductCategory modifyCategory(ModifyCategoryDto modifyCategoryDto) {
        Long productCategoryId = modifyCategoryDto.getProductCategoryId();

        ProductCategory existingCategory = productCategoryRepository.findById(productCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID 입니다."));
        existingCategory.setCategoryName(modifyCategoryDto.getCategoryName());

        return productCategoryRepository.save(existingCategory);
    }


    /**
     * 상품 카테고리 삭제
     * @param productCategoryId
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public void deleteCategory(Long productCategoryId) {
        if (productCategoryRepository.existsById(productCategoryId)) {
            productCategoryRepository.deleteById(productCategoryId);
        } else {
            throw new IllegalArgumentException("존재하지 않는 카테고리 ID 입니다.");
        }
    }
}
