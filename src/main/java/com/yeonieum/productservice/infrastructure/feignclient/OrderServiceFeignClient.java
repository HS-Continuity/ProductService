package com.yeonieum.productservice.infrastructure.feignclient;

import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.global.config.FeignConfig;
import com.yeonieum.productservice.global.enums.Gender;
import com.yeonieum.productservice.global.enums.OrderType;
import com.yeonieum.productservice.global.responses.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "orderservice", url = "http://localhost:8040", configuration = FeignConfig.class)
public interface OrderServiceFeignClient {

    @GetMapping("/orderservice/api/order/ranking/gender")
    ResponseEntity<ApiResponse<List<ProductManagementResponse.ProductOrderCount>>> getOrderGenderTop3(@RequestParam Long customerId, @RequestParam Gender gender);

    @GetMapping("/orderservice/api/order/ranking/age-range")
    ResponseEntity<ApiResponse<List<ProductManagementResponse.ProductOrderCount>>> getOrderAgeRangeTop3(@RequestParam Long customerId, @RequestParam int ageRange);

    @GetMapping("/orderservice/api/order/ranking/order-type")
    ResponseEntity<ApiResponse<List<ProductManagementResponse.ProductOrderCount>>> getAllProductsByOrderType(@RequestParam Long customerId, @RequestParam OrderType orderType);
}
