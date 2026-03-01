package com.autoroot.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tenant entity for multi-tenancy support.
 */
@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenants_slug", columnList = "slug"),
    @Index(name = "idx_tenants_domain", columnList = "domain")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;

    @Column(name = "domain", length = 100)
    private String domain;

    @Column(name = "settings", columnDefinition = "jsonb")
    private String settings;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Tenant(String name, String slug, String domain) {
        this.name = name;
        this.slug = slug;
        this.domain = domain;
        this.isActive = true;
    }
}