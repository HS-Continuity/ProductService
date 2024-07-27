package com.yeonieum.productservice.messaging.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonieum.productservice.cache.redis.StockRedisSetOperation;
import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.domain.productinventory.dto.StockUsageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final StockRedisSetOperation stockRedisSetOperation;
    private final ObjectMapper objectMapper;

    //@KafkaListener(groupId = "shippedstock-group", topics = "shippedstock-topic", containerFactory = "shippedStockKafkaListenerContainerFactory")
    public void listen(ShippedStockDto shippedStockDto) {
        stockRedisSetOperation.addShippedStock(shippedStockDto);
    }


    //@KafkaListener(groupId = "stockusage-group", topics = "stockusage-topic", containerFactory = "stockUsageKafkaListenerContainerFactory")
    public void listen(StockUsageDto stockUsageDto) {
        stockRedisSetOperation.removeStockUsage(stockUsageDto);
    }
}
