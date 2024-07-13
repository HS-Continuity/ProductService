package com.yeonieum.productservice.domain.productinventory.repository;

import com.yeonieum.productservice.domain.product.entity.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Cache {
    @Cacheable(value = "shelflife:", key = "#product.getProductId()")
    public int cache(Product product) {
        int lifeDay = product.getProductDetailCategory().getShelfLifeDay();
        return lifeDay;
    }
}
