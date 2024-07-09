package com.yeonieum.productservice.domain.cart.service;

import com.yeonieum.productservice.domain.cart.dto.CartProductRequest;
import com.yeonieum.productservice.domain.cart.dto.CartProductResponse;
import com.yeonieum.productservice.domain.cart.entity.CartProduct;
import com.yeonieum.productservice.domain.cart.entity.CartType;
import com.yeonieum.productservice.domain.cart.repository.CartProductRepository;
import com.yeonieum.productservice.domain.cart.repository.CartTypeRepository;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartProductService {

    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final CartTypeRepository cartTypeRepository;

    /**
     * 장바구니에 상품 등록
     * @param registerProductCartDto 장바구니에 등록할 상품 정보 DTO
     * @throws IllegalStateException 존재하지 않는 상품 ID인 경우 발생
     * @throws IllegalStateException 존재하지 않는 장바구니 타입 ID인 경우 발생
     * @return 성공여부
     */
    @Transactional
    public boolean registerCartProduct(CartProductRequest.RegisterProductCartDto registerProductCartDto) {

        Product product = productRepository.findById(registerProductCartDto.getProductId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 상품 ID 입니다."));

        CartType cartType = cartTypeRepository.findById(registerProductCartDto.getCartTypeId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 장바구니타입 ID 입니다."));

        List<CartProduct> cartProductList = cartProductRepository.findByMemberIdAndCartTypeIdWithProduct(registerProductCartDto.getMemberId(), registerProductCartDto.getCartTypeId());

        for (CartProduct existingCartProduct : cartProductList) {
            if (existingCartProduct.getProduct().getProductId().equals(registerProductCartDto.getProductId())) {
                // 같은 상품이 있으면 수량 추가
                existingCartProduct.changeProductQuantity(existingCartProduct.getQuantity() + registerProductCartDto.getQuantity());
                cartProductRepository.save(existingCartProduct);
                return true;
            }
        }

        // 장바구니에 같은 상품이 없으면 상품 등록
        CartProduct cartProduct = CartProduct.builder()
                .product(product)
                .cartType(cartType)
                .memberId(registerProductCartDto.getMemberId())
                .quantity(registerProductCartDto.getQuantity())
                .build();

        cartProductRepository.save(cartProduct);
        return true;
    }

    /**
     * 회원의 장바구니 상품 조회
     * @param memberId 회원 ID
     * @param cartTypeId 장바구니 타입 ID
     * @return 장바구니 상품 목록
     */
    @Transactional
    public List<CartProductResponse.RetrieveAllCartProduct> retrieveAllCartProducts(String memberId, Long cartTypeId) {

        List<CartProduct> cartProductList = cartProductRepository.findByMemberIdAndCartTypeIdWithProduct(memberId, cartTypeId);

        List<CartProductResponse.RetrieveAllCartProduct> cartProductResponseList = new ArrayList<>();

        for (CartProduct cartProduct : cartProductList) {
            Product product = cartProduct.getProduct();

            int finalPrice;

            if(cartTypeId == 1){
                finalPrice = product.getCalculatedBasePrice();
            } else if (cartTypeId == 2) {
                //맞춤고객 로직 필요

                finalPrice = product.getCalculatedRegularPrice();
            } else {
                finalPrice = product.getProductPrice();
            }

            CartProductResponse.RetrieveAllCartProduct response = CartProductResponse.RetrieveAllCartProduct.builder()
                    .cartProductId(cartProduct.getCartProductId())
                    .customerId(product.getCustomer().getCustomerId())
                    .storeName(product.getCustomer().getStoreName())
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productDescription(product.getProductDescription())
                    .productImage(product.getProductImage())
                    .productPrice(finalPrice)
                    .quantity(cartProduct.getQuantity())
                    .build();
            cartProductResponseList.add(response);
        }
        return cartProductResponseList;
    }

    /**
     * 장바구니 상품 삭제
     * @param cartProductId 장바구니 상품 ID
     * @exception
     * @throws IllegalArgumentException 존재하지 않는 장바구니 ID인 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean deleteCartProduct(Long cartProductId) {
        if(cartProductRepository.existsById(cartProductId)) {
            cartProductRepository.deleteById(cartProductId);
            return true;
        } else {
            throw new IllegalArgumentException("존재하지 않는 장바구니 ID 입니다.");
        }
    }

    /**
     * 장바구니 상품 개수 조절
     * @param cartProductId 장바구니 상품 ID
     * @param quantityDelta 수량 증감 값 (양수이면 증가, 음수이면 감소)
     * @exception IllegalStateException 존재하지 않는 장바구니 ID인 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean modifyProductQuantity(Long cartProductId, int quantityDelta) {
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 장바구니 ID 입니다."));

        int newQuantity = cartProduct.getQuantity() + quantityDelta;

        // 수량이 0 이하가 되지 않도록 제한
        if (newQuantity < 1) {
            throw new IllegalStateException("상품 수량은 1개 이상이어야 합니다.");
        }

        cartProduct.changeProductQuantity(newQuantity);
        cartProductRepository.save(cartProduct);
        return true;
    }
}
