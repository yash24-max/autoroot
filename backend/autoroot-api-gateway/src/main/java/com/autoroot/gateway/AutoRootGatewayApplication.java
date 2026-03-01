package com.autoroot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for AutoRoot API Gateway.
 */
@SpringBootApplication(scanBasePackages = {"com.autoroot"})
@EntityScan(basePackages = {"com.autoroot.common.entity"})
@EnableJpaRepositories(basePackages = {"com.autoroot"})
@EnableKafka
public class AutoRootGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoRootGatewayApplication.class, args);
    }
}