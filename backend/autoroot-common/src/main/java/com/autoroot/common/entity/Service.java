package com.autoroot.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Service entity representing monitored services.
 */
@Entity
@Table(name = "services", indexes = {
        @Index(name = "idx_services_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_services_status", columnList = "status"),
        @Index(name = "idx_services_tenant_name", columnList = "tenant_id, name")
})
@Getter
@Setter
@NoArgsConstructor
public class Service extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "url", length = 255)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceStatus status = ServiceStatus.HEALTHY;

    @Column(name = "health_check_url", length = 255)
    private String healthCheckUrl;

    @Column(name = "health_check_interval", nullable = false)
    private Integer healthCheckInterval = 300; // seconds

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "service_version", length = 100)
    private String serviceVersion;

    @Column(name = "last_seen")
    private java.time.LocalDateTime lastSeen;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Service(String name, String description, String url) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.status = ServiceStatus.HEALTHY;
        this.isActive = true;
    }

    public enum ServiceStatus {
        HEALTHY, DEGRADED, DOWN, MAINTENANCE
    }
}