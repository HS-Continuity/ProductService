package com.yeonieum.productservice.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
@Getter

public class ShippedEventMessageList {
    List<ShippedEventMessage> shippedEventMessages;
    @JsonCreator
    public ShippedEventMessageList(@JsonProperty("shippedEventMessages") List<ShippedEventMessage> shippedEventMessages) {
        this.shippedEventMessages = shippedEventMessages;
    }
}
