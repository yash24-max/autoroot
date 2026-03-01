package com.autoroot.correlation.service;

import com.autoroot.common.entity.Incident;
import com.autoroot.common.entity.LogEntry;
import com.autoroot.common.repository.LogEntryRepository;
import com.autoroot.incident.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for correlating logs based on trace IDs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CorrelationService {

    private final LogEntryRepository logEntryRepository;
    private final IncidentRepository incidentRepository;

    /**
     * Correlate a new log entry with existing logs and incidents.
     * 
     * @param logEntryId The ID of the log entry to correlate.
     */
    @Transactional
    public void correlateLog(UUID logEntryId) {
        log.debug("Correlating log entry: {}", logEntryId);

        LogEntry currentLog = logEntryRepository.findById(logEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + logEntryId));

        String traceId = currentLog.getTraceId();
        if (traceId == null || traceId.isEmpty()) {
            log.debug("Skip correlation: No trace ID for log: {}", logEntryId);
            return;
        }

        UUID tenantId = currentLog.getTenantId();

        // Find existing logs with same traceId for the tenant
        List<LogEntry> relatedLogs = logEntryRepository.findByTenantIdAndTraceIdOrderByTimestampAsc(tenantId, traceId);

        // Find if any related log already belongs to an incident
        Optional<LogEntry> logWithIncident = relatedLogs.stream()
                .filter(l -> l.getIncident() != null)
                .findFirst();

        if (logWithIncident.isPresent()) {
            Incident incident = logWithIncident.get().getIncident();
            currentLog.setIncident(incident);
            logEntryRepository.save(currentLog);
            log.info("Linked log {} to existing incident: {}", logEntryId, incident.getId());
        } else {
            // Check if we should create a new incident based on error logs in this trace
            boolean hasError = relatedLogs.stream().anyMatch(l -> "ERROR".equalsIgnoreCase(l.getLevel()));

            if (hasError && relatedLogs.size() > 1) {
                // Auto-create incident for correlated errors
                Incident newIncident = new Incident();
                newIncident.setTenantId(tenantId);
                newIncident.setTitle("Automatically detected incident for Trace: " + traceId);
                newIncident.setDescription("Correlated logs for trace " + traceId + " contained errors.");
                newIncident.setSeverity(Incident.IncidentSeverity.HIGH);
                newIncident.setStatus(Incident.IncidentStatus.OPEN);
                newIncident.setStartedAt(LocalDateTime.now());
                newIncident.setRootCauseService(
                        currentLog.getService() != null ? currentLog.getService().getName() : "Unknown");
                newIncident.setTraceId(traceId);

                Incident savedIncident = incidentRepository.save(newIncident);

                // Link all logs in this trace to the new incident
                for (LogEntry l : relatedLogs) {
                    l.setIncident(savedIncident);
                }
                currentLog.setIncident(savedIncident);
                logEntryRepository.saveAll(relatedLogs);
                logEntryRepository.save(currentLog);

                log.info("Created new incident {} for correlated trace: {}", savedIncident.getId(), traceId);
            }
        }
    }
}
