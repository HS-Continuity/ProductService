package com.yeonieum.productservice.domain.category.repository;

import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long> {

    @Query("SELECT c FROM ProductCategory c LEFT JOIN FETCH c.productDetailCategoryList WHERE c.productCategoryId = :productCategoryId")
    Optional<ProductCategory> findByIdWithDetailCategories(@Param("productCategoryId") Long productCategoryId);
}
