package com.yeonieum.productservice.domain.category.repository;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDetailCategoryRepository extends JpaRepository<ProductDetailCategory,Long> {

    boolean existsByDetailCategoryName(String detailCategoryName);

    @Query("SELECT c FROM ProductDetailCategory c WHERE c.detailCategoryName LIKE CONCAT('%', :name, '%')")
    List<ProductDetailCategory> findByNameContaining(@Param("name") String name);


}

