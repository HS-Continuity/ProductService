package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.category.dto.detailcategory.ProductDetailCategoryRequest;
import com.yeonieum.productservice.domain.category.service.ProductDetailCategoryService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detail-category")
@RequiredArgsConstructor
public class DetailCategoryController {

    private final ProductDetailCategoryService productDetailCategoryService;

    @Operation(summary = "상품 상세 카테고리 등록", description = "새로운 상품 상세 카테고리를 등록하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 상세 카테고리 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 상세 카테고리 등록 실패")
    })
    @Role(role = {"ROLE_ADMIN"}, url = "/api/detail-category", method = "POST")
    @PostMapping
    public ResponseEntity<ApiResponse> registerProductDetailCategory(@Valid @RequestBody ProductDetailCategoryRequest.RegisterDetailCategoryDto registerDetailCategoryDto) {

        productDetailCategoryService.registerProductDetailCategory(registerDetailCategoryDto);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.CREATED);
    }

    @Operation(summary = "상품 상세 카테고리 수정", description = "상품 상세 카테고리를 수정하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 카테고리 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 상세 카테고리 수정 실패")
    })
    @Role(role = {"ROLE_ADMIN"}, url = "/api/detail-category/{productDetailCategoryId}", method = "PATCH")
    @PatchMapping("/{productDetailCategoryId}")
    public ResponseEntity<ApiResponse> modifyProductCategory(
            @PathVariable Long productDetailCategoryId, @Valid @RequestBody ProductDetailCategoryRequest.ModifyDetailCategoryDto productDetailCategoryDto) {

        productDetailCategoryService.modifyDetailCategory(productDetailCategoryId, productDetailCategoryDto);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 상세 카테고리 삭제", description = "상품 상세 카테고리를 삭제하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "상품 상세 카테고리 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 상세 카테고리 삭제 실패")
    })
    @Role(role = {"ROLE_ADMIN"}, url = "/api/detail-category/{productDetailCategoryId}", method = "DELETE")
    @DeleteMapping("/{productDetailCategoryId}")
    public ResponseEntity<ApiResponse> deleteProductDetailCategory(@PathVariable Long productDetailCategoryId) {

        productDetailCategoryService.deleteDetailCategory(productDetailCategoryId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.DELETE_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
