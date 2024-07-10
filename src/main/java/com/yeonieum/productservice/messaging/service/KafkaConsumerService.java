package com.yeonieum.productservice.messaging.service;

import com.yeonieum.productservice.cache.redis.StockRedisSetOperation;
import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;

//@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final StockRedisSetOperation stockRedisSetOperation;

    @KafkaListener(groupId = "shippedstock_group", topics = "shippedstock-topic", containerFactory = "shippedStockKafkaListenerContainerFactory")
    public void listen(ShippedStockDto shippedStockDto) {
        //System.out.println(shippedStockDto);
        stockRedisSetOperation.addShippedStock(shippedStockDto);
    }


    @KafkaListener(groupId = "stockusage_group", topics = "stockusage-topic", containerFactory = "stockUsageKafkaListenerContainerFactory")
    public void listen(StockUsageDto stockUsageDto) {
        //System.out.println(shippedStockDto);
        stockRedisSetOperation.removeStockUsage(stockUsageDto);
    }
}
