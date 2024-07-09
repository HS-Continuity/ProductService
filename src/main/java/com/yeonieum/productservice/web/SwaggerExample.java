package com.yeonieum.productservice.web;

import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public class SwaggerExample {

    @Operation(summary = "회원가입", description = "회원가입 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/join")
    public ResponseEntity<ApiResponse> join() {
        //회원가입 로직
        return new ResponseEntity<>(ApiResponse.builder()
                .result("응답DTO를 담으세요.") // 응답DTO 구성
                .successCode(SuccessCode.INSERT_SUCCESS) // SuccessCode Enum타입 담기
                .build(), HttpStatus.CREATED); // HttpStatus 상태코드 담기
    }


}
