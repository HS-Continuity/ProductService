package com.yeonieum.productservice.global.config;

import com.yeonieum.productservice.web.interceptor.UserContextInterceptor;
import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
//    @Bean
//    public Feign.Builder feignBuilder() {
//        return Feign.builder();
//    }
//    @Bean
    public RequestInterceptor userContextInterceptor() {
        return new UserContextInterceptor();
    }
}