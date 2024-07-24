package com.yeonieum.productservice;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = RedissonAutoConfiguration.class)
public class ProductserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductserviceApplication.class, args);
	}
}
