-- AutoRoot Database Initialization Script
-- This script creates the initial database schema and sample data

-- Ensure we're using the autoroot database
\c autoroot;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pg_trgm extension for text search
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Create custom types
CREATE TYPE incident_status AS ENUM ('open', 'investigating', 'resolved', 'closed');
CREATE TYPE incident_severity AS ENUM ('low', 'medium', 'high', 'critical');
CREATE TYPE service_status AS ENUM ('healthy', 'degraded', 'down', 'maintenance');

-- Tenants table - Multi-tenant support
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    domain VARCHAR(100),
    settings JSONB DEFAULT '{}',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Services table - Services being monitored
CREATE TABLE services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    url VARCHAR(255),
    status service_status DEFAULT 'healthy',
    health_check_url VARCHAR(255),
    health_check_interval INTEGER DEFAULT 300, -- seconds
    metadata JSONB DEFAULT '{}',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(tenant_id, name)
);

-- Incidents table - Track service incidents
CREATE TABLE incidents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    service_id UUID REFERENCES services(id) ON DELETE SET NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status incident_status DEFAULT 'open',
    severity incident_severity DEFAULT 'medium',
    started_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    resolved_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB DEFAULT '{}',
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Log entries table - System and application logs
CREATE TABLE log_entries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    service_id UUID REFERENCES services(id) ON DELETE SET NULL,
    incident_id UUID REFERENCES incidents(id) ON DELETE SET NULL,
    level VARCHAR(10) NOT NULL, -- DEBUG, INFO, WARN, ERROR, FATAL
    message TEXT NOT NULL,
    source VARCHAR(100), -- application, system, external
    component VARCHAR(100), -- specific component that generated the log
    metadata JSONB DEFAULT '{}',
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Health check results table
CREATE TABLE health_checks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    service_id UUID NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL, -- success, failure, timeout
    response_time_ms INTEGER,
    status_code INTEGER,
    error_message TEXT,
    checked_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    metadata JSONB DEFAULT '{}'
);

-- Metrics table for storing various metrics
CREATE TABLE metrics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    service_id UUID REFERENCES services(id) ON DELETE SET NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value NUMERIC NOT NULL,
    unit VARCHAR(20),
    tags JSONB DEFAULT '{}',
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for performance
-- Tenants indexes
CREATE INDEX idx_tenants_slug ON tenants(slug);
CREATE INDEX idx_tenants_domain ON tenants(domain);
CREATE INDEX idx_tenants_active ON tenants(is_active) WHERE is_active = true;

-- Services indexes
CREATE INDEX idx_services_tenant_id ON services(tenant_id);
CREATE INDEX idx_services_status ON services(status);
CREATE INDEX idx_services_active ON services(is_active) WHERE is_active = true;
CREATE INDEX idx_services_tenant_name ON services(tenant_id, name);

-- Incidents indexes
CREATE INDEX idx_incidents_tenant_id ON incidents(tenant_id);
CREATE INDEX idx_incidents_service_id ON incidents(service_id);
CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_severity ON incidents(severity);
CREATE INDEX idx_incidents_started_at ON incidents(started_at DESC);
CREATE INDEX idx_incidents_resolved_at ON incidents(resolved_at DESC);

-- Log entries indexes
CREATE INDEX idx_log_entries_tenant_id ON log_entries(tenant_id);
CREATE INDEX idx_log_entries_service_id ON log_entries(service_id);
CREATE INDEX idx_log_entries_incident_id ON log_entries(incident_id);
CREATE INDEX idx_log_entries_level ON log_entries(level);
CREATE INDEX idx_log_entries_timestamp ON log_entries(timestamp DESC);
CREATE INDEX idx_log_entries_source ON log_entries(source);
CREATE INDEX idx_log_entries_component ON log_entries(component);
-- GIN index for JSON metadata search
CREATE INDEX idx_log_entries_metadata_gin ON log_entries USING GIN (metadata);
-- Text search index
CREATE INDEX idx_log_entries_message_trgm ON log_entries USING GIN (message gin_trgm_ops);

-- Health checks indexes
CREATE INDEX idx_health_checks_service_id ON health_checks(service_id);
CREATE INDEX idx_health_checks_status ON health_checks(status);
CREATE INDEX idx_health_checks_checked_at ON health_checks(checked_at DESC);

-- Metrics indexes
CREATE INDEX idx_metrics_tenant_id ON metrics(tenant_id);
CREATE INDEX idx_metrics_service_id ON metrics(service_id);
CREATE INDEX idx_metrics_name ON metrics(metric_name);
CREATE INDEX idx_metrics_timestamp ON metrics(timestamp DESC);
CREATE INDEX idx_metrics_tags_gin ON metrics USING GIN (tags);

-- Create update trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add updated_at triggers
CREATE TRIGGER update_tenants_updated_at BEFORE UPDATE ON tenants
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_services_updated_at BEFORE UPDATE ON services
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_incidents_updated_at BEFORE UPDATE ON incidents
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample tenant data
INSERT INTO tenants (name, slug, domain, settings) VALUES 
    ('AutoRoot Demo', 'autoroot-demo', 'demo.autoroot.dev', '{"features": ["monitoring", "logging", "alerting"], "plan": "enterprise"}'),
    ('ACME Corp', 'acme-corp', 'acme.example.com', '{"features": ["monitoring", "logging"], "plan": "professional"}'),
    ('TechStart Inc', 'techstart-inc', 'techstart.example.com', '{"features": ["monitoring"], "plan": "basic"}');

-- Get tenant IDs for sample data
DO $$
DECLARE
    demo_tenant_id UUID;
    acme_tenant_id UUID;
    techstart_tenant_id UUID;
BEGIN
    SELECT id INTO demo_tenant_id FROM tenants WHERE slug = 'autoroot-demo';
    SELECT id INTO acme_tenant_id FROM tenants WHERE slug = 'acme-corp';
    SELECT id INTO techstart_tenant_id FROM tenants WHERE slug = 'techstart-inc';

    -- Insert sample services
    INSERT INTO services (tenant_id, name, description, url, status, health_check_url, health_check_interval) VALUES 
        (demo_tenant_id, 'Web Application', 'Main web application frontend', 'https://app.autoroot.dev', 'healthy', 'https://app.autoroot.dev/health', 60),
        (demo_tenant_id, 'API Gateway', 'Main API gateway service', 'https://api.autoroot.dev', 'healthy', 'https://api.autoroot.dev/health', 30),
        (demo_tenant_id, 'User Service', 'User management microservice', 'https://users.autoroot.dev', 'degraded', 'https://users.autoroot.dev/health', 60),
        (demo_tenant_id, 'Payment Service', 'Payment processing service', 'https://payments.autoroot.dev', 'healthy', 'https://payments.autoroot.dev/health', 30),
        
        (acme_tenant_id, 'ACME Portal', 'Customer portal application', 'https://portal.acme.example.com', 'healthy', 'https://portal.acme.example.com/health', 120),
        (acme_tenant_id, 'ACME API', 'Main API service', 'https://api.acme.example.com', 'healthy', 'https://api.acme.example.com/health', 60),
        
        (techstart_tenant_id, 'TechStart App', 'Main application', 'https://app.techstart.example.com', 'down', 'https://app.techstart.example.com/health', 300);

    -- Insert sample incidents
    INSERT INTO incidents (tenant_id, service_id, title, description, status, severity, started_at, resolved_at, created_by) 
    SELECT 
        s.tenant_id,
        s.id,
        'Degraded Performance in ' || s.name,
        'Users reporting slow response times in the ' || s.name || ' service. Investigating database performance issues.',
        'investigating',
        'medium',
        NOW() - INTERVAL '2 hours',
        NULL,
        'system@autoroot.dev'
    FROM services s 
    WHERE s.status = 'degraded'
    LIMIT 1;

    INSERT INTO incidents (tenant_id, service_id, title, description, status, severity, started_at, resolved_at, created_by) 
    SELECT 
        s.tenant_id,
        s.id,
        s.name || ' Service Outage',
        'Complete service outage detected. Service is returning 503 errors. Emergency response team has been notified.',
        'open',
        'critical',
        NOW() - INTERVAL '30 minutes',
        NULL,
        'alerts@autoroot.dev'
    FROM services s 
    WHERE s.status = 'down'
    LIMIT 1;

    -- Insert sample log entries
    INSERT INTO log_entries (tenant_id, service_id, level, message, source, component, metadata) 
    SELECT 
        s.tenant_id,
        s.id,
        'ERROR',
        'Database connection timeout after 30 seconds',
        'application',
        'database-connector',
        '{"query": "SELECT * FROM users WHERE active = true", "timeout_ms": 30000, "connection_pool": "primary"}'
    FROM services s 
    WHERE s.name LIKE '%User%'
    LIMIT 1;

    INSERT INTO log_entries (tenant_id, service_id, level, message, source, component, metadata) 
    SELECT 
        s.tenant_id,
        s.id,
        'WARN',
        'High memory usage detected: 85% of available memory in use',
        'system',
        'memory-monitor',
        '{"memory_used_bytes": 7340032000, "memory_total_bytes": 8589934592, "memory_percent": 85.4}'
    FROM services s 
    WHERE s.status = 'healthy'
    LIMIT 3;

    INSERT INTO log_entries (tenant_id, service_id, level, message, source, component, metadata) 
    SELECT 
        s.tenant_id,
        s.id,
        'INFO',
        'Service health check completed successfully',
        'system',
        'health-monitor',
        '{"response_time_ms": 45, "status_code": 200, "endpoint": "/health"}'
    FROM services s 
    WHERE s.status = 'healthy';
END $$;

-- Create views for common queries
CREATE VIEW service_health_summary AS
SELECT 
    t.name as tenant_name,
    s.name as service_name,
    s.status,
    s.url,
    s.health_check_interval,
    s.updated_at as last_updated,
    COUNT(i.id) as open_incidents
FROM services s
JOIN tenants t ON s.tenant_id = t.id
LEFT JOIN incidents i ON s.id = i.service_id AND i.status IN ('open', 'investigating')
WHERE s.is_active = true AND t.is_active = true
GROUP BY t.name, s.name, s.status, s.url, s.health_check_interval, s.updated_at
ORDER BY t.name, s.name;

CREATE VIEW recent_incidents AS
SELECT 
    t.name as tenant_name,
    s.name as service_name,
    i.title,
    i.status,
    i.severity,
    i.started_at,
    i.resolved_at,
    CASE 
        WHEN i.resolved_at IS NOT NULL THEN 
            EXTRACT(EPOCH FROM (i.resolved_at - i.started_at))/3600
        ELSE 
            EXTRACT(EPOCH FROM (NOW() - i.started_at))/3600
    END as duration_hours
FROM incidents i
JOIN tenants t ON i.tenant_id = t.id
LEFT JOIN services s ON i.service_id = s.id
WHERE i.started_at > NOW() - INTERVAL '7 days'
ORDER BY i.started_at DESC;

-- Create a function to clean up old log entries
CREATE OR REPLACE FUNCTION cleanup_old_logs(days_to_keep INTEGER DEFAULT 30)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM log_entries 
    WHERE created_at < NOW() - (days_to_keep || ' days')::INTERVAL;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Create a function to get service metrics
CREATE OR REPLACE FUNCTION get_service_metrics(service_uuid UUID, hours_back INTEGER DEFAULT 24)
RETURNS TABLE(
    metric_name VARCHAR(100),
    avg_value NUMERIC,
    min_value NUMERIC,
    max_value NUMERIC,
    count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        m.metric_name,
        AVG(m.metric_value)::NUMERIC as avg_value,
        MIN(m.metric_value)::NUMERIC as min_value,
        MAX(m.metric_value)::NUMERIC as max_value,
        COUNT(*)::BIGINT as count
    FROM metrics m
    WHERE m.service_id = service_uuid 
    AND m.timestamp > NOW() - (hours_back || ' hours')::INTERVAL
    GROUP BY m.metric_name
    ORDER BY m.metric_name;
END;
$$ LANGUAGE plpgsql;

-- Grant permissions (adjust as needed for your security requirements)
-- These are basic permissions for development. In production, create specific roles with limited permissions.

-- Create application user (optional - for development)
-- DO $$ 
-- BEGIN
--     IF NOT EXISTS (SELECT FROM pg_catalog.pg_user WHERE usename = 'autoroot_app') THEN
--         CREATE USER autoroot_app WITH PASSWORD 'app_password_change_me';
--     END IF;
-- END $$;

-- GRANT CONNECT ON DATABASE autoroot TO autoroot_app;
-- GRANT USAGE ON SCHEMA public TO autoroot_app;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO autoroot_app;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO autoroot_app;

-- Final status message
DO $$
BEGIN
    RAISE NOTICE 'AutoRoot database initialization completed successfully!';
    RAISE NOTICE 'Created tables: tenants, services, incidents, log_entries, health_checks, metrics';
    RAISE NOTICE 'Created views: service_health_summary, recent_incidents';
    RAISE NOTICE 'Created functions: cleanup_old_logs, get_service_metrics';
    RAISE NOTICE 'Inserted sample data for 3 tenants with services and incidents';
END $$;