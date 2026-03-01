package com.autoroot.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity for tracking service deployment history.
 */
@Entity
@Table(name = "deployment_history", indexes = {
        @Index(name = "idx_deployment_history_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_deployment_history_service_id", columnList = "service_id"),
        @Index(name = "idx_deployment_history_deployed_at", columnList = "deployed_at")
})
@Getter
@Setter
@NoArgsConstructor
public class DeploymentHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(name = "fk_deployment_service"))
    private Service service;

    @Column(name = "deployment_version", length = 100)
    private String deploymentVersion;

    @Column(name = "status", length = 50)
    private String status; // SUCCESS, FAILED, IN_PROGRESS

    @Column(name = "environment", length = 50)
    private String environment; // production, staging, dev

    @Column(name = "deployed_at", nullable = false)
    private LocalDateTime deployedAt;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
}
