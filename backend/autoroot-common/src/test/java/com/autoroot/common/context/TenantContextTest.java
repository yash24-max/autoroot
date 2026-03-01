package com.autoroot.common.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TenantContext.
 */
class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldSetAndGetTenantId() {
        // Given
        UUID tenantId = UUID.randomUUID();

        // When
        TenantContext.setTenantId(tenantId);

        // Then
        assertEquals(tenantId, TenantContext.getTenantId());
        assertTrue(TenantContext.isSet());
    }

    @Test
    void shouldSetAndGetTenantSlug() {
        // Given
        String tenantSlug = "test-tenant";

        // When
        TenantContext.setTenantSlug(tenantSlug);

        // Then
        assertEquals(tenantSlug, TenantContext.getTenantSlug());
    }

    @Test
    void shouldClearContext() {
        // Given
        UUID tenantId = UUID.randomUUID();
        String tenantSlug = "test-tenant";
        TenantContext.setTenantId(tenantId);
        TenantContext.setTenantSlug(tenantSlug);

        // When
        TenantContext.clear();

        // Then
        assertNull(TenantContext.getTenantId());
        assertNull(TenantContext.getTenantSlug());
        assertFalse(TenantContext.isSet());
    }

    @Test
    void shouldReturnNullWhenNotSet() {
        // Given - no tenant context set

        // When & Then
        assertNull(TenantContext.getTenantId());
        assertNull(TenantContext.getTenantSlug());
        assertFalse(TenantContext.isSet());
    }
}