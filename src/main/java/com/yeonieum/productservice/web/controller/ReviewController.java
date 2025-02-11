package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.S3Upload.S3UploadService;
import com.yeonieum.productservice.domain.review.dto.ProductReviewRequest;
import com.yeonieum.productservice.domain.review.dto.ProductReviewResponse;
import com.yeonieum.productservice.domain.review.service.ProductReviewService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.paging.PageableUtil;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import com.yeonieum.productservice.global.usercontext.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/product-review")
@RequiredArgsConstructor
public class ReviewController {

    private final ProductReviewService productReviewService;
    private final S3UploadService s3UploadService;

    @Operation(summary = "상품 리뷰 등록", description = "상품 리뷰를 등록하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 리뷰 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 리뷰 등록 실패")
    })
    @Role(role = {"ROLE_MEMBER"}, url = "/api/product-review", method = "POST")
    @PostMapping
    public ResponseEntity<ApiResponse> registerProductReview(
            @Valid @RequestPart(value = "ofRegisterProductReview") ProductReviewRequest.OfRegisterProductReview ofRegisterProductReview,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        String imageUrl = null;
        String member = UserContextHolder.getContext().getUserId();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = s3UploadService.uploadImage(imageFile);
        }

        productReviewService.registerProductReview(member, ofRegisterProductReview, imageUrl);

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
    @Role(role = {"ROLE_MEMBER"}, url = "/api/product-review/{productReviewId}", method = "DELETE")
    @DeleteMapping("/{productReviewId}")
    public ResponseEntity<ApiResponse> deleteProductReview(@PathVariable Long productReviewId) {
        String member = UserContextHolder.getContext().getUserId();
        productReviewService.deleteProductReview(member, productReviewId);

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
    @Role(role = {"*"}, url = "/api/product-review/{productId}", method = "GET")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> retrieveProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int startPage,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        Page<ProductReviewResponse.OfRetrieveProductWithReview> retrieveProductWithReviews
                = productReviewService.retrieveProductWithReviews(productId, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveProductWithReviews)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
