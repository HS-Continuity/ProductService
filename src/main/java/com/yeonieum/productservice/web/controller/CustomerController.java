package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.customer.dto.CustomerResponse;
import com.yeonieum.productservice.domain.customer.service.CustomerService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Role(role = {"ROLE_ADMIN"}, url = "/api/customer/list", method = "GET")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> retrieveAllCustomers(
            @RequestParam(defaultValue = "0") int startPage,
            @RequestParam(defaultValue = "10") int pageSize) {

        Pageable pageable =  PageRequest.of(startPage, pageSize);

        Page<CustomerResponse.OfRetrieveCustomer> retrieveCustomers
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
    @Role(role = {"*"}, url = "/api/customer/{customerId}", method = "GET")
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse> retrieveDetailCustomer(@PathVariable("customerId") Long customerId) {

        CustomerResponse.OfRetrieveDetailCustomer targetCustomer = customerService.retrieveDetailCustomers(customerId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(targetCustomer)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "업체의 배송비 조회", description = "업체의 배송비를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업체 배송비 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "업체 배송비 조회 실패")
    })
    @Role(role = {"ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_MEMBER", "ROLE_GUEST", "ROLE_ANONYMOUS"}, url = "/api/customer/delivery-fee/{customerId}", method = "GET")
    @GetMapping("/delivery-fee/{customerId}")
    public ResponseEntity<ApiResponse> retrieveDeliveryFee(@PathVariable("customerId") Long customerId) {

        int deliveryFee = customerService.retrieveDeliveryFee(customerId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(deliveryFee)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "인증용 고객 정보 조회", description = "인증용 고객 정보를 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증용 고객 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "인증용 고객 정보 조회 실패")
    })
    @Role(role = {"*"}, url = "/api/customer/auth/{businessNumber}", method = "GET")
    @GetMapping("/auth/{businessNumber}")
    public ResponseEntity<ApiResponse> retrieveDetailForAuth(@PathVariable("businessNumber") String businessNumber) {
        System.out.println("들어왔다.");
        return new ResponseEntity<>(ApiResponse.builder()
                .result(customerService.retrieveCustomerForAuth(businessNumber))
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
