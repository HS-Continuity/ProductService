package com.yeonieum.productservice.domain.category.repository;

import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.yeonieum.productservice.global.enums.ActiveStatus;

import java.util.Optional;

public interface ProductDetailCategoryRepository extends JpaRepository<ProductDetailCategory,Long> {

    @Query("SELECT d FROM ProductDetailCategory d " +
            "JOIN FETCH d.productList p " +
            "WHERE d.productDetailCategoryId = :productDetailCategoryId " +
            "AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE " +
            "AND (:isCertification IS NULL OR p.isCertification = :isCertification)")
    Page<ProductDetailCategory> findByIdWithProducts(@Param("productDetailCategoryId") Long productDetailCategoryId,
                                                         @Param("isCertification") ActiveStatus isCertification,
                                                         Pageable pageable);

    boolean existsByDetailCategoryName(String detailCategoryName);
}

