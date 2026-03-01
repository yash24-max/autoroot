package com.autoroot.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Incident entity for tracking service issues.
 */
@Entity
@Table(name = "incidents", indexes = {
        @Index(name = "idx_incidents_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_incidents_service_id", columnList = "service_id"),
        @Index(name = "idx_incidents_status", columnList = "status"),
        @Index(name = "idx_incidents_severity", columnList = "severity"),
        @Index(name = "idx_incidents_started_at", columnList = "started_at")
})
@Getter
@Setter
@NoArgsConstructor
public class Incident extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(name = "fk_incident_service"))
    private Service service;

    @Column(name = "root_cause_service", length = 255)
    private String rootCauseService;

    @Column(name = "trace_id", length = 255)
    private String traceId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IncidentStatus status = IncidentStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private IncidentSeverity severity = IncidentSeverity.MEDIUM;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    public Incident(String title, String description, IncidentSeverity severity) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = IncidentStatus.OPEN;
        this.startedAt = LocalDateTime.now();
    }

    public enum IncidentStatus {
        OPEN, INVESTIGATING, RESOLVED, CLOSED
    }

    public enum IncidentSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}