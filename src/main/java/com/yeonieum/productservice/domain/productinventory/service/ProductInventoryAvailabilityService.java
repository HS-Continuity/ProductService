package com.yeonieum.productservice.domain.productinventory.service;

import com.yeonieum.productservice.cache.redis.StockRedisSetOperation;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryRequest;
import com.yeonieum.productservice.domain.productinventory.dto.AvailableProductInventoryResponse;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//@Service
@RequiredArgsConstructor
public class ProductInventoryAvailabilityService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StockRedisSetOperation stockRedisSetOperation;
    private final RedissonClient redissonClient;
    private final ProductInventoryManagementService productInventoryManagementService;

    /**
     * 주문 취소 이벤트 consume시 발생하는 재고사용량 감소
     * @param decreaseStockUsageDto
     * @return
     */
    public boolean decreaseStockUsage(AvailableProductInventoryRequest.DecreaseStockUsageDto decreaseStockUsageDto) {
        Long productId = decreaseStockUsageDto.getProductId();
        Long orderId = decreaseStockUsageDto.getOrderId();
        int quantity = decreaseStockUsageDto.getQuantity();

        Long decreaseCount = stockRedisSetOperation.removeStockUsage(redisTemplate, new StockUsageDto(productId, orderId, quantity));
        if(decreaseCount == 0) {
            return false;
        }
        return true;
    }

    /**
     * 주문서비스에서 요청하는 주문시 재고사용량 증가 액션 서비스
     * @param increaseStockUsageDtoList
     * @return
     */
    @Transactional
    public List<AvailableProductInventoryResponse> processProductInventory(List<AvailableProductInventoryRequest.IncreaseStockUsageDto> increaseStockUsageDtoList) {
        List<AvailableProductInventoryResponse> availableProductInventoryResponseList = new ArrayList<>();

        // [STEP1] [받아온 주문서의 상품들을 기준으로 구매가 가능한지 체크하고, 가능하다면 재고사용량을 증가시킨다.]
        for(AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto : increaseStockUsageDtoList) {
            Long productId = increaseStockUsageDto.getProductId();
            Long orderId = increaseStockUsageDto.getOrderId();
            int quantity = increaseStockUsageDto.getQuantity();

            //[STEP2] [응답객체에 주문서 상 상품에 대한 주문 가능여부를 담는다. -> 주문서비스는 주문 가능여부를 바탕으로 경합상황에서 주문서를 안전하게 생성할 수 있다.]
            AvailableProductInventoryResponse availableProductInventoryResponse = AvailableProductInventoryResponse.builder()
                                                            .productId(productId)
                                                            .orderId(orderId)
                                                            .quantity(quantity)
                                                            .isAvailableOrder(true)
                                                            .build();

            //[STEP3] [주문가능 여부 로직 체크 : 총 출고 가능량 과 재고 사용량 비교 -> 주문가능여부 반환]
            int available =
                    productInventoryManagementService.retrieveProductInventoryAmount(productId);

            //[STEP4] [분산락 적용하여 재고 사용량을 읽고 증가시키는 작업에 동시성 제어하여 출고가능량보다 재고 사용량이 초과되는 경우를 방지]
            RLock lock = redissonClient.getLock("stockusage" + "" +productId);
            try {
                if (!lock.tryLock(5, 1, TimeUnit.SECONDS)) {
                    availableProductInventoryResponse.changeIsAvailableOrder(false);
                    availableProductInventoryResponseList.add(availableProductInventoryResponse);
                    continue;
                    //throw new Exception("주문 실패 처리[응답]");
                }

                Integer totalStockUsage = stockRedisSetOperation.totalStockUsageCount(redisTemplate, productId);
                if(totalStockUsage == null) {
                    stockRedisSetOperation.getProductStock(productId);
                    totalStockUsage = stockRedisSetOperation.totalStockUsageCount(redisTemplate, productId);

                    if(totalStockUsage == null) {
                        totalStockUsage = 0;
                    }
                }


                if(totalStockUsage + quantity > available) {
                    availableProductInventoryResponse.changeIsAvailableOrder(false);
                    availableProductInventoryResponseList.add(availableProductInventoryResponse);
                    continue;
                }

                //[STEP5] [주문이 가능할 경우 재고사용량을 증가시키고, 주문가능여부 true로 설정한 응답객체 리스트에 추가]
                increaseStockUsage(increaseStockUsageDto, available);
                availableProductInventoryResponseList.add(availableProductInventoryResponse);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                // 모든 객체에 주문 불가 처리
                return availableProductInventoryResponseList;
            }
        }

        // [STEP6] [각 상품별 주문 가능 여부가 담긴 응답객체 리스트 반환]
        return availableProductInventoryResponseList;
    }


    /**
     * 주문 가능 시 재고사용량 증가 메서드
     * @param increaseStockUsageDto
     * @param available
     */
    public void increaseStockUsage(AvailableProductInventoryRequest.IncreaseStockUsageDto increaseStockUsageDto, int available){
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // [의문사항] [락을 걸어서 트랜잭션 진입했는데, 굳이 트랜잭션으로 묶어 격리를 시키는 것이 맞을까?] -> 추후 테스트 예정
                operations.multi();

                Long productId = increaseStockUsageDto.getProductId();
                Long orderId = increaseStockUsageDto.getOrderId();
                int quantity = increaseStockUsageDto.getQuantity();

                Integer totalStockUsageCount = stockRedisSetOperation.totalStockUsageCount(redisTemplate, increaseStockUsageDto.getProductId());
                if(totalStockUsageCount == null) {
                    totalStockUsageCount = 0;
                }

                if (available > totalStockUsageCount) {
                    stockRedisSetOperation.addStockUsage(redisTemplate, new StockUsageDto(productId, orderId, quantity));
                }
                operations.exec();
                return null;
            }
        });
    }


    /**
     * 주문 가능 상태인지 체크하는 메서드[품절여부 반환]
     * @param productId
     * @return
     */
    public boolean checkAvailableProduct(Long productId) {
        int available = productInventoryManagementService.retrieveProductInventoryAmount(productId);
        Integer totalStockUsage = stockRedisSetOperation.totalStockUsageCount(redisTemplate, productId);

        return available > totalStockUsage;
    }
}
