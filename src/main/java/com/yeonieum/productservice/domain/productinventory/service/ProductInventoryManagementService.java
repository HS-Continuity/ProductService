package com.yeonieum.productservice.domain.productinventory.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.productinventory.dto.ProductInventoryManagementRequest;
import com.yeonieum.productservice.domain.productinventory.dto.ProductInventorySummaryResponse;
import com.yeonieum.productservice.domain.productinventory.dto.RetrieveProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.entity.ProductInventory;
import com.yeonieum.productservice.domain.productinventory.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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
    public void registerProductInventory(ProductInventoryManagementRequest.RegisterDto registerDto) {
        Product product = productRepository.findById(registerDto.getProductId()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );
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
    public List<RetrieveProductInventoryResponse> retrieveProductInventorySummary(Long productId, Pageable pageable) {
        // 고객아이디 조회 가능한지 여부 체크 로직 추가
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        List<RetrieveProductInventoryResponse> productInventoryList =
                productInventoryRepository.findAllbyProductId(productId, pageable);

        return productInventoryList;
    }

    /**
     * 등록한 상품재고 수정(폐기 시에도 사용 가능)
     * @param productInventoryId
     * @param modifyDto
     * @return
     */
    public void modifyProductInventory(Long productInventoryId, ProductInventoryManagementRequest.ModifyDto modifyDto) {
        ProductInventory productInventory = productInventoryRepository.findById(productInventoryId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품재고가 존재하지 않습니다.")
        );

        Product product = productInventory.getProduct();
        productInventory.changeQuantity(modifyDto.getQuantity());
    }


    /**
     * 고객이 등록한 상품재고 summary 조회(상품별 재고수량 합계 조회)
      * @return
     */
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
