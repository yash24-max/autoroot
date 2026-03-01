package com.autoroot.common.repository;

import com.autoroot.common.entity.DeploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for DeploymentHistory entities.
 */
@Repository
public interface DeploymentHistoryRepository extends JpaRepository<DeploymentHistory, UUID> {
    List<DeploymentHistory> findByTenantIdAndServiceIdAndDeployedAtAfter(UUID tenantId, UUID serviceId,
            LocalDateTime deployedAt);
}
