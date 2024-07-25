package com.yeonieum.productservice.domain.product.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yeonieum.productservice.domain.category.entity.QProductDetailCategory;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.QProduct;

import com.yeonieum.productservice.global.enums.ActiveStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Product> findByKeywords(List<String> keywords, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QProduct product = QProduct.product;
        QProductDetailCategory category = QProductDetailCategory.productDetailCategory;

        // 쿼리 생성
        var query = queryFactory.selectFrom(product)
                .join(product.productDetailCategory, category);

        // WHERE 절 동적 생성
        BooleanExpression predicate = keywords.stream()
                .map(k -> product.productName.like("%" + k + "%")
                        .or(category.detailCategoryName.like("%" + k + "%"))
                        .and(product.isPageVisibility.eq(ActiveStatus.ACTIVE)))
                .reduce(BooleanExpression::or)
                .orElse(null);

        query.where(predicate).distinct();

        // 쿼리 결과 가져오기 및 페이징 처리
        List<Product> productList = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 가져오기
        long total = query.fetchCount();

        return new PageImpl<>(productList, pageable, total);
    }
}
