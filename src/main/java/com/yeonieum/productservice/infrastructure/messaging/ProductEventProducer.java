package com.yeonieum.productservice.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeonieum.productservice.infrastructure.messaging.message.AdvertisementEventMessage;
import com.yeonieum.productservice.infrastructure.messaging.message.TimesaleEventMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProductEventProducer {

    private final ObjectMapper objectMapper;
    public static final String TIMESALE_TOPIC = "timesale-topic";
    public static final String ADVERTISEMENT_TOPIC = "advertisement-topic";


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(AdvertisementEventMessage message) throws JsonProcessingException {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(ADVERTISEMENT_TOPIC, objectMapper.writeValueAsString(message));

        // 성공 및 실패 처리
        future.thenAccept(result -> {

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void sendMessage(TimesaleEventMessage message) throws JsonProcessingException {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TIMESALE_TOPIC, objectMapper.writeValueAsString(message));

        // 성공 및 실패 처리
        future.thenAccept(result -> {

        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

}
