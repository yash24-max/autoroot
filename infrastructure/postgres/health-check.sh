#!/bin/bash
# PostgreSQL Health Check Script for AutoRoot

set -e

# Default values
POSTGRES_USER=${POSTGRES_USER:-autoroot}
POSTGRES_DB=${POSTGRES_DB:-autoroot}

# Check if PostgreSQL is accepting connections
pg_isready -h localhost -p 5432 -U "$POSTGRES_USER"

# Check if database exists and is accessible
psql -h localhost -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT 1;" > /dev/null

# Check if required tables exist (basic schema validation)
TABLE_COUNT=$(psql -h localhost -U "$POSTGRES_USER" -d "$POSTGRES_DB" -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name IN ('tenants', 'services', 'incidents', 'log_entries');")

if [ "$TABLE_COUNT" -lt 4 ]; then
    echo "Database schema not fully initialized"
    exit 1
fi

echo "PostgreSQL health check passed"
exit 0