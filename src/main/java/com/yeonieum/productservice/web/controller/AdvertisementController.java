package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.customerservice.RegisterAdvertisementRequestDto;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveAdvertisementProductResponseDto;
import com.yeonieum.productservice.domain.product.dto.memberservice.RetrieveAdvertisementProductResponse;
import com.yeonieum.productservice.domain.product.service.customerservice.AdvertisementManagementService;
import com.yeonieum.productservice.domain.product.service.memberservice.AdvertisementService;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
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


    // 상품광고 서비스 신청
    @GetMapping("/list")
    public ResponseEntity getAdvertisement(@RequestParam int startPage,
                                                @RequestParam int pageSize,
                                                @RequestParam long customerId) {
        List<RetrieveAdvertisementProductResponseDto> advertisementProduct = advertisementManagementService.retrieveAppliedProduct(customerId);


        return new ResponseEntity(ApiResponse.builder()
                .result(advertisementProduct)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @PostMapping("/product")
    public ResponseEntity registerAdvertisement(@RequestBody RegisterAdvertisementRequestDto registerAdvertisementRequestDto) {
        advertisementManagementService.registerAdvertisement(registerAdvertisementRequestDto);

        return new ResponseEntity(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.OK);
    }


    @GetMapping("/product/list")
    public ResponseEntity getAdvertisementProduct(@RequestParam int startPage,
                                                 @RequestParam int pageSize) {
        List<RetrieveAdvertisementProductResponse> advertisementProduct = advertisementService.retrieveAdvertisementProductList();

        return new ResponseEntity(ApiResponse.builder()
                .result(advertisementProduct)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
