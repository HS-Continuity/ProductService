package com.yeonieum.productservice.domain.productinventory.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.productinventory.dto.ProductInventoryManagementRequest;
import com.yeonieum.productservice.domain.productinventory.dto.ProductInventorySummaryResponse;
import com.yeonieum.productservice.domain.productinventory.dto.RetrieveProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.entity.ProductInventory;
import com.yeonieum.productservice.domain.productinventory.exception.ProductInventoryException;
import com.yeonieum.productservice.domain.productinventory.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.PRODUCT_NOT_FOUND;
import static com.yeonieum.productservice.domain.productinventory.exception.ProductInventoryExceptionCode.PRODUCT_INVENTORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductInventoryManagementService {
    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;

    /**
     * 상품재고등록
     * @param registerDto
     * @return
     */
    @Transactional
    public void registerProductInventory(Long customerId, ProductInventoryManagementRequest.RegisterDto registerDto) {
        Product product = productRepository.findByProductIdAndCustomer_CustomerId(registerDto.getProductId(), customerId);
        if (product == null) {
            throw new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        // 출고가능한 유효기간만 입력할 수 있도록 체크하는 로직 추가 예정
        ProductInventory productInventory = ProductInventory.builder()
                .product(product)
                .quantity(registerDto.getQuantity())
                .warehouseDate(registerDto.getWarehouseDate())
                .expirationDate(registerDto.getExpirationDate())
                .build();

        productInventoryRepository.save(productInventory);
    }

    /**
     * 고객의 상품 재고등록현황 조회
     * @param productId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public List<RetrieveProductInventoryResponse> retrieveProductInventorySummary(Long customerId, Long productId, Pageable pageable) {
        Product product = productRepository.findByProductIdAndCustomer_CustomerId(productId, customerId);
        if (product == null) {
            throw new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        List<ProductInventory> productInventoryList =
                productInventoryRepository.findAllbyProductId(productId, pageable);

        return productInventoryList.stream().map(RetrieveProductInventoryResponse::toEntity).collect(Collectors.toList());    }

    /**
     * 등록한 상품재고 수정(폐기 시에도 사용 가능)
     * @param productInventoryId
     * @param modifyDto
     * @return
     */
    @Transactional
    public void modifyProductInventory(Long customerId, Long productInventoryId, ProductInventoryManagementRequest.ModifyDto modifyDto) {
        ProductInventory productInventory = productInventoryRepository.findById(productInventoryId).orElseThrow(
                () -> new ProductInventoryException(PRODUCT_INVENTORY_NOT_FOUND, HttpStatus.NOT_FOUND)
        );

        Product product = productInventory.getProduct();
        if(product == null || product.getCustomer().getCustomerId() !=customerId) {
            throw new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.");
        }

        productInventory.changeQuantity(modifyDto.getQuantity());
    }


    /**
     * 고객이 등록한 상품재고 summary 조회(상품별 재고수량 합계 조회)
      * @return
     */
    @Transactional(readOnly = true)
    public Page<ProductInventorySummaryResponse> retrieveProductInventoryList(Long customerId, Pageable pageable) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Page<Object[]> productInventoryPage = productInventoryRepository.findInventorySumByProductsAndExpirations(customerId, today, pageable);

        return productInventoryPage.map(
                object -> new ProductInventorySummaryResponse(
                        Long.parseLong(object[0].toString()),
                        object[1].toString(),
                        Long.parseLong(object[2].toString())
                )
        );
    }
}
