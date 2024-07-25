package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<Product> findByKeywords(List<String> keywords, Pageable pageable);
}
