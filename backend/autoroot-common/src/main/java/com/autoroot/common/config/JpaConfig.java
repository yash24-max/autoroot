package com.autoroot.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration for AutoRoot application.
 */
@Configuration
@EnableJpaRepositories(basePackages = { "com.autoroot" })
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

    // Additional JPA configurations can be added here
}