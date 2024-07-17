package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleRequestForCustomer;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleResponseForCustomer;
import com.yeonieum.productservice.domain.product.dto.memberservice.TimesaleResponseForMember;
import com.yeonieum.productservice.domain.product.service.customerservice.TimesaleManagementService;
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
public class TimesaleController {
    private final TimesaleManagementService timesaleManagementService;

    // TimesaleService의 메서드를 바탕으로 타임세일 조회, 등록, 타임세일 상품 조회를 구현해주세요.
    // 타임세일 조회, 등록, 타임세일 상품 조회를 위한 API를 구현해주세요.

    // 고객이 신청한 타임세일 조회
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getTimesale() {
        Long customerId = 1L;
        List<TimesaleResponseForCustomer.OfRetrieve> customersTimesaleList = timesaleManagementService.retrieveTimesaleProducts(customerId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(customersTimesaleList)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    // 타임세일 등록
    @PostMapping
    public ResponseEntity<ApiResponse> registerTimesale(@RequestBody TimesaleRequestForCustomer.OfRegister registerRequest) {
        timesaleManagementService.registerTimesale(registerRequest);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(),HttpStatus.CREATED);
    }

    // 타임세일 상품 조회
    @GetMapping("/{timesaleId}")
    public ResponseEntity<ApiResponse> getTimesaleProduct(@PathVariable Long timesaleId) {
        TimesaleResponseForCustomer.OfRetrieve timesaleRespone = timesaleManagementService.retrieveCustomersTimesale(timesaleId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(timesaleRespone)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @PatchMapping("/{timesaleId}")
    public ResponseEntity<ApiResponse> updateTimesaleProduct(@PathVariable Long timesaleId, TimesaleRequestForCustomer.OfModifyStatus ofModifyStatus) {
        // 타임세일 상품 수정
        timesaleManagementService.modifyTimesaleStatus(ofModifyStatus);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    // 타임세일 상품리스트 조회
    @GetMapping("/product/list")
    public ResponseEntity<ApiResponse> getTimesaleProductList(@RequestParam int pageNumber,
                                                              @RequestParam int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<TimesaleResponseForMember.OfRetrieve> timesaleProductResponseList = timesaleManagementService.retrieveTimesaleProducts(pageable);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(timesaleProductResponseList)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
