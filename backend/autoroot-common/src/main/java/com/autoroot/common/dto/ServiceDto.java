package com.autoroot.common.dto;

import com.autoroot.common.entity.Service;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service Data Transfer Object.
 */
@Data
public class ServiceDto {

    private UUID id;

    @JsonProperty("tenant_id")
    private UUID tenantId;

    @NotBlank(message = "Service name is required")
    private String name;

    private String description;

    private String url;

    @NotNull(message = "Service status is required")
    private Service.ServiceStatus status;

    @JsonProperty("health_check_url")
    private String healthCheckUrl;

    @JsonProperty("health_check_interval")
    @Positive(message = "Health check interval must be positive")
    private Integer healthCheckInterval;

    private String metadata;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private Long version;
}