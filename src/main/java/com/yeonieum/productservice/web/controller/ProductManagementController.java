package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.S3Upload.S3UploadService;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.domain.product.service.customerservice.ProductManagementService;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
public class ProductManagementController {
    private final ProductManagementService productManagementService;
    S3UploadService s3UploadService;
    /**
     * 일반상품 등록(이미지등록) ok, 수정 ok, 조회 ok, 삭제 ok
     * 친환경상품 등록 ok , 수정 ok, 조회 ok, 삭제 ok (친환경이미지 등록)
     * 이미 등록된 상품의 상세이미지 수정, 재등록, 삭제 ok
     * 광고등록 조회 수정
     * 재고등록, 수정, 조회
     * 타임세일 조회 수정
     * 친환경인증서번호 인증, 조회 api
     */
    @Operation(summary = "일반상품등록", description = "고객(seller)의 일반상품을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/product/normal")
    public ResponseEntity<ApiResponse> createNormalProduct(ProductManagementRequest.RegisterDto normalProduct,
                                                           @RequestPart MultipartFile defaultImage) throws IOException {
        String imageUrl = s3UploadService.uploadImage(defaultImage);
        productManagementService.registerProduct(normalProduct, imageUrl);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.CREATED);
    }

    @Operation(summary = "친환경상품등록", description = "고객(seller)의 찬환경상품을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "친환경상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/product/eco-friend")
    public ResponseEntity<ApiResponse> createEcoFriendlyProduct(ProductManagementRequest.RegisterDto ecoFriendlyProduct,
                                                                @RequestPart MultipartFile defaultImage,
                                                                @RequestPart MultipartFile certificationImage) throws IOException {
        String defaultImageUrl = s3UploadService.uploadImage(defaultImage);
        String certificationImageUrl = s3UploadService.uploadImage(certificationImage);

        try {
            ((ProductManagementRequest.RegisterEcoFriendlyProductDto)(ecoFriendlyProduct)).getCertification().changeImageName(certificationImageUrl);
            productManagementService.registerProduct((ProductManagementRequest.RegisterEcoFriendlyProductDto) ecoFriendlyProduct, defaultImageUrl);
        } catch (RuntimeException e) {
            s3UploadService.deleteImageFromS3(defaultImageUrl);
            s3UploadService.deleteImageFromS3(certificationImageUrl);
            throw new RuntimeException();
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.CREATED);
    }

    @Operation(summary = "상품수정", description = "고객(seller)의 일반상품을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PutMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> updateProductInformation(@PathVariable Long productId,
                                                                ProductManagementRequest.ModifyDto productInformation) {
        Long customerId = 1L;
        productManagementService.modifyProduct(productId, customerId, productInformation);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "고객의 상품리스트 조회", description = "고객(seller)의 상품을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/product/list")
    // 친환경상품, 일반상품 조회 따로 파야한다.
    public ResponseEntity<ApiResponse> getCustomersAllProduct(@RequestParam char isEcoFriend,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        // 고객 id 시큐리티컨텍스트 조회 할 것이다. // 페이지네이션 받기
        Long customerId = 1L;
        List<ProductManagementResponse.RetrieveDto> productList = productManagementService.retrieveCustomersProducts(customerId, ActiveStatus.fromCode(isEcoFriend));

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품상세조회", description = "고객(seller)의 상품 상세를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/product/details/{productId}")
    public ResponseEntity<ApiResponse> getProductDetail(@PathVariable Long productId) {
        ProductManagementResponse.RetrieveDto productDetail = productManagementService.retrieveProductDetail(productId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(productDetail)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품삭제", description = "고객(seller)의 상품을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        Long customerId = 1L;
        productManagementService.deleteProduct(productId, customerId);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.DELETE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 상세이미지 등록", description = "고객(seller)의 상품 상세이미지를 업로드합니다..")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/product/{productId}/default-image")
    public ResponseEntity<ApiResponse> uploadDefaultImage(@PathVariable Long productId,
                                                          @RequestPart  MultipartFile defaultImage) throws IOException {
        String imageUrl = s3UploadService.uploadImage(defaultImage);
        try {
            productManagementService.uploadProductImageUrl(productId, imageUrl);
        } catch (RuntimeException e) {
            s3UploadService.deleteImageFromS3(imageUrl);
            throw new RuntimeException();
        }


        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @PostMapping("/product/{productId}/detail-image")
    public ResponseEntity<ApiResponse> uploadProductDetailImage(@PathVariable Long productId,
                                                                List<Long> deleteDetailImageList,
                                                                @RequestPart  List<MultipartFile> uploadImageList) throws IOException {
        List<String> imageUrlList = new ArrayList<>();
        for(MultipartFile uploadFile : uploadImageList) {
            String imageUrl = s3UploadService.uploadImage(uploadFile);
            imageUrlList.add(imageUrl);
        }

        productManagementService.uploadProductDetailImages(productId, deleteDetailImageList, imageUrlList);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.CREATED);
    }
}
