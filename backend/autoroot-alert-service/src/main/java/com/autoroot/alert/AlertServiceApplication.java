package com.autoroot.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for AutoRoot Alert Service.
 */
@SpringBootApplication(scanBasePackages = { "com.autoroot.alert", "com.autoroot.common" })
public class AlertServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlertServiceApplication.class, args);
    }
}
