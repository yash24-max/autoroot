package com.autoroot.common.config;

import com.autoroot.common.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor to extract tenant information from request headers.
 */
@Component
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_SLUG_HEADER = "X-Tenant-Slug";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
                           @NonNull HttpServletResponse response, 
                           @NonNull Object handler) {
        // Extract tenant ID from header
        String tenantIdHeader = request.getHeader(TENANT_HEADER);
        try {
            if (tenantIdHeader != null && !tenantIdHeader.trim().isEmpty()) {
                UUID tenantId = UUID.fromString(tenantIdHeader);
                TenantContext.setTenantId(tenantId);
            }

            // Extract tenant slug from header
            String tenantSlugHeader = request.getHeader(TENANT_SLUG_HEADER);
            if (tenantSlugHeader != null && !tenantSlugHeader.trim().isEmpty()) {
                TenantContext.setTenantSlug(tenantSlugHeader);
            }

            log.debug("Tenant context set: tenantId={}, tenantSlug={}", 
                TenantContext.getTenantId(), TenantContext.getTenantSlug());

            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid tenant ID format: {}", tenantIdHeader, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, 
                              @NonNull HttpServletResponse response, 
                              @NonNull Object handler, 
                              Exception ex) {
        // Clear tenant context after request completion
        TenantContext.clear();
        log.debug("Tenant context cleared");
    }
}