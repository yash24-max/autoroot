package com.autoroot.deployment.service;

import com.autoroot.common.entity.DeploymentHistory;
import com.autoroot.common.entity.Incident;
import com.autoroot.common.repository.DeploymentHistoryRepository;
import com.autoroot.incident.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for analyzing deployment risk.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentAnalyzerService {

    private final DeploymentHistoryRepository deploymentHistoryRepository;
    private final IncidentRepository incidentRepository;

    /**
     * Analyze if an incident is likely caused by a recent deployment.
     * 
     * @param incidentId The ID of the incident to analyze.
     */
    public void analyzeIncidentRisk(UUID incidentId) {
        log.info("Analyzing deployment risk for incident: {}", incidentId);

        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        if (incident.getService() == null) {
            log.debug("Skip risk analysis: Incident has no associated service.");
            return;
        }

        UUID tenantId = incident.getTenantId();
        UUID serviceId = incident.getService().getId();
        LocalDateTime incidentTime = incident.getStartedAt();

        // Check for deployments in the last 1 hour before the incident
        LocalDateTime checkWindowStart = incidentTime.minusHours(1);
        List<DeploymentHistory> recentDeployments = deploymentHistoryRepository
                .findByTenantIdAndServiceIdAndDeployedAtAfter(tenantId, serviceId, checkWindowStart);

        if (!recentDeployments.isEmpty()) {
            DeploymentHistory latestDeployment = recentDeployments.get(recentDeployments.size() - 1);
            log.warn("🚨 Deployment Risk Detected! Incident {} occurred after deployment {} of service {}.",
                    incidentId, latestDeployment.getDeploymentVersion(), incident.getService().getName());

            // Mark the incident as potentially deployment-related in metadata
            String metadata = incident.getMetadata() != null ? incident.getMetadata() : "{}";
            // Simple string manipulation for demo purposes, could use a proper JSON library
            metadata = metadata.replace("}", String.format(
                    ", \"is_deployment_related\": true, \"deployment_id\": \"%s\", \"deployed_version\": \"%s\"}",
                    latestDeployment.getId(), latestDeployment.getDeploymentVersion()));
            incident.setMetadata(metadata);
            incidentRepository.save(incident);
        }
    }
}
