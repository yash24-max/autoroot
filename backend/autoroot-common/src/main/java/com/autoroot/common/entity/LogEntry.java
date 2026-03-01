package com.autoroot.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Log entry entity for storing log metadata.
 */
@Entity
@Table(name = "log_entries", indexes = {
        @Index(name = "idx_log_entries_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_log_entries_trace_id", columnList = "trace_id"),
        @Index(name = "idx_log_entries_service_id", columnList = "service_id"),
        @Index(name = "idx_log_entries_incident_id", columnList = "incident_id"),
        @Index(name = "idx_log_entries_level", columnList = "level"),
        @Index(name = "idx_log_entries_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
public class LogEntry extends BaseEntity {

    @Column(name = "trace_id", length = 255)
    private String traceId;

    @Column(name = "span_id", length = 255)
    private String spanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(name = "fk_log_entry_service"))
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", foreignKey = @ForeignKey(name = "fk_log_entry_incident"))
    private Incident incident;

    @Column(name = "level", nullable = false, length = 10)
    private String level;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "source", length = 100)
    private String source; // application, system, external

    @Column(name = "component", length = 100)
    private String component; // specific component that generated the log

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public LogEntry(String level, String message, String source, String component, String traceId) {
        this.level = level;
        this.message = message;
        this.source = source;
        this.component = component;
        this.traceId = traceId;
        this.timestamp = LocalDateTime.now();
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR, FATAL
    }
}