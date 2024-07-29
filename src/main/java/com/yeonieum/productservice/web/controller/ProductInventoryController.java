package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.domain.product.service.customerservice.ProductManagementService;
import com.yeonieum.productservice.domain.productinventory.dto.ProductInventoryManagementRequest;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.service.ProductInventoryManagementService;
import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class ProductInventoryController {
    private final StockSystemService stockSystemService;
    private final ProductManagementService productManagementService;
    private final ProductInventoryManagementService productInventoryManagementService;

    @Role(role = {"ROLE_ADMIN", "ROLE_MEMBER"}, url = "/api/inventory/stock-usage", method = "POST")
    @PostMapping("/stock-usage")
    public ResponseEntity<AvailableProductInventoryResponseList> getAvailableOrderProduct(@RequestBody StockUsageRequest.IncreaseStockUsageList increaseStockUsageListDtoList) {
        List<AvailableProductInventoryResponse> responseList = new ArrayList<>();
        for(StockUsageRequest.OfIncreasing ofIncreasing : increaseStockUsageListDtoList.getOfIncreasingList()) {
            AvailableProductInventoryResponse response = stockSystemService.processProductInventory(ofIncreasing);
            responseList.add(response);
        }
        return ResponseEntity.ok(AvailableProductInventoryResponseList.builder()
                .availableProductInventoryResponseList(responseList)
                .build());
    }

    @PostMapping("/regular-order/stock-usage")
    public ResponseEntity<List<ProductManagementResponse.OfOrderInformation>> getAvailableRegularDelivery(@RequestBody StockUsageRequest.IncreaseStockUsageList increaseStockUsageList) {
        List<ProductManagementResponse.OfOrderInformation> responseList = productManagementService.retrieveOrderInformation(increaseStockUsageList);

        for(StockUsageRequest.OfIncreasing ofIncreasing : increaseStockUsageList.getOfIncreasingList()) {
            AvailableProductInventoryResponse response = stockSystemService.processProductInventory(ofIncreasing);
            responseList.stream()
                    .filter(ofOrderInformation -> ofOrderInformation.getProductId().equals(response.getProductId()))
                    .forEach(ofOrderInformation -> ofOrderInformation.changeIsAvailable(response.getIsAvailableOrder()));
        }

        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "상품재고 등록", description = "고객이 상품재고를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 재고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/inventory", method = "POST")
    @PostMapping
    public ResponseEntity<ApiResponse> registerProductInventory(@Valid  @RequestBody ProductInventoryManagementRequest.RegisterDto registerDto) {
        productInventoryManagementService.registerProductInventory(registerDto);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품재고리스트 조회", description = "고객이 등록한 상품재고리스트을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 재고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/inventory/{productId}", method = "GET")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> retrieveProductInventories(@PathVariable Long productId,
                                                                  @RequestParam(defaultValue = "0") int startPage,
                                                                  @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(startPage, pageSize);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(productInventoryManagementService.retrieveProductInventorySummary(productId, pageable))
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }


    @Operation(summary = "상품재고수량 변경", description = "고객이 등록한 상품재고수량을 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 재고 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/inventory/{productInventoryId}", method = "PUT")
    @PutMapping("/{productInventoryId}")
    public ResponseEntity<ApiResponse> modifyProductInventory(@PathVariable Long productInventoryId,
                                                              @Valid @RequestBody ProductInventoryManagementRequest.ModifyDto modifyDto) {
        productInventoryManagementService.modifyProductInventory(productInventoryId, modifyDto);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.UPDATE_SUCCESS)
                .build(), HttpStatus.OK);
    }


    //고객의 상품 재고 summary 조회(상품별 재고수량 합계 조회)
    @Operation(summary = "상품재고 summary 조회", description = "고객이 등록한 상품재고 summary를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 재고 summary 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/inventory/summary", method = "GET")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> retrieveProductInventorySummary(@RequestParam Long customerId,
                                                                       @RequestParam(defaultValue = "0") int startPage,
                                                                       @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(startPage, pageSize);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(productInventoryManagementService.retrieveProductInventorySummary(customerId, pageable))
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }


    @Builder
    @Getter
    public static class AvailableProductInventoryResponseList {
        List<AvailableProductInventoryResponse> availableProductInventoryResponseList;
    }
}
