package com.autoroot.gateway.controller;

import com.autoroot.incident.service.IncidentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Incident management.
 */
@RestController
@RequestMapping("/api/v1/incidents")
@Slf4j
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<IncidentDto>>> getAllIncidents(Pageable pageable) {
        log.debug("Getting all incidents with pagination: {}", pageable);
        Page<IncidentDto> incidents = incidentService.getAllIncidents(pageable);
        return ResponseEntity.ok(ApiResponse.success(incidents, "Incidents retrieved successfully"));
    }

    @GetMapping("/{incidentId}")
    public ResponseEntity<ApiResponse<IncidentDto>> getIncident(@PathVariable UUID incidentId) {
        log.debug("Getting incident: {}", incidentId);
        IncidentDto incident = incidentService.getIncidentById(incidentId);
        return ResponseEntity.ok(ApiResponse.success(incident, "Incident retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IncidentDto>> createIncident(@Valid @RequestBody IncidentDto incidentDto) {
        log.debug("Creating incident: {}", incidentDto.getTitle());
        IncidentDto createdIncident = incidentService.createIncident(incidentDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdIncident, "Incident created successfully"));
    }

    @PutMapping("/{incidentId}")
    public ResponseEntity<ApiResponse<IncidentDto>> updateIncident(
            @PathVariable UUID incidentId,
            @Valid @RequestBody IncidentDto incidentDto) {
        log.debug("Updating incident: {}", incidentId);
        IncidentDto updatedIncident = incidentService.updateIncident(incidentId, incidentDto);
        return ResponseEntity.ok(ApiResponse.success(updatedIncident, "Incident updated successfully"));
    }

    @DeleteMapping("/{incidentId}")
    public ResponseEntity<ApiResponse<Void>> deleteIncident(@PathVariable UUID incidentId) {
        log.debug("Deleting incident: {}", incidentId);
        incidentService.deleteIncident(incidentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Incident deleted successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<IncidentDto>>> getIncidentsByStatus(@PathVariable String status) {
        log.debug("Getting incidents by status: {}", status);

        try {
            Incident.IncidentStatus incidentStatus = Incident.IncidentStatus.valueOf(status.toUpperCase());
            List<IncidentDto> incidents = incidentService.getIncidentsByStatus(incidentStatus);
            return ResponseEntity.ok(ApiResponse.success(incidents, "Incidents retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid incident status: " + status, "INVALID_STATUS"));
        }
    }

    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<IncidentDto>>> getOpenIncidents() {
        log.debug("Getting open incidents");
        List<IncidentDto> openIncidents = incidentService.getOpenIncidents();
        return ResponseEntity.ok(ApiResponse.success(openIncidents, "Open incidents retrieved successfully"));
    }
}