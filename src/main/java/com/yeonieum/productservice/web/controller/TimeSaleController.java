package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleProductResponse;
import com.yeonieum.productservice.domain.product.dto.customerservice.RetrieveTimeSaleResponse;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimeSaleRequest;
import com.yeonieum.productservice.domain.product.service.customerservice.TimeSaleService;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-sale")
@RequiredArgsConstructor
public class TimeSaleController {
    private final TimeSaleService timeSaleService;

    // TimeSaleService의 메서드를 바탕으로 타임세일 조회, 등록, 타임세일 상품 조회를 구현해주세요.
    // 타임세일 조회, 등록, 타임세일 상품 조회를 위한 API를 구현해주세요.

    // 고객이 신청한 타임세일 조회
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getTimeSale() {
        Long customerId = 1L;
        List<RetrieveTimeSaleResponse> customersTimeSaleList = timeSaleService.retrieveTimeSaleProducts(customerId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(customersTimeSaleList)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    // 타임세일 등록
    @PostMapping
    public ResponseEntity<ApiResponse> registerTimeSale(@RequestBody TimeSaleRequest.RegisterDto registerDto) {
        timeSaleService.registerTimeSale(registerDto);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(),HttpStatus.CREATED);
    }

    // 타임세일 상품 조회
    @GetMapping("/{timeSaleId}")
    public ResponseEntity<ApiResponse> getTimeSaleProduct(@PathVariable Long timeSaleId) {
        RetrieveTimeSaleResponse timeSaleRespone = timeSaleService.retrieveCustomersTimeSale(timeSaleId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(timeSaleRespone)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @PatchMapping("/{timesaleId}")
    public ResponseEntity<ApiResponse> updateTimeSaleProduct(@PathVariable Long timesaleId, TimeSaleRequest.ModifyStatusDto modifyStatusDto) {
        // 타임세일 상품 수정
        timeSaleService.modifyTimeSaleStatus(modifyStatusDto);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    // 타임세일 상품리스트 조회
    @GetMapping("/product/list")
    public ResponseEntity<ApiResponse> getTimeSaleProductList(@RequestParam int pageNumber,
                                                              @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<RetrieveTimeSaleProductResponse> timeSaleProductResponseList = timeSaleService.retrieveTimeSaleProducts(pageable);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(timeSaleProductResponseList)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
