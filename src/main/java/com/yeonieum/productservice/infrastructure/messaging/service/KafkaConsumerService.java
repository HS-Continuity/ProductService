package com.yeonieum.productservice.infrastructure.messaging.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonieum.productservice.infrastructure.cache.data.StockUsageCache;
import com.yeonieum.productservice.domain.productinventory.dto.ShippedStockDto;
import com.yeonieum.productservice.infrastructure.cache.redis.StockSetOperation;
import com.yeonieum.productservice.infrastructure.messaging.dto.ShippedEventMessage;
import com.yeonieum.productservice.infrastructure.messaging.dto.ShippedEventMessageList;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final StockSetOperation stockSetOperation;
    private final ObjectMapper objectMapper;
    @KafkaListener(id = "shipped-order-consumer", topics = "shipped-order-topic", groupId = "shipped-order-group", autoStartup = "true")
    public void listenApproveEvent(@Payload String message) {
        try {
            System.out.println("adfadfsdf");
            List<ShippedEventMessage> shippedStockEventMessages = objectMapper.readValue(message, new TypeReference<List<ShippedEventMessage>>(){});
            for(ShippedEventMessage shippedEventMessage : shippedStockEventMessages) {
                List<StockUsageCache>  stockUsageCaches = new ArrayList<>();
                for(int i = 0; i < shippedEventMessage.getQuantity(); i++) {
                    stockUsageCaches.add(StockUsageCache.builder()
                            .id(i)
                            .orderDetailId(shippedEventMessage.getOrderDetailId())
                            .productId(shippedEventMessage.getProductId())
                            .shippedAt(shippedEventMessage.getShippedAt())
                            .build());
                }
                stockSetOperation.addShippedStock(shippedEventMessage.getProductId() , stockUsageCaches);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @KafkaListener(id = "cancel-order-consumer", topics = "cancel-order-topic", groupId = "cancel-order-group", autoStartup = "true")
    public void listenCancelEvent(@Payload String message) {
        try {
            List<ShippedEventMessage> shippedStockEventMessages = objectMapper.readValue(message, new TypeReference<List<ShippedEventMessage>>(){});
            for(ShippedEventMessage shippedEventMessage : shippedStockEventMessages) {
                List<StockUsageCache>  stockUsageCaches = new ArrayList<>();
                for(int i = 0; i < shippedEventMessage.getQuantity(); i++) {
                    stockUsageCaches.add(StockUsageCache.builder()
                            .id(i)
                            .orderDetailId(shippedEventMessage.getOrderDetailId())
                            .productId(shippedEventMessage.getProductId())
                            .shippedAt(shippedEventMessage.getShippedAt())
                            .build());
                }
                stockSetOperation.removeStockUsage(shippedEventMessage.getProductId() , shippedEventMessage.getOrderDetailId(), stockUsageCaches);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
