package com.autoroot.common.context;

import java.util.UUID;

/**
 * Tenant context holder for multi-tenant support.
 * Uses ThreadLocal to maintain tenant information per request.
 */
public class TenantContext {

    private static final ThreadLocal<UUID> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_SLUG = new ThreadLocal<>();

    private TenantContext() {
        // Utility class
    }

    /**
     * Set the current tenant ID for this thread.
     *
     * @param tenantId the tenant ID to set
     */
    public static void setTenantId(UUID tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * Get the current tenant ID for this thread.
     *
     * @return the current tenant ID, or null if not set
     */
    public static UUID getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * Set the current tenant slug for this thread.
     *
     * @param tenantSlug the tenant slug to set
     */
    public static void setTenantSlug(String tenantSlug) {
        TENANT_SLUG.set(tenantSlug);
    }

    /**
     * Get the current tenant slug for this thread.
     *
     * @return the current tenant slug, or null if not set
     */
    public static String getTenantSlug() {
        return TENANT_SLUG.get();
    }

    /**
     * Clear the tenant context for this thread.
     * Should be called at the end of request processing.
     */
    public static void clear() {
        TENANT_ID.remove();
        TENANT_SLUG.remove();
    }

    /**
     * Check if tenant context is set for current thread.
     *
     * @return true if tenant ID is set, false otherwise
     */
    public static boolean isSet() {
        return TENANT_ID.get() != null;
    }
}