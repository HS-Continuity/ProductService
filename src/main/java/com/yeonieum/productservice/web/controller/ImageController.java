package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.image.dto.ImageResponse;
import com.yeonieum.productservice.domain.image.service.ImageService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "상품 상세 이미지 조회", description = "선택한 상품의 상세 이미지를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 이미지 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 상세 이미지 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_ANONYMOUS"}, url = "/api/product-image/{productId}", method = "GET")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> retrieveProductReviews(@PathVariable Long productId) {

        List<ImageResponse.OfRetrieveDetailImage> productDetailImages = imageService.retrieveProductDetailImages(productId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(productDetailImages)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 인증서 정보 조회", description = "선택한 상품의 인증서 정보를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 인증서 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 인증서 정보 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_ANONYMOUS"}, url = "/api/product-image/certification/{productId}", method = "GET")
    @GetMapping("/certification/{productId}")
    public ResponseEntity<ApiResponse> retrieveProductCertification(@PathVariable Long productId) {

        ImageResponse.OfRetrieveCertification productCertification = imageService.retrieveProductCertification(productId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(productCertification)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
