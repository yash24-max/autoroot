package com.autoroot.incident.repository;

import com.autoroot.common.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Incident entity with multi-tenant support.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    /**
     * Find all incidents for a specific tenant.
     */
    Page<Incident> findByTenantIdOrderByStartedAtDesc(UUID tenantId, Pageable pageable);

    /**
     * Find incident by ID and tenant ID.
     */
    Optional<Incident> findByIdAndTenantId(UUID id, UUID tenantId);

    /**
     * Find all incidents by status for a tenant.
     */
    List<Incident> findByTenantIdAndStatusOrderByStartedAtDesc(UUID tenantId, Incident.IncidentStatus status);

    /**
     * Find all incidents by severity for a tenant.
     */
    List<Incident> findByTenantIdAndSeverityOrderByStartedAtDesc(UUID tenantId, Incident.IncidentSeverity severity);

    /**
     * Find all incidents for a specific service.
     */
    Page<Incident> findByTenantIdAndServiceIdOrderByStartedAtDesc(UUID tenantId, UUID serviceId, Pageable pageable);

    /**
     * Find open incidents for a tenant.
     */
    @Query("SELECT i FROM Incident i WHERE i.tenantId = :tenantId AND i.status IN :statuses ORDER BY i.startedAt DESC")
    List<Incident> findOpenIncidents(@Param("tenantId") UUID tenantId, 
                                   @Param("statuses") List<Incident.IncidentStatus> statuses);

    /**
     * Find incidents within a date range for a tenant.
     */
    @Query("SELECT i FROM Incident i WHERE i.tenantId = :tenantId AND i.startedAt BETWEEN :startDate AND :endDate ORDER BY i.startedAt DESC")
    List<Incident> findByTenantIdAndDateRange(@Param("tenantId") UUID tenantId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Count incidents by status for a tenant.
     */
    long countByTenantIdAndStatus(UUID tenantId, Incident.IncidentStatus status);

    /**
     * Count incidents by severity for a tenant.
     */
    long countByTenantIdAndSeverity(UUID tenantId, Incident.IncidentSeverity severity);
}