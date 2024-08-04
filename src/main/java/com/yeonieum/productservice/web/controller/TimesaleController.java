package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleRequestForCustomer;
import com.yeonieum.productservice.domain.product.dto.customerservice.TimesaleResponseForCustomer;
import com.yeonieum.productservice.domain.product.dto.memberservice.TimesaleResponseForMember;
import com.yeonieum.productservice.domain.product.service.customerservice.TimesaleManagementFacade;
import com.yeonieum.productservice.domain.product.service.customerservice.TimesaleManagementService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import com.yeonieum.productservice.global.usercontext.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final TimesaleManagementFacade timesaleManagementFacade;

    // TimesaleService의 메서드를 바탕으로 타임세일 조회, 등록, 타임세일 상품 조회를 구현해주세요.
    // 타임세일 조회, 등록, 타임세일 상품 조회를 위한 API를 구현해주세요.

    @Operation(summary = "고객들의 타임세일 신청내역 리스트 조회", description = "고객이 신청했던 타임세일 리스트를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "타임세일 리스트 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/time-sale/list", method = "GET")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getTimesale() {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUserId());
        List<TimesaleResponseForCustomer.OfRetrieve> customersTimesaleList = timesaleManagementService.retrieveTimesaleProducts(customer);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(customersTimesaleList)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "고객들의 타임세일 신청", description = "고객이 타임세일을 신청합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "타임세일 신청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/time-sale", method = "POST")
    @PostMapping
    public ResponseEntity<ApiResponse> registerTimesale(@Valid @RequestBody TimesaleRequestForCustomer.OfRegister registerRequest) {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUserId());
        timesaleManagementService.registerTimesale(customer, registerRequest);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(),HttpStatus.CREATED);
    }

    @Operation(summary = "타임세일 상세 조회", description = "고객이 타임세일에 대한 상세내용을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "타임세일 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/time-sale/{timesaleId}", method = "GET")
    @GetMapping("/{timesaleId}")
    public ResponseEntity<ApiResponse> getTimesaleProduct(@PathVariable Long timesaleId) {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUserId());
        TimesaleResponseForCustomer.OfRetrieve timesaleRespone = timesaleManagementService.retrieveCustomersTimesale(customer, timesaleId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(timesaleRespone)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "고객의 타임세일 취소", description = "고객이 타임세일 신청건을 취소합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "타임세일 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/time-sale/{timesaleId}/cancel", method = "PATCH")
    @PatchMapping("/{timesaleId}/cancel")
    public ResponseEntity<ApiResponse> updateTimesaleProduct(@PathVariable Long timesaleId) {
        // 타임세일 신청 취소
        Long customer = Long.valueOf(UserContextHolder.getContext().getUserId());
        timesaleManagementService.cancelTimesale(customer, timesaleId);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "회원용 타임세일 상품 조회", description = "회원이 타임세일 상품 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "타임세일 상품 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"*"}, url = "/api/time-sale/product/list", method = "GET")
    @GetMapping("/product/list")
    public ResponseEntity<ApiResponse> getTimesaleProductListForMember(@RequestParam(defaultValue = "0") int startPage,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(startPage, pageSize);
        Page<TimesaleResponseForMember.OfRetrieve> timesaleProductResponseList = timesaleManagementFacade.retrieveTimesaleProducts(pageable);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(timesaleProductResponseList)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "회원용 타임세일 상품 조회", description = "회원이 타임세일 상품 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "타임세일 상품 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"*"}, url = "/api/time-sale/product", method = "GET")
    @GetMapping("/{timesaleId}/product")
    public ResponseEntity<ApiResponse> getTimesaleProductForMember(@PathVariable Long timesaleId) {
        return new ResponseEntity<>(ApiResponse.builder()
                .result(timesaleManagementFacade.retrieveTimesaleProduct(timesaleId))
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
