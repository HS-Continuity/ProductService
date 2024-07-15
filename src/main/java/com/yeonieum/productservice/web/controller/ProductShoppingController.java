package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.memberservice.ProductShoppingResponse;
import com.yeonieum.productservice.domain.product.service.memberservice.ProductShoppingFacade;
import com.yeonieum.productservice.domain.product.service.memberservice.ProductShoppingService;
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
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> retrieveCategoryWithProducts(@PathVariable Long categoryId,
                                                                    @RequestParam(value = "isCertification", required = false) ActiveStatus isCertification,
                                                                    @RequestParam(defaultValue = "0") int startPage,
                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                    @RequestParam(defaultValue = "productName") String sort,
                                                                    @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        ProductShoppingResponse.RetrieveCategoryWithProductsDto retrieveCategoryWithProducts =
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


    @Operation(summary = "상세 상품 조회", description = "선택한 상품의 정보를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 정보 조회 실패")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> retrieveDetailProduct(@PathVariable Long productId) {

        ProductShoppingResponse.DetailProductInformationDto detailProductInformation =
                productShoppingService.detailProductInformationDto(productId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(detailProductInformation)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "키워드로 상품 조회", description = "입력한 키워드를 가진 상품을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "키워드 상품 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "키워드 상품 조회 실패")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> retrieveSearchKeywordProduct(@RequestParam(required = false) String keyword,
                                                                    @RequestParam(defaultValue = "0") int startPage,
                                                                    @RequestParam(defaultValue = "10") int pageSize,
                                                                    @RequestParam(defaultValue = "productName") String sort,
                                                                    @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveKeywordWithProducts =
                productShoppingFacade.retrieveKeywordWithProducts(keyword, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveKeywordWithProducts)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "업체 상품 조회", description = "선택한 업체의 상품들을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 상품 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "업체 상품 조회 실패")
    })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse> retrieveSearchCustomerProduct(@PathVariable Long customerId,
                                                                     @RequestParam(required = false) Long detailCategoryId,
                                                                     @RequestParam(defaultValue = "0") int startPage,
                                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                                     @RequestParam(defaultValue = "productName") String sort,
                                                                     @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageableUtil.createPageable(startPage, pageSize, sort, direction);

        ProductShoppingResponse.RetrieveSearchWithProductsDto retrieveCustomerWithProducts =
                productShoppingFacade.retrieveCustomerWithProductsDto(customerId, detailCategoryId, pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveCustomerWithProducts)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
