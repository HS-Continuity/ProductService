package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.cart.dto.CartProductResponse;
import com.yeonieum.productservice.domain.customer.dto.CustomerResponse;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.service.CustomerService;
import com.yeonieum.productservice.global.paging.PageableUtil;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "고객 목록 조회", description = "고객 목록을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고객 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "고객 목록 조회 실패")
    })
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> retrieveAllCustomers(
            @RequestParam(defaultValue = "0") int startPage,
            @RequestParam(defaultValue = "10") int pageSize) {

        Pageable pageable =  PageRequest.of(startPage, pageSize);

        Page<CustomerResponse.RetrieveCustomerDto> retrieveCustomers
                = customerService.retrieveCustomers(pageable);


        return new ResponseEntity<>(ApiResponse.builder()
                .result(retrieveCustomers)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "고객 상세 정보 조회", description = "고객의 상세 정보를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고객 상세 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "고객 상세 정보 조회 실패")
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse> retrieveDetailCustomer(@PathVariable("customerId") Long customerId) {

        CustomerResponse.RetrieveDetailCustomerDto targetCustomer = customerService.retrieveDetailCustomers(customerId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(targetCustomer)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
