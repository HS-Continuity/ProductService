package com.yeonieum.productservice.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(Arrays.asList("Content-Type", "application/json"));
        corsConfiguration.addExposedHeader("Bearer");
        corsConfiguration.addExposedHeader("provider");
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5174", "http://localhost:3000"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"));

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5174", "http://localhost:3000")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS")
                .allowedHeaders("Content-Type", "application/json")
                .exposedHeaders("Bearer", "provider")
                .allowCredentials(true);
    }
}
