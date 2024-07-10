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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
     * @param registerRequestDto
     * @exception
     * @throws
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerProduct(ProductManagementRequest.RegisterDto registerRequestDto, String imageUrl) throws RuntimeException {
        try {
            Customer customer =
                    customerRepository.findById(registerRequestDto.getCustomerId()).orElseThrow(
                            () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                    );

            ProductCategory productCategory =
                    productCategoryRepository.findById(registerRequestDto.getMainCategoryId()).orElseThrow(
                            () -> new IllegalArgumentException("존재하지않는 대분류 카테고리입니다.")
                    );

            // []
            List<ProductDetailCategory> productDetailCategoryList = productCategory.getProductDetailCategoryList();
            ProductDetailCategory productDetailCategory = productDetailCategoryList.stream()
                    .filter(detailCategory -> detailCategory.getProductDetailCategoryId() == registerRequestDto.getSubCategoryId())
                    .findFirst().orElseThrow(
                            () -> new IllegalArgumentException("잘못된 소분류 카테고리 선택입니다.")
                    );

            SaleType saleType = saleTypeRepository.findById(1L).orElseThrow();

            // [상품생성]
            Product product = Product.builder()
                    .saleType(saleType)
                    .customer(customer)
                    .productName(registerRequestDto.getProductName())
                    .productPrice(registerRequestDto.getPrice())
                    .baseDiscountRate(registerRequestDto.getBaseDiscountRate())
                    .regularDiscountRate(registerRequestDto.getRegularDiscountRate())
                    .personalizedDiscountRate(registerRequestDto.getPersonalizedDiscountRate())
                    .productDescription(registerRequestDto.getDescription())
                    .productOrigin(registerRequestDto.getOrigin())
                    .productImage(imageUrl) // 이미지 파일명은 고객아이디+상품명
                    .isPageVisibility(ActiveStatus.fromCode(registerRequestDto.getIsPageVisibility()))
                    .isRegularSale(ActiveStatus.fromCode(registerRequestDto.getIsRegularSale()))
                    .productDetailCategory(productDetailCategory)
                    .build();

            // 친환경상품이면 [상품의 인증서 여부 T로 변경, 인증서 등록]
            if(registerRequestDto instanceof ProductManagementRequest.RegisterEcoFriendlyProductDto) {
                product.setIsCertification(ActiveStatus.ACTIVE);
                productRepository.save(product);

                ProductManagementRequest.Certification certification
                        = ((ProductManagementRequest.RegisterEcoFriendlyProductDto) registerRequestDto).getCertification();

                ProductCertification productCertification = ProductCertification.builder()
                        .product(product)
                        .certificationImage(certification.getImageName())
                        .certificationName(certification.getName())
                        .certificationNumber(certification.getSerialNumber())
                        .build();

                productCertificationRepository.save(productCertification);
            }

            productRepository.save(product);
            return true;
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
    public boolean deleteProduct(Long productId, Long customerId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        // delete 매핑이라 customerId가 product의 소유자인지 검증
        if(product.getCustomer().getCustomerId() != customerId) {
            throw new IllegalArgumentException("해당 상품의 소유자가 아닙니다.");
        }

        productRepository.deleteById(productId);
        return false;
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
    public boolean modifyProduct(Long productId,  Long customerId, ProductManagementRequest.ModifyDto modifyDto) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );
        // put매핑이라 customerId가 product의 소유자인지 검증
        if(product.getCustomer().getCustomerId() != customerId) {
            throw new IllegalArgumentException("해당 상품의 소유자가 아닙니다.");
        }

        product.changeProductName(modifyDto.getProductName());
        product.changeProductDescription(modifyDto.getDescription());
        product.changeProductOrigin(modifyDto.getOrigin());
        product.changeProductPrice(modifyDto.getPrice());
        product.changeBaseDiscountRate(modifyDto.getBaseDiscountRate());
        product.changePersonalizedDiscountRate(modifyDto.getPersonalizedDiscountRate());
        product.changeRegularDiscountRate(modifyDto.getRegularDiscountRate());
        product.changeIsPageVisibility(ActiveStatus.fromCode(modifyDto.getIsPageVisibility()));
        product.changeIsRegularSale(ActiveStatus.fromCode(modifyDto.getIsRegularSale()));
        return true;
    }

    /**
     * 고객이 등록한 상품리스트 조회
     * @param customerId
     * @return
     */
    @Override
    public List<ProductManagementResponse.RetrieveDto> retrieveCustomersProducts(Long customerId, ActiveStatus isEcoFriend) {
        Customer customer =
                customerRepository.findCustomersWithProductsByStatus(customerId, isEcoFriend.getCode()).orElseThrow(
                        () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                );

        List<Product> productListOfCustomer = customer.getProductList();

        // Entity -> DTO변환
        List<ProductManagementResponse.RetrieveDto> productList =
                productListOfCustomer.stream().map(product -> {

                    ProductDetailCategory productDetailCategory = product.getProductDetailCategory();
                    ProductCategory productCategory = productDetailCategory.getProductCategory();

                    return ProductManagementResponse.RetrieveDto.builder()
                            .mainCategoryName(productCategory.getCategoryName())
                            .subCategoryName(productCategory.getCategoryName())
                            .salesType(product.getSaleType().getTypeName())
                            .productName(product.getProductName())
                            .description(product.getProductDescription())
                            .price(product.getProductPrice())
                            .origin(product.getProductOrigin())
                            .personalizedDiscountRate(product.getPersonalizedDiscountRate())
                            .baseDiscountRate(product.getBaseDiscountRate())
                            .regularDiscountRate(product.getRegularDiscountRate())
                            .isPageVisibility(product.getIsPageVisibility().getCode())
                            .isRegularSale(product.getIsRegularSale().getCode())
                            .build();
                        }
                    ).collect(Collectors.toList());
        return productList;
    }

    /**
     * 고객의 상품 상세 조회
     * @param productId
     * @return
     */
    @Override
    public ProductManagementResponse.RetrieveDto retrieveProductDetail(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );
        ProductDetailCategory productDetailCategory = product.getProductDetailCategory();
        ProductCategory productCategory = productDetailCategory.getProductCategory();

        ProductManagementResponse.RetrieveDto productDto = ProductManagementResponse.RetrieveDto.builder()
                .mainCategoryName(productCategory.getCategoryName())
                .subCategoryName(productCategory.getCategoryName())
                .salesType(product.getSaleType().getTypeName())
                .productName(product.getProductName())
                .description(product.getProductDescription())
                .price(product.getProductPrice())
                .origin(product.getProductOrigin())
                .personalizedDiscountRate(product.getPersonalizedDiscountRate())
                .baseDiscountRate(product.getBaseDiscountRate())
                .regularDiscountRate(product.getRegularDiscountRate())
                .isPageVisibility(product.getIsPageVisibility().getCode())
                .isRegularSale(product.getIsRegularSale().getCode())
                .build();

        return productDto;
    }

    /**
     * 기존 등록된 상품의 기본이미지 url변경
     * @param productId
     * @param imageUrl
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uploadProductImageUrl(Long productId, String imageUrl) {
        try {
            Product product = productRepository.findById(productId).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
            );

            String defaultImageUrl = product.getProductImage();
            product.changeProductImage(imageUrl);
            productRepository.save(product);
            return true;
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
    public boolean uploadProductDetailImages(Long productId,
                                             List<Long> deleteList,
                                             List<String> imageUrlList) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        for(Long detailImageIdList : deleteList) {
            ProductDetailImage productDetailImage = productDetailImageRepository.findById(detailImageIdList).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 상세이미지가 존재하지 않습니다.")
            );

            productDetailImageRepository.deleteById(detailImageIdList);
        }

        for(String imageUrl : imageUrlList) {
            ProductDetailImage productDetailImage = ProductDetailImage.builder()
                    .imageDetailName(imageUrl)
                    .product(product)
                    .build();

            productDetailImageRepository.save(productDetailImage);
        }

        return true;
    }

    @Override
    public List<ProductManagementResponse.RetrieveDetailImage> retrieveDetailImage(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        List<ProductDetailImage> productDetailImageList = productDetailImageRepository.findByProductId(productId);

        List<ProductManagementResponse.RetrieveDetailImage> detailImageList =
                productDetailImageList.stream().map(
                        productDetailImage -> {
                            return ProductManagementResponse.RetrieveDetailImage.builder()
                                    .imageName(productDetailImage.getImageDetailName())
                                    .productId(productId)
                                    .productDetailImageId(productDetailImage.getProductDetailImageId())
                                    .build();
                        }
                ).collect(Collectors.toList());

        return detailImageList;
    }


    /**
     * 인증서 이미지 등록
     * @param productId
     * @param imageUrl
     * @param certification
     * @return
     */
    @Override
    public boolean uploadCertificationImage(Long productId, String imageUrl, ProductManagementRequest.Certification certification) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        ProductCertification productCertification = productCertificationRepository.findByProductId(productId);
        if(productCertification != null) {
            // 이미 등록된 경우 덮어 쓰기
            productCertification.changeCertificationImage(imageUrl);
        } else {
            // 등록된 인증서가 없는경우 생성
            productCertification = ProductCertification.builder()
                    .product(product)
                    .certificationName(certification.getName())
                    .certificationNumber(certification.getSerialNumber())
                    .certificationImage(imageUrl)
                    .build();
        }

        productCertificationRepository.save(productCertification);
        return true;
    }

    @Transactional
    public void registerCertificationOf(Product product, ProductManagementRequest.RegisterDto registerRequestDto) {
        ProductManagementRequest.Certification certification
                = ((ProductManagementRequest.RegisterEcoFriendlyProductDto) registerRequestDto).getCertification();

        ProductCertification productCertification = ProductCertification.builder()
                .product(product)
                .certificationImage(certification.getImageName())
                .certificationName(certification.getName())
                .certificationNumber(certification.getSerialNumber())
                .build();

        productCertificationRepository.save(productCertification);
    }
}
