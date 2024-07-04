package com.yeonieum.productservice.domain.category.repository;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailCategoryRepository extends JpaRepository<ProductDetailCategory,Integer> {
}
