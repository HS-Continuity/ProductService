package com.yeonieum.productservice.infrastructure.persistance.repository.category;

import com.yeonieum.productservice.infrastructure.persistance.entity.category.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long> {
}
