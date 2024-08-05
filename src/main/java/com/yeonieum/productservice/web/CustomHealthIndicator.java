package com.yeonieum.productservice.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
@RefreshScope
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Value("${server.communication.enabled}")
    private boolean isCommunicationEnabled;

    @Override
    public Health health() {
        if (isCommunicationEnabled) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("reason", "Communication is disabled").build();
        }
    }
}