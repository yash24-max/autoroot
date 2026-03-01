package com.autoroot.common.repository;

import com.autoroot.common.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Service entities.
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    Optional<Service> findByTenantIdAndName(UUID tenantId, String name);
}
