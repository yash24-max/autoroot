package com.autoroot.gateway.controller;

import com.autoroot.common.dto.ApiResponse;
import com.autoroot.common.dto.ServiceDto;
import com.autoroot.common.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Service management.
 */
@RestController
@RequestMapping("/api/v1/services")
@Slf4j
public class ServiceController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getAllServices() {
        log.debug("Getting all services for tenant: {}", TenantContext.getTenantId());
        
        // TODO: Implement service retrieval logic
        List<ServiceDto> services = List.of(); // Placeholder
        
        return ResponseEntity.ok(ApiResponse.success(services, "Services retrieved successfully"));
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceDto>> getService(@PathVariable UUID serviceId) {
        log.debug("Getting service {} for tenant: {}", serviceId, TenantContext.getTenantId());
        
        // TODO: Implement service retrieval logic
        ServiceDto service = new ServiceDto(); // Placeholder
        service.setId(serviceId);
        service.setName("Sample Service");
        
        return ResponseEntity.ok(ApiResponse.success(service, "Service retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceDto>> createService(@Valid @RequestBody ServiceDto serviceDto) {
        log.debug("Creating service for tenant: {}", TenantContext.getTenantId());
        
        // TODO: Implement service creation logic
        serviceDto.setId(UUID.randomUUID());
        serviceDto.setTenantId(TenantContext.getTenantId());
        
        return ResponseEntity.ok(ApiResponse.success(serviceDto, "Service created successfully"));
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceDto>> updateService(
            @PathVariable UUID serviceId, 
            @Valid @RequestBody ServiceDto serviceDto) {
        log.debug("Updating service {} for tenant: {}", serviceId, TenantContext.getTenantId());
        
        // TODO: Implement service update logic
        serviceDto.setId(serviceId);
        serviceDto.setTenantId(TenantContext.getTenantId());
        
        return ResponseEntity.ok(ApiResponse.success(serviceDto, "Service updated successfully"));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable UUID serviceId) {
        log.debug("Deleting service {} for tenant: {}", serviceId, TenantContext.getTenantId());
        
        // TODO: Implement service deletion logic
        
        return ResponseEntity.ok(ApiResponse.success(null, "Service deleted successfully"));
    }
}