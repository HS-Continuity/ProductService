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

@FeignClient(name = "orderservice", configuration = FeignConfig.class)
public interface OrderServiceFeignClient {

    @GetMapping("/orderservice/api/order/ranking/statistics")
    ResponseEntity<ApiResponse<List<ProductManagementResponse.ProductOrderCount>>> getAllProductsByCondition(@RequestParam Long customerId,
                                                                                                             @RequestParam(required = false) Gender gender,
                                                                                                             @RequestParam(required = false) Integer ageRange,
                                                                                                             @RequestParam(required = false) OrderType orderType,
                                                                                                             @RequestParam(required = false) Integer month);

}
