package com.yeonieum.productservice.domain.cart.repository;

import com.yeonieum.productservice.domain.cart.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    @Query("SELECT cp FROM CartProduct cp " +
            "JOIN FETCH cp.product p " +
            "WHERE cp.memberId = :memberId " +
            "AND cp.cartType.cartTypeId = :cartTypeId " +
            "AND p.isPageVisibility = com.yeonieum.productservice.global.enums.ActiveStatus.ACTIVE")
    List<CartProduct> findByMemberIdAndCartTypeIdWithProduct(@Param("memberId") String memberId, @Param("cartTypeId") Long cartTypeId);

    @Query("SELECT COUNT(cp) FROM CartProduct cp WHERE cp.memberId = :memberId AND (:cartTypeId IS NULL OR cp.cartType.cartTypeId = :cartTypeId)")
    Long countByMemberIdAndOptionalCartTypeId(@Param("memberId") String memberId, @Param("cartTypeId") Long cartTypeId);
    //네이티브쿼리 사용하기
    @Query(value = "DELETE FROM cart_product WHERE member_id = :memberId AND cart_type_id = :cartTypeId", nativeQuery = true)
    void deleteByMemberIdAndCartType(@Param("memberId") String memberId, @Param("cartTypeId") Long cartTypeId);

}
