package com.yeonieum.productservice.domain.cart.service;

import com.yeonieum.productservice.domain.cart.dto.CartProductRequest;
import com.yeonieum.productservice.domain.cart.dto.CartProductResponse;
import com.yeonieum.productservice.domain.cart.entity.CartProduct;
import com.yeonieum.productservice.domain.cart.entity.CartType;
import com.yeonieum.productservice.domain.cart.exception.CartException;
import com.yeonieum.productservice.domain.cart.repository.CartProductRepository;
import com.yeonieum.productservice.domain.cart.repository.CartTypeRepository;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.yeonieum.productservice.domain.cart.exception.CartExceptionCode.CART_NOT_FOUND;
import static com.yeonieum.productservice.domain.cart.exception.CartExceptionCode.CART_TYPE_NOT_FOUND;
import static com.yeonieum.productservice.domain.cart.exception.CartExceptionCode.QUANTITY_BELOW_MINIMUM;

@Service
@RequiredArgsConstructor
public class CartProductService {

    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final CartTypeRepository cartTypeRepository;

    /**
     * 장바구니에 상품 등록
     * @param ofRegisterProductCart 장바구니에 등록할 상품 정보 DTO
     * @throws IllegalStateException 존재하지 않는 상품 ID인 경우 발생
     * @throws CartException 존재하지 않는 장바구니 타입 ID인 경우 발생
     * @return 성공여부
     */
    @Transactional
    public boolean registerCartProduct(CartProductRequest.OfRegisterProductCart ofRegisterProductCart) {

        Product product = productRepository.findById(ofRegisterProductCart.getProductId())
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 상품 ID 입니다."));

        CartType cartType = cartTypeRepository.findById(ofRegisterProductCart.getCartTypeId())
                .orElseThrow(() -> new CartException(CART_TYPE_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<CartProduct> cartProductList = cartProductRepository.findByMemberIdAndCartTypeIdWithProduct(ofRegisterProductCart.getMemberId(), ofRegisterProductCart.getCartTypeId());

        for (CartProduct existingCartProduct : cartProductList) {
            if (existingCartProduct.getProduct().getProductId().equals(ofRegisterProductCart.getProductId())) {
                // 같은 상품이 있으면 수량 추가
                existingCartProduct.changeProductQuantity(existingCartProduct.getQuantity() + ofRegisterProductCart.getQuantity());
                cartProductRepository.save(existingCartProduct);
                return true;
            }
        }

        // 장바구니에 같은 상품이 없으면 상품 등록
        CartProduct cartProduct = ofRegisterProductCart.toEntity(product, cartType);

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
    public List<CartProductResponse.OfRetrieveCartProduct> retrieveAllCartProducts(String memberId, Long cartTypeId) {

        List<CartProduct> cartProductList = cartProductRepository.findByMemberIdAndCartTypeIdWithProduct(memberId, cartTypeId);

        List<CartProductResponse.OfRetrieveCartProduct> cartProductResponseList = new ArrayList<>();

        for (CartProduct cartProduct : cartProductList) {
            Product product = cartProduct.getProduct();

            int finalPrice;
            int finalDiscountRate;

            if(cartTypeId == 1){
                finalPrice = product.getCalculatedBasePrice();
                finalDiscountRate = product.getBaseDiscountRate();
            } else if (cartTypeId == 2) {
                //맞춤고객 로직 필요

                finalPrice = product.getCalculatedRegularPrice();
                finalDiscountRate = product.getRegularDiscountRate();
            } else {
                finalPrice = product.getProductPrice();
                finalDiscountRate = product.getBaseDiscountRate();
            }

            CartProductResponse.OfRetrieveCartProduct response = CartProductResponse.OfRetrieveCartProduct.convertedBy(cartProduct, finalPrice, finalDiscountRate);

            cartProductResponseList.add(response);
        }
        return cartProductResponseList;
    }

    /**
     * 장바구니 상품 삭제
     * @param cartProductId 장바구니 상품 ID
     * @throws IllegalArgumentException 존재하지 않는 장바구니 ID인 경우
     * @return 성공 여부
     */
    @Transactional
    public boolean deleteCartProduct(Long cartProductId) {
        if(cartProductRepository.existsById(cartProductId)) {
            cartProductRepository.deleteById(cartProductId);
            return true;
        } else {
            throw new CartException(CART_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 장바구니 상품 개수 조절
     * @param cartProductId 장바구니 상품 ID
     * @param quantityDelta 수량 증감 값 (양수이면 증가, 음수이면 감소)
     * @throws CartException 존재하지 않는 장바구니 ID인 경우
     * @throws CartException 상품 수량을 1개 미만으로 설정하려고 할 때
     * @return 성공 여부
     */
    @Transactional
    public boolean modifyProductQuantity(Long cartProductId, int quantityDelta) {
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(() -> new CartException(CART_NOT_FOUND, HttpStatus.NOT_FOUND));

        int newQuantity = cartProduct.getQuantity() + quantityDelta;

        // 수량이 0 이하가 되지 않도록 제한
        if (newQuantity < 1) {
            throw new CartException(QUANTITY_BELOW_MINIMUM, HttpStatus.BAD_REQUEST);
        }

        cartProduct.changeProductQuantity(newQuantity);
        cartProductRepository.save(cartProduct);
        return true;
    }

    /**
     * 장바구니 상품 개수 조회
     * @param memberId 회원 ID
     * @param cartTypeId 장바구니 타입 ID
     * @return 장바구니에 담긴 상품 수 (일반구매, 정기구매)
     */
    @Transactional
    public Long CountCartProduct(String memberId, Long cartTypeId) {
        return cartProductRepository.countByMemberIdAndOptionalCartTypeId(memberId, cartTypeId);
    }


    /**
     * 회원의 장바구니 상품 일괄 삭제
     * @param memberId
     */
    @Transactional
    public void deleteCartProductByMemberId(String memberId, Long cartTypeId) {
        cartProductRepository.deleteByMemberIdAndCartType(memberId, cartTypeId);
    }
}
