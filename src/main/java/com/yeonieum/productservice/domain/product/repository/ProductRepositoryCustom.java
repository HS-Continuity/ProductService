package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<Product> findByKeywords(List<String> keywords, Pageable pageable);

    Page<Product> findProductsByCriteria(Long customerId, ActiveStatus isEcoFriend, String productName, String description, String origin, Integer price, ActiveStatus isPageVisibility, ActiveStatus isRegularSale, Integer baseDiscountRate, Integer regularDiscountRate, Pageable pageable);
}

