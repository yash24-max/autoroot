package com.autoroot.common.repository;

import com.autoroot.common.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for LogEntry entity.
 */
@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {

    List<LogEntry> findByTenantIdAndTraceIdOrderByTimestampAsc(UUID tenantId, String traceId);

    List<LogEntry> findByTenantIdAndServiceNameOrderByTimestampDesc(UUID tenantId, String serviceName);

    @Query("SELECT l FROM LogEntry l WHERE l.tenantId = :tenantId AND l.traceId = :traceId")
    List<LogEntry> findByTraceId(@Param("tenantId") UUID tenantId, @Param("traceId") String traceId);
}
