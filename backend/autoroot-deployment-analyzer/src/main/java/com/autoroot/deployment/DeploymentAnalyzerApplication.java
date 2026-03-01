package com.autoroot.deployment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for AutoRoot Deployment Analyzer.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.autoroot.common.entity")
@EnableJpaRepositories(basePackages = {
        "com.autoroot.common.repository",
        "com.autoroot.incident.repository"
})
public class DeploymentAnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeploymentAnalyzerApplication.class, args);
    }
}
