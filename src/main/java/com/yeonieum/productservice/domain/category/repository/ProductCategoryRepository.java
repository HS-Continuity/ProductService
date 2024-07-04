package com.yeonieum.productservice.domain.category.repository;

import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long> {
}
