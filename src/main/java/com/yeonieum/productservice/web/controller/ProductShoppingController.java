package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.service.memberservice.ProductShoppingFacade;
import com.yeonieum.productservice.domain.product.service.memberservice.ProductShoppingService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.global.paging.PageableUtil;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping/product")
@RequiredArgsConstructor
public class ProductShoppingController {

    private final ProductShoppingService productShoppingService;
    private final ProductShoppingFacade productShoppingFacade;

    @Operation(summary = "카테고리 상품 조회", description = "선택한 카테고리의 상품들을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카테고리 상품 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "카테고리 상품 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_ANONYMOUS"}, url = "/api/shopping/product/category/{categoryId}", method = "GET")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> retrieveCategoryWithProducts(@PathVariable Long categoryId,
                                                                    @RequestParam(value = "isCertification", required = false) ActiveStatus isCertification,
                                                                    @RequestParam(defaultValue = "0") int startPage,
                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                    @RequestParam(defaultValue = "productName") String sort,
                                                                    @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        Page<ProductShoppingResponse.OfRetrieveCategoryWithProduct> retrieveCategoryWithProducts =
                productShoppingFacade.retrieveCategoryWithProducts(categoryId, isCertification, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveCategoryWithProducts)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상세 카테고리 상품 조회", description = "선택한 상세 카테고리의 상품들을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상세 카테고리 상품 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상세 카테고리 상품 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_ANONYMOUS"}, url = "/api/shopping/product/detail-category/{detailCategoryId}", method = "GET")
    @GetMapping("/detail-category/{detailCategoryId}")
    public ResponseEntity<ApiResponse> retrieveDetailCategoryWithProducts(@PathVariable Long detailCategoryId,
                                                                          @RequestParam(value = "isCertification", required = false) ActiveStatus isCertification,
                                                                          @RequestParam(defaultValue = "0") int startPage,
                                                                          @RequestParam(defaultValue = "10") int pageSize,
                                                                          @RequestParam(defaultValue = "productName") String sort,
                                                                          @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        Page<ProductShoppingResponse.OfRetrieveDetailCategoryWithProduct> retrieveDetailCategoryWithProducts =
                productShoppingFacade.retrieveDetailCategoryWithProducts(detailCategoryId, isCertification, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveDetailCategoryWithProducts)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }


    @Operation(summary = "상품 상세 정보 조회", description = "선택한 상품의 상세 정보를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 상세 정보 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER",  "ROLE_ANONYMOUS"}, url = "/api/shopping/product/{productId}", method = "GET")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> retrieveDetailProduct(@PathVariable Long productId) {

        ProductShoppingResponse.OfDetailProductInformation detailProductInformation =
                productShoppingService.detailProductInformation(productId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(detailProductInformation)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "필터링된 상품 조회", description = "(키워드, 친환경)으로 필터링 된 상품을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "필터링 상품 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "필터링 상품 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_ANONYMOUS"}, url = "/api/shopping/product/search", method = "GET")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> retrieveSearchFilterProduct(@RequestParam(required = false) String keyword,
                                                                    @RequestParam(value = "isCertification", required = false) ActiveStatus isCertification,
                                                                    @RequestParam(defaultValue = "0") int startPage,
                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                    @RequestParam(defaultValue = "productName") String sort,
                                                                    @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        Page<ProductShoppingResponse.OfSearchProductInformation> retrieveFilteringProducts =
                productShoppingFacade.retrieveFilteringProducts(keyword, isCertification, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveFilteringProducts)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "업체 상품 조회", description = "선택한 업체의 상품들을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 상품 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "업체 상품 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER",  "ROLE_ANONYMOUS"}, url = "/api/shopping/product/customer/{customerId}", method = "GET")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse> retrieveSearchCustomerProduct(@PathVariable Long customerId,
                                                                     @RequestParam(required = false) Long detailCategoryId,
                                                                     @RequestParam(defaultValue = "0") int startPage,
                                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                                     @RequestParam(defaultValue = "productName") String sort,
                                                                     @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        Page<ProductShoppingResponse.OfSearchProductInformation> retrieveCustomerWithProducts =
                productShoppingFacade.retrieveCustomerWithProducts(customerId, detailCategoryId, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveCustomerWithProducts)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "키워드 검색 순위 조회", description = "키워드 검색 순위를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "키워드 검색 순위 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "키워드 검색 순위 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_GUEST", "ROLE_ANONYMOUS"}, url = "/api/shopping/product/ranking", method = "GET")
    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse> retrieveSearchRank() {

        List<ProductShoppingResponse.OfSearchRank> searchRanks = productShoppingService.retrieveSearchRank();

        return new ResponseEntity<>(ApiResponse.builder()
                .result(searchRanks)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
