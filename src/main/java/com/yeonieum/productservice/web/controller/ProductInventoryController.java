package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class ProductInventoryController {
    private final StockSystemService stockSystemService;


    @PostMapping("/stock-usage")
    public AvailableProductInventoryResponseList getAvailableOrderProduct(@RequestBody AvailableProductInventoryRequest.IncreaseStockUsageList increaseStockUsageDtoList) {
        List<AvailableProductInventoryResponse> responseList = new ArrayList<>();
        for(AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto : increaseStockUsageDtoList.getIncreaseStockUsageDtoList()) {
            AvailableProductInventoryResponse response = stockSystemService.processProductInventory(increaseStockUsageDto);
            responseList.add(response);
        }
        return AvailableProductInventoryResponseList.builder()
                .availableProductInventoryResponseList(responseList)
                .build();
    }


    @Builder
    @Getter
    public static class AvailableProductInventoryResponseList {
        List<AvailableProductInventoryResponse> availableProductInventoryResponseList;
    }
}
