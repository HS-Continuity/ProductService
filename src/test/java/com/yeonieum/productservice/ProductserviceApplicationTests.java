package com.yeonieum.productservice;

import com.yeonieum.productservice.domain.productinventory.service.StockSystemService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;

//@SpringBootTest
class ProductserviceApplicationTests {
    @Autowired
    private StockSystemService stockSystemService;
    @Autowired
    private  RedissonClient redissonClient;
}




//   @Test
//   void contextLoads() throws InterruptedException {
//      int numUsers = 100;
//      ExecutorService executorService = Executors.newFixedThreadPool(numUsers);
//      for (int i = 1; i <= numUsers; i++) {
//         System.out.println(i);
//         int finalI = i;
//         executorService.submit(() -> {
//            // 각 사용자가 실행할 작업 정의 (예: 숫자 증가)
//            System.out.println(111);
//            StockUsageDto stockUsageDto = StockUsageDto.builder()
//                  .productId(1L)
//                  .orderId((long) finalI)
//                  .quantity(1)
//                  .build();
//            stockSystemService.processProductInventory(AvailableProductInventoryRequest.IncreaseStockUsageDto.builder()
//                  .productId(1L)
//                  .orderId((long) finalI)
//                  .quantity(10)
//                  .build());
//            return 1;
//         });
//      }
//
//      // 모든 사용자가 실행을 완료할 때까지 기다림
//      executorService.shutdown();
//      while (!executorService.isTerminated()) {
//         Thread.sleep(100); // 잠시 대기
//      }
//   }


