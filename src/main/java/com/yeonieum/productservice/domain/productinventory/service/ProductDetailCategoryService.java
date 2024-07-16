package com.yeonieum.productservice.domain.productinventory.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailCategoryService {
    @Cacheable(value = "shelflife:", key = "#product.getProductId()", cacheManager = "localCacheManager")
    public int getProductShelflife(Product product) {
        int lifeDay = product.getProductDetailCategory().getShelfLifeDay();
        return lifeDay;
    }
}
