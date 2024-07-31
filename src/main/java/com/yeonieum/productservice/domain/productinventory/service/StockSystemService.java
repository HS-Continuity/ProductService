package com.yeonieum.productservice.domain.productinventory.service;

import com.yeonieum.productservice.infrastructure.cache.redis.StockSetOperation;
import com.yeonieum.productservice.infrastructure.cache.redis.StockUsageService;
import com.yeonieum.productservice.domain.product.entity.Product;
import com.yeonieum.productservice.domain.product.exception.ProductException;
import com.yeonieum.productservice.domain.product.repository.ProductRepository;
import com.yeonieum.productservice.domain.product.service.ProductShelflifeCache;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import com.yeonieum.productservice.domain.productinventory.repository.ProductInventoryRepository;
import com.yeonieum.productservice.global.lock.Lock;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yeonieum.productservice.domain.product.exception.ProductExceptionCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StockSystemService {
    private final ProductShelflifeCache productShelflifeCache;
    private final StockUsageService stockUsageService;
    //private final StockRedisSetOperation stockRedisSetOperation;
    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final StockSetOperation stockSetOperation;


    /**
     * 주문서비스에서 요청하는 주문시 재고사용량 증가 액션 서비스
     * @param ofIncreasing
     * @return
     */
    // AOP 프록시 객체 락 획득 시 트랜잭션 범위 안으로 편입되므로 트랜잭션 애너테이션 제거
    @Lock(keyPrefix = "lock:", leaseTime = 3)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AvailableProductInventoryResponse processProductInventory(StockUsageRequest.OfIncreasing ofIncreasing) {
        // [STEP1] [받아온 주문서의 상품들을 기준으로 구매가 가능한지 체크하고, 가능하다면 재고사용량을 증가시킨다.]
        Long productId = ofIncreasing.getProductId();
        String orderDetailId = ofIncreasing.getOrderDetailId();
        int quantity = ofIncreasing.getQuantity();

        //[STEP2] [응답객체에 주문서 상 상품에 대한 주문 가능여부를 담는다. -> 주문서비스는 주문 가능여부를 바탕으로 경합상황에서 주문서를 안전하게 생성할 수 있다.]
        AvailableProductInventoryResponse availableProductInventoryResponse = AvailableProductInventoryResponse.builder()
                                                        .productId(productId)
                                                        .orderDetailId(orderDetailId)
                                                        .quantity(quantity)
                                                        .build();


        //[STEP3] [주문가능 여부 로직 체크 : 총 출고 가능량 과 재고 사용량 비교 -> 주문가능여부 반환]
        int availableStockAmount = retrieveProductStockAmount(productId);
        boolean available =  stockUsageService.increaseStockUsage(new StockUsageDto(productId, orderDetailId, quantity), availableStockAmount);
        //[STEP4] [주문이 가능할 경우 재고사용량을 증가시키고, 주문가능여부 true로 설정한 응답객체 리스트에 추가]
        StockUsageDto stockUsageDto = StockUsageDto.builder()
                        .productId(ofIncreasing.getProductId())
                        .orderDetailId(ofIncreasing.getOrderDetailId())
                        .quantity(ofIncreasing.getQuantity())
                        .build();

        availableProductInventoryResponse.changeIsAvailableOrder(true);
        return availableProductInventoryResponse;
    }

    @Transactional(readOnly = true)
    public boolean checkAvailableOrderProduct(Long productId) {
        int available = retrieveProductStockAmount(productId);
        Long totalStockUsage = stockSetOperation.totalStockUsageCount(productId);
        //Integer totalStockUsage = stockRedisSetOperation.totalStockUsageCount(productId);

        return available > totalStockUsage;
    }

    @Transactional(readOnly = true)
    public Map<Long, Boolean> bulkCheckAvailableOrderProduct(List<Long> productIdList) {
        List<Boolean> resultList = new ArrayList<>();
        Map<Long, Boolean> resultMap = new HashMap<>();
        Map<Long, Integer> availableMap = bulkRetrieveProductStockAmount(productIdList);
        Map<Long, Long> totalStockUsage = stockSetOperation.bulkTotalStockUsageCount(productIdList);
        // stockRedisSetOperation.bulkTotalStockUsageCount(productIdList);

        for(Long productId : productIdList) {
            Integer available = availableMap.getOrDefault(productId, 0);
            Long usage = totalStockUsage.getOrDefault(productId, 0L);

            resultMap.put(productId, available > usage);
        }
        return resultMap;
    }

    public int retrieveProductStockAmount(Long productId) {
        try {
            Product product = productRepository.findById(productId).orElseThrow(
                    () -> new ProductException(PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND)
            );

            // 당일출고 기준시간 (조회해야함)
            int shippingCutoffTime = 14;
            long startTime = System.nanoTime();

            //[STEP1. 배송도착시 남아있어야하는 소비자의 최소 소비 기간을 조회해서 가져온다.]
            int lifeDay = 3;
                    //cache.cache(product);


            //[STEP2. 오늘 날짜 및 현재 시각을 조회한다.]
            LocalDate todayDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
            int additionalShippingDay = calculateAdditionalShippingDays(shippingCutoffTime, todayDate);

            //[STEP3. 리드타임 (배송-> 도착 기간)]
            int leadTime = 2;

            //[STEP4. 쿼리 기준일을 구한다. 오늘날짜 + 배송평균일 + 추가배송기간 + 사용자의 소비 기간]
            LocalDate queryDate = todayDate.plusDays((long)(lifeDay + leadTime + additionalShippingDay));

            Integer result = productInventoryRepository.findAvailableInventoryQuantityByProductIdAndExpirationDate(productId, queryDate);
            Integer resp = null;
            if(result == null) {
                resp = 0;
            } else {
                resp = result;
            }

            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Map<Long, Integer> bulkRetrieveProductStockAmount(List<Long> productId) {
        try {
            List<Integer> respList = new ArrayList<>();
            List<Product> productList = productRepository.findAllById(productId);
            List<LocalDate> expirationDateList = new ArrayList<>();
            Map<Long, Integer> productStockMap = new HashMap();
            for(Product product : productList) {
                // 당일출고 기준시간 (조회해야함)
                int shippingCutoffTime = 14;
                long startTime = System.nanoTime();

                //[STEP1. 배송도착시 남아있어야하는 소비자의 최소 소비 기간을 조회해서 가져온다.]
                int lifeDay = productShelflifeCache.getProductShelflife(product);


                //[STEP2. 오늘 날짜 및 현재 시각을 조회한다.]
                LocalDate todayDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
                int additionalShippingDay = calculateAdditionalShippingDays(shippingCutoffTime, todayDate);

                //[STEP3. 리드타임 (배송-> 도착 기간)]
                int leadTime = 2;

                //[STEP4. 쿼리 기준일을 구한다. 오늘날짜 + 배송평균일 + 추가배송기간 + 사용자의 소비 기간]
                LocalDate queryDate = todayDate.plusDays((long)(lifeDay + leadTime + additionalShippingDay));
                expirationDateList.add(queryDate);

                Integer resp = null;
                Integer result = productInventoryRepository.findAvailableInventoryQuantityByProductIdAndExpirationDate(product.getProductId(), queryDate);
                if(result == null) {
                    resp = 0;
                } else {
                    resp = result;
                }
                productStockMap.put(product.getProductId(), resp);
                respList.add(resp);
            }
            //return respList;
            return productStockMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
