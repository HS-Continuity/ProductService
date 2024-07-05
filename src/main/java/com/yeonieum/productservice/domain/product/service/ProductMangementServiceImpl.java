package com.yeonieum.productservice.domain.product.service;

import com.yeonieum.productservice.domain.category.entity.ProductCategory;
import com.yeonieum.productservice.domain.category.entity.ProductDetailCategory;
import com.yeonieum.productservice.domain.category.repository.ProductCategoryRepository;
import com.yeonieum.productservice.domain.customer.entity.Customer;
import com.yeonieum.productservice.domain.customer.repository.CustomerRepository;
import com.yeonieum.productservice.domain.product.dto.ProductManagementRequest;
import com.yeonieum.productservice.domain.product.dto.ProductManagementResponse;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.entity.ProductCertification;
import com.yeonieum.productservice.domain.product.entity.SaleType;
import com.yeonieum.productservice.domain.product.repository.ProductCertificationRepository;
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
    public boolean registerProduct(ProductManagementRequest.RegisterDto registerRequestDto, String imageUrl) {
        try {
            Customer customer =
                    customerRepository.findById(registerRequestDto.getCustomerId()).orElseThrow(
                            () -> new IllegalArgumentException("존재하지않는 고객 요청입니다.")
                    );

            ProductCategory productCategory =
                    productCategoryRepository.findById(registerRequestDto.getMainCategoryId()).orElseThrow(
                            () -> new IllegalArgumentException("존재하지않는 대분류 카테고리입니다.")
                    );

            // productCategory가 가진 하위 카테고리에 아이디가 있는지 체크
            List<ProductDetailCategory> productDetailCategoryList = productCategory.getProductDetailCategoryList();
            ProductDetailCategory productDetailCategory = productDetailCategoryList.stream()
                    .filter(detailCategory -> detailCategory.getProductDetailCategoryId() == registerRequestDto.getSubCategoryId())
                    .findFirst().orElseThrow(
                            () -> new IllegalArgumentException("잘못된 소분류 카테고리 선택입니다.")
                    );

            SaleType saleType = saleTypeRepository.findById(1L).orElseThrow();

            // 고객의 상품 생성 및 등록
            Product product = Product.builder()
                    .saleType(saleType)
                    .customer(customer)
                    .name(registerRequestDto.getProductName())
                    .price(registerRequestDto.getPrice())
                    .baseDiscountRate(registerRequestDto.getBaseDiscountRate())
                    .regularDiscountRate(registerRequestDto.getRegularDiscountRate())
                    .personalizeDiscountRate(registerRequestDto.getPersonalizedDiscountRate())
                    .description(registerRequestDto.getDescription())
                    .origin(registerRequestDto.getOrigin())
                    .image(imageUrl) // 이미지 파일명은 고객아이디+상품명
                    .isPageVisibility(ActiveStatus.fromCode(registerRequestDto.getIsPageVisibility()))
                    .isRegularSale(ActiveStatus.fromCode(registerRequestDto.getIsRegularSale()))
                    .productDetailCategory(productDetailCategory)
                    .build();

            productRepository.save(product);

            // 친환경상품이면 인증서 필수 등록
            if(registerRequestDto instanceof ProductManagementRequest.RegisterEcoFriendlyProductDto) {
                registerCertificationOf(product, registerRequestDto);
            }
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
    public boolean deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

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
    public boolean modifyProduct(Long productId, ProductManagementRequest.ModifyDto modifyDto) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        product.setName(modifyDto.getProductName());
        product.setDescription(modifyDto.getDescription());
        product.setOrigin(modifyDto.getOrigin());
        product.setPrice(modifyDto.getPrice());
        product.setBaseDiscountRate(modifyDto.getBaseDiscountRate());
        product.setPersonalizeDiscountRate(modifyDto.getPersonalizedDiscountRate());
        product.setRegularDiscountRate(modifyDto.getRegularDiscountRate());
        product.setIsPageVisibility(ActiveStatus.fromCode(modifyDto.getIsPageVisibility()));
        product.setIsRegularSale(ActiveStatus.fromCode(modifyDto.getIsRegularSale()));
        return true;
    }

    @Override
    public List<ProductManagementResponse.RetrieveDto> retrieveCustomersProducts(Long customerId) {
        Customer customer =
                customerRepository.findById(customerId).orElseThrow(
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
                            .productName(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .origin(product.getOrigin())
                            .personalizedDiscountRate(product.getPersonalizeDiscountRate())
                            .baseDiscountRate(product.getBaseDiscountRate())
                            .regularDiscountRate(product.getRegularDiscountRate())
                            .isPageVisibility(product.getIsPageVisibility().getCode())
                            .isRegularSale(product.getIsRegularSale().getCode())
                            .build();
                        }
                    ).collect(Collectors.toList());
        return productList;
    }

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
                .productName(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .origin(product.getOrigin())
                .personalizedDiscountRate(product.getPersonalizeDiscountRate())
                .baseDiscountRate(product.getBaseDiscountRate())
                .regularDiscountRate(product.getRegularDiscountRate())
                .isPageVisibility(product.getIsPageVisibility().getCode())
                .isRegularSale(product.getIsRegularSale().getCode())
                .build();

        return productDto;
    }

    @Override
    public boolean uploadProductImage(Long productId, MultipartFile imageFile) {
        return false;
    }

    @Override
    public boolean uploadProductDetailImages(Long productId, MultipartFile imageFile) {
        return false;
    }

    @Override
    public boolean uploadCertificationImage(Long productId, MultipartFile imageFile) {
        return false;
    }

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
