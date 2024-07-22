package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.S3Upload.S3UploadService;
import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import com.yeonieum.productservice.domain.product.entity.SaleType;
import com.yeonieum.productservice.domain.product.repository.ProductCertificationRepository;
import com.yeonieum.productservice.domain.product.repository.ProductDetailImageRepository;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.SaleTypeRepository;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductMangementServiceImpl implements ProductManagementService{
    private final ProductRepository productRepository;
    private final ProductCertificationRepository productCertificationRepository;
    private final CustomerRepository customerRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final SaleTypeRepository saleTypeRepository;
    private final ProductDetailImageRepository productDetailImageRepository;
    private final S3UploadService s3UploadService;

    /**
     * 친환경 인증서 시리얼넘버 유효성 검증 메서드
     * @param serialNumber
     * @return
     */
    @Override
    public boolean checkCertificationValidation(Long serialNumber) {
        return serialNumber % 2 ==0;
    }

    /**
     * 고객의 상품 등록(일반상품, 친환경상품 공통 메서드)
     *
     * @param registerRequest
     * @exception
     * @throws
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerProduct(ProductManagementRequest.OfRegister registerRequest, MultipartFile defaultImage, List<MultipartFile> detailImageList) throws RuntimeException {
        try {
            Customer customer = customerRepository.findById(registerRequest.getCustomerId()).orElseThrow(
                            () -> new IllegalArgumentException("존재하지않는 고객 요청입니다."));

            ProductCategory productCategory = productCategoryRepository.findById(registerRequest.getMainCategoryId()).orElseThrow(
                            () -> new IllegalArgumentException("존재하지않는 대분류 카테고리입니다."));

            // []
            List<ProductDetailCategory> productDetailCategoryList = productCategory.getProductDetailCategoryList();
            ProductDetailCategory productDetailCategory = productDetailCategoryList.stream()
                    .filter(detailCategory -> detailCategory.getProductDetailCategoryId() == registerRequest.getSubCategoryId())
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("잘못된 소분류 카테고리 선택입니다."));


            String defaultImageUrl = s3UploadService.uploadImage(defaultImage);
            List<String> detailImageUrlList = detailImageList.stream()
                    .map(image -> {
                        try {
                            return s3UploadService.uploadImage(image);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            // [상품생성]
            SaleType saleType = saleTypeRepository.findByTypeName("일반판매"); // 일반판매타입, 타임세일타입
            Product product = registerRequest.toEntity(saleType, customer, productDetailCategory, defaultImageUrl);

            // [상품상세이미지 생성]
            List<ProductDetailImage> productDetailImageList = detailImageUrlList.stream().map(imageUrl ->
                    ProductDetailImage.builder()
                            .detailImage(imageUrl)
                            .product(product)
                            .build()).collect(Collectors.toList());
            productDetailImageRepository.saveAll(productDetailImageList);

            // 친환경상품이면 [상품의 인증서 여부 T로 변경, 인증서 등록]
            if(registerRequest instanceof ProductManagementRequest.OfRegisterEcoFriendlyProduct) {
                product.setIsCertification(ActiveStatus.ACTIVE);

                ProductManagementRequest.Certification certification
                        = ((ProductManagementRequest.OfRegisterEcoFriendlyProduct) registerRequest).getCertification();

                String certificationImageUrl = s3UploadService.uploadImage(certification.getImage());
                productCertificationRepository.save(certification.toEntity(product, certificationImageUrl));
            }
            productRepository.save(product);
        }catch (Exception e) {
            e.printStackTrace();
            // S3upload 롤백 시도하는 예외 던지기[런타임 예외 던지기]
            // RestControllerAdvice에서 처리하기
            throw new RuntimeException();
        }
    }


    /**
     * 고객의 상품 삭제
     *
     * @param productId
     * @exception
     * @throws
     * @return
     */
    @Override
    public void deleteProduct(Long productId, Long customerId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));
        // delete 매핑이라 customerId가 product의 소유자인지 검증
        if(product.getCustomer().getCustomerId() != customerId) {
            throw new IllegalArgumentException("해당 상품의 소유자가 아닙니다.");
        }
        productRepository.deleteById(productId);
    }

    /**
     * 고객의 상품 내용 수정
     *
     * @param productId
     * @exception
     * @throws
     * @return
     */
    @Override
    @Transactional
    public void modifyProduct(Long productId,  Long customerId, ProductManagementRequest.OfModify ofModify) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));
        // put매핑이라 customerId가 product의 소유자인지 검증
        if(product.getCustomer().getCustomerId() != customerId) {
            throw new IllegalArgumentException("해당 상품의 소유자가 아닙니다.");
        }

        product.changeProductName(ofModify.getProductName());
        product.changeProductDescription(ofModify.getDescription());
        product.changeProductOrigin(ofModify.getOrigin());
        product.changeProductPrice(ofModify.getPrice());
        product.changeBaseDiscountRate(ofModify.getBaseDiscountRate());
        product.changePersonalizedDiscountRate(ofModify.getPersonalizedDiscountRate());
        product.changeRegularDiscountRate(ofModify.getRegularDiscountRate());
        product.changeIsPageVisibility(ActiveStatus.fromCode(ofModify.getIsPageVisibility()));
        product.changeIsRegularSale(ActiveStatus.fromCode(ofModify.getIsRegularSale()));
    }

    /**
     * 고객이 등록한 상품리스트 조회  -> 친환경 여부를 넣어야한다.
     * @param customerId
     * @return
     */
    @Override
    public Page<ProductManagementResponse.OfRetrieve> retrieveCustomersProducts(Long customerId, ActiveStatus isEcoFriend, Pageable pageable) {
        Page<Product> productListOfCustomer = productRepository.findProductsWithCustomersByStatus(customerId, isEcoFriend.getCode(), pageable);
        return productListOfCustomer.map(productPage ->  ProductManagementResponse.OfRetrieve.convertedBy(productPage));
    }

    /**
     * 고객의 상품 상세 조회
     * @param productId
     * @return
     */
    @Override
    public ProductManagementResponse.OfRetrieveDetails retrieveProductDetail(Long productId) {
        Product product = productRepository.findProductWithCategoryInfoByProductId(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));
        return ProductManagementResponse.OfRetrieveDetails.convertedBy(product);
    }

    /**
     * 기존 등록된 상품의 기본이미지 url변경
     * @param productId
     * @param imageUrl
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadProductImageUrl(Long productId, String imageUrl) {
        try {
            Product product = productRepository.findById(productId).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));

            product.changeProductImage(imageUrl);
            productRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 상품상세이미지등록
     * @param productId
     * @param
     * @return
     */
    @Override
    @Transactional
    public void uploadProductDetailImages(Long productId,
                                             ProductManagementRequest.OfDeleteDetailImageList deleteList,
                                             List<String> imageUrlList) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));

        for(Long detailImageIdList : deleteList.getDetailImageList()) {
            productDetailImageRepository.deleteById(detailImageIdList);
        }

        List<ProductDetailImage> productDetailImageList = imageUrlList.stream().map(imageUrl ->
                ProductDetailImage.builder()
                        .detailImage(imageUrl)
                        .product(product)
                        .build()).collect(Collectors.toList());

        productDetailImageRepository.saveAll(productDetailImageList);
    }

    @Override
    public ProductManagementResponse.OfRetrieveProductOrder retrieveProductInformation(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        return ProductManagementResponse.OfRetrieveProductOrder.convertedBy(product);
    }

    @Override
    public Map<Long, ProductManagementResponse.OfRetrieveProductOrder> bulkRetrieveProductInformation(List<Long> productIdList) {
        List<Product> productList = productRepository.findAllById(productIdList);
        return productList.stream().collect(Collectors.toMap(
                Product::getProductId, // key mapper - 상품의 ID를 키로 사용
                ProductManagementResponse.OfRetrieveProductOrder::convertedBy // value mapper - 변환된 값을 값으로 사용
        ));
    }

    @Override
    public List<ProductManagementResponse.OfRetrieveDetailImage> retrieveDetailImage(Long productId) {
        List<ProductDetailImage> productDetailImageList = productDetailImageRepository.findByProductId(productId);

        return productDetailImageList.stream()
                .map(detailImage -> ProductManagementResponse.OfRetrieveDetailImage.convertedBy(detailImage, productId))
                .collect(Collectors.toList());
    }


    /**
     * 인증서 이미지 등록
     * @param productId
     * @param imageUrl
     * @param certification
     * @return
     */
    @Override
    public void uploadCertificationImage(Long productId, String imageUrl, ProductManagementRequest.Certification certification) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다."));

        ProductCertification productCertification = productCertificationRepository.findByProductId(productId);
        // 이미 등록된 경우 덮어 쓰기 || 등록된 인증서가 없는경우 생성
        if(productCertification != null) {
            productCertification.changeCertificationImage(imageUrl);
        } else {
            String certificationImageUrl = s3UploadService.uploadImage(certification.getImage());
            productCertification = certification.toEntity(product, certificationImageUrl);
        }
        productCertificationRepository.save(productCertification);
    }

}
