package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.global.auth.RoleMetaDataCollector;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
// 롤메타데이터수집기의 대상에서 제외되기 위해 @Controller 어노테이션
@Controller
@RequiredArgsConstructor
public class PermissionController {
    private final RoleMetaDataCollector roleMetaDataCollector;

    @GetMapping("/api/permissions")
    public @ResponseBody ResponseEntity<?> retrieveProductReviews(Long productId) {
        return new ResponseEntity<>(roleMetaDataCollector.getRoleMetadata(), HttpStatus.OK);
    }
}
