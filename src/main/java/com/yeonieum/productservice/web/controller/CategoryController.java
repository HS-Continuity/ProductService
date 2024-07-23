package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.category.dto.category.ProductCategoryRequest;
import com.yeonieum.productservice.domain.category.dto.category.ProductCategoryResponse;
import com.yeonieum.productservice.domain.category.service.ProductCategoryService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ProductCategoryService productCategoryService;

    @Operation(summary = "상품 카테고리 등록", description = "새로운 상품 카테고리를 등록하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 카테고리 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 카테고리 등록 실패")
    })
    @Role(role = {"ROLE_ADMIN"}, url = "/api/category", method = "POST")
    @PostMapping
    public ResponseEntity<ApiResponse> registerProductCategory(@RequestBody ProductCategoryRequest.RegisterCategoryDto registerCategoryDto) {

        productCategoryService.registerCategory(registerCategoryDto);

        return new ResponseEntity<>(ApiResponse.builder()
            .result(null)
            .successCode(SuccessCode.INSERT_SUCCESS)
            .build(), HttpStatus.CREATED);
    }

    @Operation(summary = "상품 카테고리 조회", description = "상품 카테고리를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 카테고리 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 카테고리 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_ANONYMOUS"}, url = "/api/category", method = "GET")
    @GetMapping
    public ResponseEntity<ApiResponse> retrieveProductCategoryList() {

        List<ProductCategoryResponse.RetrieveAllCategoryDto> retrieveAllCategories = productCategoryService.retrieveAllCategories();

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveAllCategories)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 카테고리 수정", description = "상품 카테고리를 수정하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 카테고리 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 카테고리 수정 실패")
    })
    @Role(role = {"ROLE_ADMIN"}, url = "/api/category/{productCategoryId}", method = "PATCH")
    @PatchMapping("/{productCategoryId}")
    public ResponseEntity<ApiResponse> modifyProductCategory(
            @PathVariable Long productCategoryId, @RequestBody ProductCategoryRequest.ModifyCategoryDto modifyCategoryDto) {

        productCategoryService.modifyCategory(productCategoryId, modifyCategoryDto);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 카테고리 삭제", description = "상품 카테고리를 삭제하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "상품 카테고리 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 카테고리 삭제 실패")
    })
    @Role(role = {"ROLE_ADMIN"}, url = "/api/category/{productCategoryId}", method = "DELETE")
    @DeleteMapping("/{productCategoryId}")
    public ResponseEntity<ApiResponse> deleteProductCategory(@PathVariable Long productCategoryId) {

        productCategoryService.deleteCategory(productCategoryId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.DELETE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "하위 카테고리 조회", description = "상품 카테고리를 조회시, 하위 카테고리도 같이 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 하위 카테고리 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 하위 카테고리 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_ANONYMOUS"}, url = "/api/category/{productCategoryId}/detail", method = "GET")
    @GetMapping("/{productCategoryId}/detail")
    public ResponseEntity<ApiResponse> RetrieveCategoryWithDetails(@PathVariable Long productCategoryId) {

        ProductCategoryResponse.RetrieveCategoryWithDetailsDto retrieveCategoryWithDetails = productCategoryService.retrieveCategoryWithDetails(productCategoryId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveCategoryWithDetails)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
