package com.yeonieum.productservice.web.controller;

import com.yeonieum.productservice.domain.S3Upload.S3UploadService;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.domain.product.service.customerservice.ProductManagementService;
import com.yeonieum.productservice.global.auth.Role;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import com.yeonieum.productservice.global.enums.Gender;
import com.yeonieum.productservice.global.enums.OrderType;
import com.yeonieum.productservice.global.responses.ApiResponse;
import com.yeonieum.productservice.global.responses.code.SuccessCode;
import com.yeonieum.productservice.global.usercontext.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final S3UploadService s3UploadService;
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
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/management/product/normal", method = "POST")
    @PostMapping("/product/normal")

    public ResponseEntity<ApiResponse> createNormalProduct(@Valid @RequestPart(value = "normalProduct") ProductManagementRequest.OfRegisterNormalProduct normalProduct,
                                                           @RequestPart(value = "image") MultipartFile defaultImage,
                                                           @RequestPart(value = "detailImageList") List<MultipartFile> detailImageList) throws IOException {
        String imageUrl = s3UploadService.uploadImage(defaultImage);
        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        productManagementService.registerProduct(customer, normalProduct, defaultImage, detailImageList);

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
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/management/product/eco-friend", method = "POST")
    @PostMapping("/product/eco-friend")
    public ResponseEntity<ApiResponse> createEcoFriendlyProduct(@Valid @RequestPart(value = "product") ProductManagementRequest.OfRegisterEcoFriendlyProduct ecoFriendlyProduct,
                                                                @RequestPart(value = "defaultImage") MultipartFile defaultImage,
                                                                @RequestPart(value = "certificationImage") MultipartFile certificationImage,
                                                                @RequestPart(value = "detailImageList") List<MultipartFile> detailImageList) throws IOException {

        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        ((ProductManagementRequest.OfRegisterEcoFriendlyProduct)(ecoFriendlyProduct)).getCertification().setImage(certificationImage);
        productManagementService.registerProduct(customer, (ProductManagementRequest.OfRegisterEcoFriendlyProduct) ecoFriendlyProduct, defaultImage, detailImageList);

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
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/management/product/{productId}", method = "PUT")
    @PutMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> updateProductInformation(@PathVariable Long productId,
                                                                @Valid @RequestBody ProductManagementRequest.OfModify productInformation) {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        productManagementService.modifyProduct(productId, customer, productInformation);

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
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/management/product/list", method = "GET")
    @GetMapping("/product/list")
    public ResponseEntity<ApiResponse> getCustomersAllProduct(@RequestParam(required = false) ActiveStatus isEcoFriend,
                                                              @RequestParam(required = false) Long productId,
                                                              @RequestParam(required = false) String productName,
                                                              @RequestParam(required = false) String detailCategoryName,
                                                              @RequestParam(required = false) String origin,
                                                              @RequestParam(required = false) Integer price,
                                                              @RequestParam(required = false) ActiveStatus isPageVisibility,
                                                              @RequestParam(required = false) ActiveStatus isRegularSale,
                                                              @RequestParam(required = false) Integer baseDiscountRate,
                                                              @RequestParam(required = false) Integer regularDiscountRate,
                                                              @RequestParam(defaultValue = "1") int startPage,
                                                              @RequestParam(defaultValue = "10") int pageSize) {
        //Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        Pageable pageable = PageRequest.of(startPage, pageSize);
        Page<ProductManagementResponse.OfRetrieve> productList =
                productManagementService.retrieveCustomersProducts(1L, isEcoFriend, productId, productName, detailCategoryName, origin, price, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate, pageable);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(productList)
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품상세조회", description = "고객(seller)의 상품 상세를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/management/product/{productId}/details", method = "GET")
    @GetMapping("/product/{productId}/details")
    public ResponseEntity<ApiResponse> getProductDetail(@PathVariable Long productId) {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        ProductManagementResponse.OfRetrieveDetails productDetail = productManagementService.retrieveProductDetail(customer, productId);

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
    @Role(role = {"ROLE_CUSTOMER"}, url = "/api/management/product/{productId}", method = "DELETE")
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        productManagementService.deleteProduct(productId, customer);

        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.DELETE_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 이미지 등록", description = "고객(seller)의 상품 상세이미지를 업로드합니다..")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일반상품등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/product/{productId}/default-image")
    public ResponseEntity<ApiResponse> uploadDefaultImage(@PathVariable Long productId,
                                                          @RequestPart(value = "image")  MultipartFile defaultImage) throws IOException {
        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        String imageUrl = s3UploadService.uploadImage(defaultImage);
        try {
            productManagementService.uploadProductImageUrl(productId, customer, imageUrl);
        } catch (RuntimeException e) {
            s3UploadService.deleteImageFromS3(imageUrl);
            throw new RuntimeException();
        }


        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "상품 상세이미지 등록", description = "상품 상세이미지 등록 기능입니다.(최대 5개 이미지)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상세이미지 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping("/product/{productId}/detail-image")
    public ResponseEntity<ApiResponse> uploadProductDetailImage(@PathVariable Long productId,
                                                                @RequestPart(value = "deleteList") ProductManagementRequest.OfDeleteDetailImageList deleteOfDeleteDetailImageList,
                                                                @RequestPart(value = "imageList") List<MultipartFile> uploadImageList) throws IOException {

        Long customer = Long.valueOf(UserContextHolder.getContext().getUniqueId());
        List<String> imageUrlList = new ArrayList<>();
        for(MultipartFile uploadFile : uploadImageList) {
            String imageUrl = s3UploadService.uploadImage(uploadFile);
            imageUrlList.add(imageUrl);
        }

        productManagementService.uploadProductDetailImages(productId, customer, deleteOfDeleteDetailImageList, imageUrlList);
        return new ResponseEntity<>(ApiResponse.builder()
                .result(null)
                .successCode(SuccessCode.INSERT_SUCCESS)
                .build(), HttpStatus.CREATED);
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse> bulkRetrieveProductInformation(@RequestParam List<Long> productIdList) {
        return new ResponseEntity<>(ApiResponse.builder()
                .result(productManagementService.bulkRetrieveProductInformation(productIdList))
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }

    @Operation(summary = "고객의 성별 및 연령별 TOP3 상품정보 조회", description = "고객의 성별 및 연령별 TOP3 상품을 조회하는 기능입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "상품 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "상품 정보 조회 실패")
    })
    @GetMapping("/ranking/gender-product")
    public ResponseEntity<ApiResponse> getProductByCondition(@RequestParam Long customerId,
                                                             @RequestParam(required = false) Gender gender,
                                                             @RequestParam(required = false) Integer ageRange,
                                                             @RequestParam(required = false) OrderType orderType) {
        return new ResponseEntity<>(ApiResponse.builder()
                .result(productManagementService.retrieveTopProductsByCondition(customerId, gender, ageRange, orderType))
                .successCode(SuccessCode.SELECT_SUCCESS)
                .build(), HttpStatus.OK);
    }
}
