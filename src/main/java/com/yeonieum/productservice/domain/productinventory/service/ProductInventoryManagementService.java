package com.yeonieum.productservice.domain.productinventory.service;

import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.productinventory.dto.ProductInventoryManagementRequest;
import com.yeonieum.productservice.domain.productinventory.dto.RetrieveProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.entity.ProductInventory;
import com.yeonieum.productservice.domain.productinventory.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public boolean registerProductInventory(ProductInventoryManagementRequest.RegisterDto registerDto) {
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


        // 상품의 판매가능(품절, 판매가능) 상태 변경로직 추가예정
        productInventoryRepository.save(productInventory);
        return true;
    }

    /**
     * 고객의 상품 재고등록현황 조회
     * @param productId
     * @param pageable
     * @return
     */
    public List<RetrieveProductInventoryResponse> retrieveProductInventories(Long productId, Pageable pageable) {
        // 고객아이디 조회 가능한지 여부 체크 로직 추가
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        // Pageable pageable = PageRequest.of(0, 10); 서비스레이어에서 처리하는 것이 맞을까, 컨트롤러에서 처리하고 내리는 것이 맞을까 고민해보기
        List<RetrieveProductInventoryResponse> productInventoryList =
                productInventoryRepository.findAllbyProductId(productId, pageable);

        return productInventoryList;
    }

    /**
     * 등록한 상품재고 수정
     * @param productInventoryId
     * @param modifyDto
     * @return
     */
    public boolean modifyProductInventory(Long productInventoryId, ProductInventoryManagementRequest.ModifyDto modifyDto) {
        ProductInventory productInventory = productInventoryRepository.findById(productInventoryId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        Product product = productInventory.getProduct();
        // 고객권한 찾기
        productInventory.setQuantity(modifyDto.getQuantity());
        productInventory.setWarehouseDate(modifyDto.getWarehousingDate());
        productInventory.setExpirationDate(modifyDto.getExpirationDate());

        return true;
    }

    public int retrieveProductInventoryAmount(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        return 0;
    }
}
