package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.customer.dto.CustomerResponse;
import com.yeonieum.productservice.domain.review.dto.ProductReviewRequest;
import com.yeonieum.productservice.domain.review.dto.ProductReviewResponse;
import com.yeonieum.productservice.domain.review.service.ProductReviewService;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-review")
@RequiredArgsConstructor
public class ReviewController {

    private final ProductReviewService productReviewService;

    @Operation(summary = "상품 리뷰 등록", description = "상품 리뷰를 등록하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 리뷰 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 리뷰 등록 실패")
    })
    @PostMapping
    public ResponseEntity<ApiResponse> registerProductReview(@RequestBody ProductReviewRequest.RegisterProductReviewDto registerProductReviewDto) {

        productReviewService.registerProductReview(registerProductReviewDto);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.CREATED);
    }

    @Operation(summary = "상품 리뷰 삭제", description = "상품 리뷰를 삭제하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "상품 리뷰 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 리뷰 삭제 실패")
    })
    @DeleteMapping("/{productReviewId}")
    public ResponseEntity<ApiResponse> deleteProductReview(@PathVariable Long productReviewId) {

        productReviewService.deleteProductReview(productReviewId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.DELETE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 리뷰 조회", description = "선택한 상품 리뷰를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 리뷰 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 리뷰 조회 실패")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> retrieveProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int startPage,
            @RequestParam(defaultValue = "10") int pageSize) {

        Pageable pageable =  PageRequest.of(startPage, pageSize);

        Page<ProductReviewResponse.RetrieveProductWithReviewsDto> retrieveProductWithReviews
                = productReviewService.retrieveProductWithReviews(productId, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveProductWithReviews)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
