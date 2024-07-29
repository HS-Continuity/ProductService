package com.yeonieum.productservice.domain.product.service.customerservice;

import com.yeonieum.productservice.domain.S3Upload.S3UploadService;
import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.exception.CategoryException;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.exception.CustomerException;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.customerservice.ProductManagementResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.ProductDetailImage;
import com.yeonieum.productservice.domain.product.entity.SaleType;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductCertificationRepository;
import com.yeonieum.productservice.domain.product.repository.ProductDetailImageRepository;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.repository.SaleTypeRepository;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.global.enums.ActiveStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yeonieum.productservice.domain.category.exception.CategoryExceptionCode.CATEGORY_NOT_FOUND;
import static com.yeonieum.productservice.domain.category.exception.CategoryExceptionCode.DETAIL_CATEGORY_NOT_FOUND;
import static com.yeonieum.productservice.domain.customer.exception.CustomerExceptionCode.CUSTOMER_NOT_FOUND;
import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.NOT_PRODUCT_OWNER;
import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.PRODUCT_NOT_FOUND;

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
                            () -> new CustomerException(CUSTOMER_NOT_FOUND, HttpStatus.NOT_FOUND));

            ProductCategory productCategory = productCategoryRepository.findById(registerRequest.getMainCategoryId()).orElseThrow(
                            () -> new CategoryException(CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));

            // []
            List<ProductDetailCategory> productDetailCategoryList = productCategory.getProductDetailCategoryList();
            ProductDetailCategory productDetailCategory = productDetailCategoryList.stream()
                    .filter(detailCategory -> detailCategory.getProductDetailCategoryId() == registerRequest.getSubCategoryId())
                    .findFirst().orElseThrow(() -> new CategoryException(DETAIL_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));


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
                () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
        // delete 매핑이라 customerId가 product의 소유자인지 검증
        if(product.getCustomer().getCustomerId() != customerId) {
            throw new ProductException(NOT_PRODUCT_OWNER, HttpStatus.FORBIDDEN);
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
                () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
        // put매핑이라 customerId가 product의 소유자인지 검증
        if(product.getCustomer().getCustomerId() != customerId) {
            throw new ProductException(NOT_PRODUCT_OWNER, HttpStatus.FORBIDDEN);
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
    @Transactional(readOnly = true)
    public Page<ProductManagementResponse.OfRetrieve> retrieveCustomersProducts(Long customerId, ActiveStatus isEcoFriend, String productName, String description, String origin, Integer price, ActiveStatus isPageVisibility, ActiveStatus isRegularSale, Integer baseDiscountRate, Integer regularDiscountRate, Pageable pageable) {
        Page<Product> productListOfCustomer = productRepository.findProductsByCriteria(customerId, isEcoFriend, productName, description, origin, price, isPageVisibility, isRegularSale, baseDiscountRate, regularDiscountRate, pageable);
        return productListOfCustomer.map(ProductManagementResponse.OfRetrieve::convertedBy);
    }

    /**
     * 고객의 상품 상세 조회
     * @param productId
     * @return
     */
    @Override
    public ProductManagementResponse.OfRetrieveDetails retrieveProductDetail(Long productId) {
        Product product = productRepository.findProductWithCategoryInfoByProductId(productId).orElseThrow(
                () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
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
                    () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));

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
                () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));

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
    @Transactional
    public ProductManagementResponse.OfRetrieveProductOrder retrieveProductInformation(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));
        return ProductManagementResponse.OfRetrieveProductOrder.convertedBy(product);
    }




    @Override
    @Transactional(readOnly = true)
    public Map<Long, ProductManagementResponse.OfRetrieveProductOrder> bulkRetrieveProductInformation(List<Long> productIdList) {
        List<Product> productList = productRepository.findAllById(productIdList);
        return productList.stream().collect(Collectors.toMap(
                Product::getProductId, // key mapper - 상품의 ID를 키로 사용
                ProductManagementResponse.OfRetrieveProductOrder::convertedBy // value mapper - 변환된 값을 값으로 사용
        ));
    }

    @Override
    @Transactional
    public List<ProductManagementResponse.OfOrderInformation> retrieveOrderInformation(StockUsageRequest.IncreaseStockUsageList increaseStockUsageList) {
        List<Product> productList = productRepository.findAllById(increaseStockUsageList.getOfIncreasingList().stream().map(
                StockUsageRequest.OfIncreasing::getProductId).collect(Collectors.toList())
        );

        Map<Long, Integer> productQuantityMap = increaseStockUsageList.getOfIncreasingList().stream().collect(Collectors.toMap(
                StockUsageRequest.OfIncreasing::getProductId,
                StockUsageRequest.OfIncreasing::getQuantity
        ));

        return productList.stream().map(product -> ProductManagementResponse.OfOrderInformation.convertedBy(product, productQuantityMap.get(product.getProductId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public void uploadCertificationImage(Long productId, String imageUrl, ProductManagementRequest.Certification certification) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND));

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
