package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.domain.product.service.customerservice.ProductManagementService;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import com.yeonieum.productservice.global.auth.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    @Role(role = {"ROLE_ADMIN", "ROLE_MEMBER"}, url = "/api/inventory/stock-usage", method = "POST")
    @PostMapping("/stock-usage")
    public AvailableProductInventoryResponseList getAvailableOrderProduct(@RequestBody StockUsageRequest.IncreaseStockUsageList increaseStockUsageListDtoList) {
        List<AvailableProductInventoryResponse> responseList = new ArrayList<>();
        for(StockUsageRequest.OfIncreasing ofIncreasing : increaseStockUsageListDtoList.getOfIncreasingList()) {
            AvailableProductInventoryResponse response = stockSystemService.processProductInventory(ofIncreasing);
            responseList.add(response);
        }
        return AvailableProductInventoryResponseList.builder()
                .availableProductInventoryResponseList(responseList)
                .build();
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


    @Builder
    @Getter
    public static class AvailableProductInventoryResponseList {
        List<AvailableProductInventoryResponse> availableProductInventoryResponseList;
    }
}
