package com.autoroot.common.exception;

/**
 * Exception thrown when tenant is not found.
 */
public class TenantNotFoundException extends AutoRootException {

    public TenantNotFoundException(String tenantId) {
        super("Tenant not found: " + tenantId, "TENANT_NOT_FOUND");
    }

    public TenantNotFoundException(String tenantId, Throwable cause) {
        super("Tenant not found: " + tenantId, "TENANT_NOT_FOUND", cause);
    }
}