package com.yeonieum.productservice.domain.category.repository;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductDetailCategoryRepository extends JpaRepository<ProductDetailCategory,Long> {

    @Query("SELECT d FROM ProductDetailCategory d LEFT JOIN FETCH d.productList WHERE d.productDetailCategoryId = :productDetailCategoryId")
    Optional<ProductDetailCategory> findByIdWithProducts(@Param("productDetailCategoryId") Long productDetailCategoryId);

    boolean existsByDetailCategoryName(String detailCategoryName);
}

