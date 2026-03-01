package com.autoroot.incident.service;

import com.autoroot.common.context.TenantContext;
import com.autoroot.common.dto.IncidentDto;
import com.autoroot.common.entity.Incident;
import com.autoroot.common.exception.ResourceNotFoundException;
import com.autoroot.common.mapper.IncidentMapper;
import com.autoroot.incident.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing incidents.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;

    /**
     * Get all incidents for the current tenant.
     */
    @Transactional(readOnly = true)
    public Page<IncidentDto> getAllIncidents(Pageable pageable) {
        UUID tenantId = TenantContext.getTenantId();
        log.debug("Getting all incidents for tenant: {}", tenantId);
        
        Page<Incident> incidents = incidentRepository.findByTenantIdOrderByStartedAtDesc(tenantId, pageable);
        return incidents.map(incidentMapper::toDto);
    }

    /**
     * Get incident by ID.
     */
    @Transactional(readOnly = true)
    public IncidentDto getIncidentById(UUID incidentId) {
        UUID tenantId = TenantContext.getTenantId();
        log.debug("Getting incident {} for tenant: {}", incidentId, tenantId);
        
        Incident incident = incidentRepository.findByIdAndTenantId(incidentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", incidentId.toString()));
                
        return incidentMapper.toDto(incident);
    }

    /**
     * Create a new incident.
     */
    public IncidentDto createIncident(IncidentDto incidentDto) {
        UUID tenantId = TenantContext.getTenantId();
        log.debug("Creating incident for tenant: {}", tenantId);
        
        Incident incident = incidentMapper.toEntity(incidentDto);
        incident.setTenantId(tenantId);
        incident.setStartedAt(LocalDateTime.now());
        
        Incident savedIncident = incidentRepository.save(incident);
        log.info("Created incident {} for tenant: {}", savedIncident.getId(), tenantId);
        
        return incidentMapper.toDto(savedIncident);
    }

    /**
     * Update an existing incident.
     */
    public IncidentDto updateIncident(UUID incidentId, IncidentDto incidentDto) {
        UUID tenantId = TenantContext.getTenantId();
        log.debug("Updating incident {} for tenant: {}", incidentId, tenantId);
        
        Incident existingIncident = incidentRepository.findByIdAndTenantId(incidentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", incidentId.toString()));
        
        // Update fields from DTO
        incidentMapper.updateEntityFromDto(incidentDto, existingIncident);
        
        // Handle status changes
        if (incidentDto.getStatus() == Incident.IncidentStatus.RESOLVED && 
            existingIncident.getResolvedAt() == null) {
            existingIncident.setResolvedAt(LocalDateTime.now());
        }
        
        Incident updatedIncident = incidentRepository.save(existingIncident);
        log.info("Updated incident {} for tenant: {}", incidentId, tenantId);
        
        return incidentMapper.toDto(updatedIncident);
    }

    /**
     * Delete an incident.
     */
    public void deleteIncident(UUID incidentId) {
        UUID tenantId = TenantContext.getTenantId();
        log.debug("Deleting incident {} for tenant: {}", incidentId, tenantId);
        
        Incident incident = incidentRepository.findByIdAndTenantId(incidentId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident", incidentId.toString()));
        
        incidentRepository.delete(incident);
        log.info("Deleted incident {} for tenant: {}", incidentId, tenantId);
    }

    /**
     * Get incidents by status.
     */
    @Transactional(readOnly = true)
    public List<IncidentDto> getIncidentsByStatus(Incident.IncidentStatus status) {
        UUID tenantId = TenantContext.getTenantId();
        log.debug("Getting incidents with status {} for tenant: {}", status, tenantId);
        
        List<Incident> incidents = incidentRepository.findByTenantIdAndStatusOrderByStartedAtDesc(tenantId, status);
        return incidents.stream().map(incidentMapper::toDto).toList();
    }

    /**
     * Get open incidents.
     */
    @Transactional(readOnly = true)
    public List<IncidentDto> getOpenIncidents() {
        UUID tenantId = TenantContext.getTenantId();
        log.debug("Getting open incidents for tenant: {}", tenantId);
        
        List<Incident.IncidentStatus> openStatuses = List.of(
            Incident.IncidentStatus.OPEN,
            Incident.IncidentStatus.INVESTIGATING
        );
        
        List<Incident> incidents = incidentRepository.findOpenIncidents(tenantId, openStatuses);
        return incidents.stream().map(incidentMapper::toDto).toList();
    }
}