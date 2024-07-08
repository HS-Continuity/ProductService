package com.yeonieum.productservice.domain.cart.repository;

import com.yeonieum.productservice.domain.cart.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    @Query("SELECT cp FROM CartProduct cp JOIN FETCH cp.product WHERE cp.memberId = :memberId AND cp.cartType.cartTypeId = :cartTypeId")
    List<CartProduct> findByMemberIdAndCartTypeIdWithProduct(@Param("memberId") String memberId, @Param("cartTypeId") Long cartTypeId);

}
