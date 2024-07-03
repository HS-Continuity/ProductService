package com.yeonieum.productservice.infrastructure.persistance.repository.category;

import com.yeonieum.productservice.infrastructure.persistance.entity.category.ProductDetailCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailCategoryRepository extends JpaRepository<ProductDetailCategory,Integer> {
}
