package com.autoroot.deployment.controller;

import com.autoroot.common.entity.DeploymentHistory;
import com.autoroot.common.entity.Service;
import com.autoroot.common.repository.DeploymentHistoryRepository;
import com.autoroot.common.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for receiving deployment notifications from CI/CD pipelines.
 */
@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
@Slf4j
public class DeploymentController {

    private final DeploymentHistoryRepository deploymentHistoryRepository;
    private final ServiceRepository serviceRepository;

    @PostMapping("/notify")
    public ResponseEntity<?> notifyDeployment(@RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, String> request) {
        String serviceName = request.get("serviceName");
        String version = request.get("version");
        String environment = request.getOrDefault("environment", "production");

        log.info("Received deployment notification for service: {} version: {}", serviceName, version);

        Service service = serviceRepository.findByTenantIdAndName(tenantId, serviceName)
                .orElseGet(() -> {
                    Service newService = new Service();
                    newService.setTenantId(tenantId);
                    newService.setName(serviceName);
                    return serviceRepository.save(newService);
                });

        DeploymentHistory history = new DeploymentHistory();
        history.setTenantId(tenantId);
        history.setService(service);
        history.setDeploymentVersion(version);
        history.setEnvironment(environment);
        history.setStatus("SUCCESS");
        history.setDeployedAt(LocalDateTime.now());

        deploymentHistoryRepository.save(history);

        // Update service version
        service.setServiceVersion(version);
        service.setLastSeen(LocalDateTime.now());
        serviceRepository.save(service);

        return ResponseEntity.ok(Map.of("message", "Deployment recorded successfully", "id", history.getId()));
    }
}
