package com.yeonieum.productservice.domain.productinventory.service;

import com.yeonieum.productservice.cache.redis.StockRedisSetOperation;
import com.yeonieum.productservice.cache.redis.StockUsageService;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.repository.ProductInventoryRepository;
import com.yeonieum.productservice.global.lock.Lock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class StockSystemService {

    private final StockUsageService stockUsageService;
    private final StockRedisSetOperation stockRedisSetOperation;
    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;


    /**
     * 주문서비스에서 요청하는 주문시 재고사용량 증가 액션 서비스
     * @param increaseStockUsageDto
     * @return
     */
    // AOP 프록시 객체 락 획득 시 트랜잭션 범위 안으로 편입되므로 트랜잭션 애너테이션 제거
    @Lock(keyPrefix = "stockusage:", leaseTime = 1)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AvailableProductInventoryResponse processProductInventory(AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto) {
        // [STEP1] [받아온 주문서의 상품들을 기준으로 구매가 가능한지 체크하고, 가능하다면 재고사용량을 증가시킨다.]
        Long productId = increaseStockUsageDto.getProductId();
        Long orderId = increaseStockUsageDto.getOrderId();
        int quantity = increaseStockUsageDto.getQuantity();

        System.out.println(productId + " " + orderId + " " + quantity);

        //[STEP2] [응답객체에 주문서 상 상품에 대한 주문 가능여부를 담는다. -> 주문서비스는 주문 가능여부를 바탕으로 경합상황에서 주문서를 안전하게 생성할 수 있다.]
        AvailableProductInventoryResponse availableProductInventoryResponse = AvailableProductInventoryResponse.builder()
                                                        .productId(productId)
                                                        .orderId(orderId)
                                                        .quantity(quantity)
                                                        .build();


        //[STEP3] [주문가능 여부 로직 체크 : 총 출고 가능량 과 재고 사용량 비교 -> 주문가능여부 반환]
        int availableStockAmount = retrieveProductStockAmount(productId);
        boolean available = stockUsageService.increaseStockUsage(new StockUsageDto(productId, orderId, quantity), availableStockAmount);
        //[STEP4] [주문이 가능할 경우 재고사용량을 증가시키고, 주문가능여부 true로 설정한 응답객체 리스트에 추가]
        StockUsageDto stockUsageDto = StockUsageDto.builder()
                        .productId(increaseStockUsageDto.getProductId())
                        .orderId(increaseStockUsageDto.getOrderId())
                        .quantity(increaseStockUsageDto.getQuantity())
                        .build();
        if(available) {
            availableProductInventoryResponse.changeIsAvailableOrder(true);
        }

        return availableProductInventoryResponse;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean checkAvailableOrderProduct(Long productId) {
        int available = retrieveProductStockAmount(productId);
        Integer totalStockUsage = stockRedisSetOperation.totalStockUsageCount(productId);

        return available > totalStockUsage;
    }

    public int retrieveProductStockAmount(Long productId) {
        try {
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

            productInventoryRepository.findAvailableInventoryQuantityByProductIdAndExpirationDate(productId, queryDate);
            /**
             * null 처리 하기
             */

            return 100;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
}
