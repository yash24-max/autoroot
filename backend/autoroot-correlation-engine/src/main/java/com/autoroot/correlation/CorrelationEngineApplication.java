package com.autoroot.correlation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for AutoRoot Correlation Engine.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.autoroot.common.entity")
@EnableJpaRepositories(basePackages = {
        "com.autoroot.common.repository",
        "com.autoroot.incident.repository"
})
public class CorrelationEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(CorrelationEngineApplication.class, args);
    }
}
