package com.yeonieum.productservice.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.AdvertisementResponse;
import com.yeonieum.productservice.domain.product.service.customerservice.AdvertisementManagementService;
import com.yeonieum.productservice.domain.product.service.memberservice.AdvertisementService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import com.yeonieum.productservice.global.usercontext.UserContextHolder;
import com.yeonieum.productservice.infrastructure.messaging.ProductEventProducer;
import com.yeonieum.productservice.infrastructure.messaging.message.AdvertisementEventMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    private final ProductEventProducer productEventProducer;


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
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/advertisement/list", method = "GET")
    @GetMapping("/list")
    public ResponseEntity getAdvertisement(@RequestParam int startPage,
                                           @RequestParam int pageSize,
                                           @RequestParam long customerId) {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        List<AdvertisementResponse.OfRetrieve> advertisementProduct = advertisementManagementService.retrieveAppliedProduct(customer);

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
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/advertisement/product", method = "POST")
    @PostMapping("/product")
    public ResponseEntity registerAdvertisement(@Valid @RequestBody AdvertisementRequest.OfRegister registerRequest) throws JsonProcessingException {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        Long id= advertisementManagementService.registerAdvertisement(customer, registerRequest);

        AdvertisementEventMessage advertisementEventMessage = registerRequest.toEventMessage(id);
        productEventProducer.sendMessage(advertisementEventMessage);
        return new ResponseEntity(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "회원용 상단광고 조회", description = "회원용 상단광고 신청내역 조회입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상단광고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"*"}, url = "/api/advertisement/product/list", method = "GET")
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
    @Role(role = {"ROLE_ADMIN"}, url = "/api/advertisement/{advertisementId}/approval", method = "PATCH")
    @PatchMapping("/{advertisementId}/approval")
    public ResponseEntity approveAdvertisement(@PathVariable Long advertisementId) {
        advertisementManagementService.approveAdvertisement(advertisementId);

        return new ResponseEntity(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
