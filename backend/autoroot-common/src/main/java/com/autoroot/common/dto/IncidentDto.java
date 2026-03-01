package com.autoroot.common.dto;

import com.autoroot.common.entity.Incident;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Incident Data Transfer Object.
 */
@Data
public class IncidentDto {

    private UUID id;

    @JsonProperty("tenant_id")
    private UUID tenantId;

    @JsonProperty("service_id")
    private UUID serviceId;

    @JsonProperty("service_name")
    private String serviceName;

    @NotBlank(message = "Incident title is required")
    private String title;

    private String description;

    @NotNull(message = "Incident status is required")
    private Incident.IncidentStatus status;

    @NotNull(message = "Incident severity is required")
    private Incident.IncidentSeverity severity;

    @JsonProperty("started_at")
    @NotNull(message = "Started at is required")
    private LocalDateTime startedAt;

    @JsonProperty("resolved_at")
    private LocalDateTime resolvedAt;

    private String metadata;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private Long version;
}