package com.yeonieum.productservice;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = RedissonAutoConfiguration.class)
@EnableFeignClients
//@EnableDiscoveryClient
@EnableScheduling
public class ProductserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductserviceApplication.class, args);
	}
}
