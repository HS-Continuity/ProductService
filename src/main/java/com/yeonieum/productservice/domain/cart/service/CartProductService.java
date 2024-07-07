package com.yeonieum.productservice.domain.cart.service;

import com.yeonieum.productservice.domain.cart.dto.CartProductRequest;
import com.yeonieum.productservice.domain.cart.dto.CartProductResponse;
import com.yeonieum.productservice.domain.cart.entity.CartProduct;
import com.yeonieum.productservice.domain.cart.entity.CartType;
import com.yeonieum.productservice.domain.cart.repository.CartProductRepository;
import com.yeonieum.productservice.domain.cart.repository.CartTypeRepository;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
     * @param registerProductCartDto
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public boolean registerCartProduct(CartProductRequest.RegisterProductCartDto registerProductCartDto) {

        Product product = productRepository.findById(registerProductCartDto.getProductId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 상품 ID 입니다."));

        CartType cartType = cartTypeRepository.findById(registerProductCartDto.getProductCartId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 장바구니타입 ID 입니다."));

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
     * @param
     * @exception
     * @throws
     * @return
     */
    @Transactional
    public List<CartProductResponse.RetrieveAllCartProduct> retrieveAllCartProducts(String memberId){

        List<CartProduct> cartProductList = cartProductRepository.findByMemberIdWithProduct(memberId);

        List<CartProductResponse.RetrieveAllCartProduct> cartProductResponseList = new ArrayList<>();

        for (CartProduct cartProduct : cartProductList) {
            Product product = cartProduct.getProduct();
            CartProductResponse.RetrieveAllCartProduct response = CartProductResponse.RetrieveAllCartProduct.builder()
                    .cartProductId(cartProduct.getCartProductId())
                    .customerId(product.getCustomer().getCustomerId())
                    .storeName(product.getCustomer().getStoreName())
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productDescription(product.getProductDescription())
                    .productImage(product.getProductImage())
                    .productPrice(product.getProductPrice())
                    .quantity(cartProduct.getQuantity())
                    .build();
            cartProductResponseList.add(response);
        }
        return cartProductResponseList;
    }

    /**
     * 장바구니 상품 삭제
     * @param cartProductId
     * @exception
     * @throws
     * @return
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
}
