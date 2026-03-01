package com.autoroot.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for LogEntry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntryDto {
    private UUID id;
    private UUID tenantId;
    private String traceId;
    private String spanId;
    private UUID serviceId;
    private String serviceName;
    private UUID incidentId;
    private String level;
    private String message;
    private String source;
    private String component;
    private String metadata;
    private LocalDateTime timestamp;
}
