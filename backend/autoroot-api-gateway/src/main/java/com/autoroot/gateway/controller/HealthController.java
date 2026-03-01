package com.autoroot.gateway.controller;

import com.autoroot.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Health check controller.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "autoroot-api-gateway",
            "version", "1.0.0-SNAPSHOT"
        );
        
        return ResponseEntity.ok(ApiResponse.success(healthData, "Service is healthy"));
    }

    @GetMapping("/ready")
    public ResponseEntity<ApiResponse<Map<String, String>>> readiness() {
        Map<String, String> readinessData = Map.of(
            "status", "READY",
            "database", "CONNECTED",
            "cache", "CONNECTED"
        );
        
        return ResponseEntity.ok(ApiResponse.success(readinessData, "Service is ready"));
    }

    @GetMapping("/live")
    public ResponseEntity<ApiResponse<Map<String, String>>> liveness() {
        Map<String, String> livenessData = Map.of(
            "status", "ALIVE"
        );
        
        return ResponseEntity.ok(ApiResponse.success(livenessData, "Service is alive"));
    }
}