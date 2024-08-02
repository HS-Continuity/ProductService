package com.yeonieum.productservice.domain.product.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ProductShelflifeCache {
    // 개발환경을 위한 serverCache로 전환 -> 추후 로컬캐시로 전환 예정[불변값]
    @Cacheable(value = "shelflife:", key = "#product.getProductId()", cacheManager = "serverCacheManager")
    public int getProductShelflife(Product product) {
        int lifeDay = product.getProductDetailCategory().getShelfLifeDay();
        return lifeDay;
    }
}
