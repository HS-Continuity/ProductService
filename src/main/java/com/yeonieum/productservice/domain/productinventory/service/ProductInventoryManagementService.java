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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
                () -> new IllegalArgumentException("해당하는 상품재고가 존재하지 않습니다.")
        );

        Product product = productInventory.getProduct();
        // 고객권한 찾기
        productInventory.changeQuantity(modifyDto.getQuantity());
        return true;
    }

    @Transactional(readOnly = true)
    public int retrieveProductInventoryAmount(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품이 존재하지 않습니다.")
        );

        // 당일출고 기준시간 (조회해야함)
        int shippingCutoffTime = 14;

        //[STEP1. 배송도착시 남아있어야하는 소비자의 최소 소비 기간을 조회해서 가져온다.]
        int lifeDay = product.getProductDetailCategory().getShelfLifeDay();

        //[STEP2. 오늘 날짜 및 현재 시각을 조회한다.]
        LocalDate todayDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int additionalShippingDay = calculateAdditionalShippingDays(shippingCutoffTime, todayDate);

        //[STEP3. 리드타임 (배송-> 도착 기간)]
        int leadTime = 2;

        //[STEP4. 쿼리 기준일을 구한다. 오늘날짜 + 배송평균일 + 추가배송기간 + 사용자의 소비 기간]
        LocalDate queryDate = todayDate.plusDays((long)(lifeDay + leadTime + additionalShippingDay));

        int availableProductQuantity = productInventoryRepository.findAvailableInventoryQuantityByProductIdAndExpirationDate(productId, queryDate);
        return availableProductQuantity;
    }

    public int calculateAdditionalShippingDays(int shippingCutoffTime , LocalDate today) {
        int currentHour = LocalTime.now().getHour();
        int additionalShippingDays = 0;

        additionalShippingDays = switch (today.getDayOfWeek()) {
            case SATURDAY -> 2;
            case SUNDAY -> 1;
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY ->
                    currentHour >= shippingCutoffTime ? 1 : 0;
            case FRIDAY ->
                    currentHour >= shippingCutoffTime ? 3 : 0;
            default -> 0;
        };

        return additionalShippingDays;
    }

    @Transactional
    public void disposeProductInventory(Long productInventoryId) {
        ProductInventory productInventory = productInventoryRepository.findById(productInventoryId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 상품재고가 존재하지 않습니다.")
        );
        // 상품재고 상태 속성 테이블에 추가 후 로직 작성
    }
}
