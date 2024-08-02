package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.cart.dto.CartProductRequest;
import com.yeonieum.productservice.domain.cart.dto.CartProductResponse;
import com.yeonieum.productservice.domain.cart.service.CartProductService;
import com.yeonieum.productservice.domain.product.service.memberservice.ProductShoppingFacade;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import com.yeonieum.productservice.global.usercontext.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-product")
@RequiredArgsConstructor
public class CartController {

    private final CartProductService cartProductService;
    private final ProductShoppingFacade productShoppingFacade;

    @Operation(summary = "장바구니 상품 등록", description = "회원의 장바구니에 상품을 등록하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "장바구니 상품 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "장바구니 상품 등록 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/cart-product", method = "POST")
    @PostMapping
    public ResponseEntity<ApiResponse> registerCartProduct(@RequestBody CartProductRequest.OfRegisterProductCart ofRegisterProductCart) {
        String member = UserContextHolder.getContext().getUserId();
        cartProductService.registerCartProduct(member, ofRegisterProductCart);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.CREATED);
    }

    @Operation(summary = "장바구니 상품 조회", description = "회원의 장바구니 상품들을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 상품 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "장바구니 상품 조회 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/cart-product", method = "GET")
    @GetMapping
    public ResponseEntity<ApiResponse> retrieveAllCartProducts(
            @RequestParam("memberId") String memberId,
            @RequestParam("cartTypeId") Long cartTypeId) {

        String member = UserContextHolder.getContext().getUserId();
        List<CartProductResponse.OfRetrieveCartProduct> ofRetrieveCartProducts = productShoppingFacade.retrieveAllCartProducts(member, cartTypeId);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(ofRetrieveCartProducts)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "장바구니 상품 삭제", description = "회원의 장바구니 상품을 삭제하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "장바구니 상품 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "장바구니 상품 삭제 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/cart-product/{cartProductId}", method = "DELETE")
    @DeleteMapping("/{cartProductId}")
    public ResponseEntity<ApiResponse> deleteCartProduct(@PathVariable("cartProductId") Long cartProductId) {
        String member = UserContextHolder.getContext().getUserId();
        cartProductService.deleteCartProduct(member, cartProductId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.DELETE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "장바구니 상품 개수 추가", description = "장바구니 상품의 개수를 1개 추가하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 상품 개수 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "장바구니 상품 개수 수정 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/cart-product/quantity/{cartProductId}/increment", method = "PUT")
    @PutMapping("/quantity/{cartProductId}/increment")
    public ResponseEntity<ApiResponse> increaseProductQuantity(@PathVariable("cartProductId") Long cartProductId) {
        String member = UserContextHolder.getContext().getUserId();
        cartProductService.modifyProductQuantity(member, cartProductId, 1);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "장바구니 상품 개수 차감", description = "장바구니 상품의 개수를 1개 차감하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 상품 개수 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "장바구니 상품 개수 수정 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/cart-product/quantity/{cartProductId}/decrement", method = "PUT")
    @PutMapping("/quantity/{cartProductId}/decrement")
    public ResponseEntity<ApiResponse> decreaseProductQuantity(@PathVariable("cartProductId") Long cartProductId) {
        String member = UserContextHolder.getContext().getUserId();
        cartProductService.modifyProductQuantity(member, cartProductId, -1);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "장바구니 상품 수 조회", description = "회원의 장바구니 상품 수를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "장바구니 상품 수 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "장바구니 상품 수 조회 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/cart-product/count", method = "GET")
    @GetMapping("count")
    public ResponseEntity<ApiResponse> countCartProduct(
            @RequestParam("memberId") String memberId,
            @RequestParam(value = "cartTypeId", required = false) Long cartTypeId) {

        String member = UserContextHolder.getContext().getUserId();
        Long countCartProduct = cartProductService.CountCartProduct(member, cartTypeId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(countCartProduct)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    // 회원의 장바구니 일괄삭제
    @Operation(summary = "장바구니 상품 일괄 삭제", description = "회원의 장바구니 상품을 일괄 삭제하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "장바구니 상품 일괄 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "장바구니 상품 일괄 삭제 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/cart-product/bulk", method = "DELETE")
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteAllCartProduct(@RequestParam("cartTypeId") Long cartTypeId, @RequestParam("cartIds") List<Long> cartIds) {
        String member = UserContextHolder.getContext().getUserId();
        cartProductService.deleteCartProductByMemberId(member, cartTypeId, cartIds);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.DELETE_SUCCESS)
                .build(), HttpStatus.valueOf(204));
    }
}