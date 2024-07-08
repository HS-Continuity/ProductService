package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.cart.dto.CartProductRequest;
import com.yeonieum.productservice.domain.cart.dto.CartProductResponse;
import com.yeonieum.productservice.domain.cart.service.CartProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-products")
@RequiredArgsConstructor
public class CartController {

    private final CartProductService cartProductService;

    /**
     * 장바구니에 상품 등록
     * @param registerProductCartDto 장바구니에 담을 상품 정보
     * @return 성공 -> 성공 메시지와 200 OK, 실패 -> 실패 메시지와 500 Error
     */
//    @Operation(summary = "장바구니 상품 등록", description = "장바구니에 상품을 등록합니다.")
//    @ApiResponses({
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "장바구니 상품 등록 성공"),
//            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
//    })
    @PostMapping("")
    public ResponseEntity<String> registerCartProduct(@RequestBody CartProductRequest.RegisterProductCartDto registerProductCartDto) {
        boolean isRegistered = cartProductService.registerCartProduct(registerProductCartDto);

        if (isRegistered) {
            return ResponseEntity.ok("장바구니에 상품이 성공적으로 담겼습니다.");
        } else {
            return ResponseEntity.status(500).body("장바구니에 상품을 담는 것을 실패했습니다.");
        }
    }

    /**
     * 회원의 장바구니 상품 조회
     * @param memberId 회원 ID
     * @param cartTypeId 장바구니 유형 ID
     * @return 장바구니 상품 목록
     */
    @GetMapping("")
    public ResponseEntity<List<CartProductResponse.RetrieveAllCartProduct>> retrieveAllCartProducts(
            @RequestParam("memberId") String memberId,
            @RequestParam("cartTypeId") Long cartTypeId) {

        List<CartProductResponse.RetrieveAllCartProduct> cartProductResponseList = cartProductService.retrieveAllCartProducts(memberId, cartTypeId);
        return ResponseEntity.ok(cartProductResponseList);
    }

    /**
     * 장바구니에서 상품 삭제
     * @param cartProductId 장바구니 상품 ID
     * @return 성공 -> 성공 메시지와 200 OK, 실패 -> 실패 메시지와 500 Error
     */
    @DeleteMapping("/{cartProductId}")
    public ResponseEntity<String> deleteCartProduct(@PathVariable("cartProductId") Long cartProductId) {
        boolean isDeleted = cartProductService.deleteCartProduct(cartProductId);

        if (isDeleted) {
            return ResponseEntity.ok("장바구니에서 상품이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(500).body("장바구니에서 상품 삭제를 실패했습니다.");
        }
    }

    /**
     * 장바구니 상품 수량 증가
     * @param cartProductId 장바구니 상품 ID
     * @return 성공 -> 성공 메시지와 200 OK, 실패 -> 실패 메시지와 500 Error
     */
    @PutMapping("/quantity/{cartProductId}/increment")
    public ResponseEntity<String> increaseProductQuantity(
            @PathVariable("cartProductId") Long cartProductId) {
        boolean isModified = cartProductService.modifyProductQuantity(cartProductId, 1);

        if (isModified) {
            return ResponseEntity.ok("장바구니 상품 수량이 성공적으로 증가되었습니다.");
        } else {
            return ResponseEntity.status(500).body("장바구니 상품 수량 증가를 실패했습니다.");
        }
    }

    /**
     * 장바구니 상품 수량 감소
     * @param cartProductId 장바구니 상품 ID
     * @return 성공 -> 성공 메시지와 200 OK, 실패 -> 실패 메시지와 500 Error
     */
    @PutMapping("/quantity/{cartProductId}/decrement")
    public ResponseEntity<String> decreaseProductQuantity(
            @PathVariable("cartProductId") Long cartProductId) {
        boolean isModified = cartProductService.modifyProductQuantity(cartProductId, -1);

        if (isModified) {
            return ResponseEntity.ok("장바구니 상품 수량이 성공적으로 감소되었습니다.");
        } else {
            return ResponseEntity.status(500).body("장바구니 상품 수량 감소를 실패했습니다.");
        }
    }
}