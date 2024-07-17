package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementResponse;
import com.yeonieum.productservice.domain.product.service.customerservice.AdvertisementManagementService;
import com.yeonieum.productservice.domain.product.service.memberservice.AdvertisementService;
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
@RequestMapping("/api/advertisement")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementManagementService advertisementManagementService;
    private final AdvertisementService advertisementService;

    /**
     * 상품광고서비스 신청
     * 상품광고서비스 조회
     * 광고 상품 조회(랜덤)
     */

    @Operation(summary = "상품 상단광고 조회", description = "고객이 신청한 상품 상단광고를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상단광고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/list")
    public ResponseEntity getAdvertisement(@RequestParam int startPage,
                                                @RequestParam int pageSize,
                                                @RequestParam long customerId) {
        List<AdvertisementResponse.OfRetrieve> advertisementProduct = advertisementManagementService.retrieveAppliedProduct(customerId);


        return new ResponseEntity(ApiResponse.builder()
                .result(advertisementProduct)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 상단광고 신청", description = "고객이 상품 상단광고를 신청합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상단광고 신청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/product")
    public ResponseEntity registerAdvertisement(@RequestBody AdvertisementRequest.OfRegister registerRequest) {
        advertisementManagementService.registerAdvertisement(registerRequest);

        return new ResponseEntity(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "고객들의 상품 상단광고 신청내역 조회", description = "관리자용 상단광고 신청내역 조회입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상단광고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/product/list")
    public ResponseEntity getAdvertisementProduct(@RequestParam int startPage,
                                                  @RequestParam int pageSize) {
        List<AdvertisementResponse.OfRetrieve> advertisementProduct = advertisementService.retrieveAdvertisementProductList();

        return new ResponseEntity(ApiResponse.builder()
                .result(advertisementProduct)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "신청한 상단광고 승인", description = "관리자가 상단광고신청에 대해 승인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상단광고 승인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PatchMapping("/{advertisementId}/approval")
    public ResponseEntity approveAdvertisement(@PathVariable Long advertisementId) {
        advertisementManagementService.approveAdvertisement(advertisementId);

        return new ResponseEntity(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
