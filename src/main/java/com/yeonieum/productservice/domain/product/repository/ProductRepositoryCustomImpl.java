package com.yeonieum.productservice.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
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

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findByKeywords(List<String> keywords, Pageable pageable) {

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

    @Override
    public Page<Product> findProductsByCriteria(Long customerId, ActiveStatus isEcoFriend, Long productId, String productName, String detailCategoryName, String origin, Integer price, ActiveStatus isPageVisibility, ActiveStatus isRegularSale, Integer baseDiscountRate, Integer regularDiscountRate, Pageable pageable) {
        QProduct product = QProduct.product;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.customer.customerId.eq(customerId));

        if (isEcoFriend != null) {
            builder.and(product.isCertification.eq(isEcoFriend));
        }
        if (productId != null) {
            builder.and(product.productId.eq(productId));
        }
        if (productName != null) {
            builder.and(product.productName.like("%" + productName + "%"));
        }
        if (detailCategoryName != null) {
            builder.and(product.productDetailCategory.detailCategoryName.like("%" + detailCategoryName + "%"));
        }
        if (origin != null) {
            builder.and(product.productOrigin.like("%" + origin + "%"));
        }
        if (price != null) {
            builder.and(product.productPrice.eq(price));
        }
        if (isPageVisibility != null) {
            builder.and(product.isPageVisibility.eq(isPageVisibility));
        }
        if (isRegularSale != null) {
            builder.and(product.isRegularSale.eq(isRegularSale));
        }
        if (baseDiscountRate != null) {
            builder.and(product.baseDiscountRate.eq(baseDiscountRate));
        }
        if (regularDiscountRate != null) {
            builder.and(product.regularDiscountRate.eq(regularDiscountRate));
        }

        List<Product> results = queryFactory
                .selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(product)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
