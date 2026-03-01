# AutoRoot Infrastructure

This directory contains the complete Docker Compose infrastructure setup for the AutoRoot project.

## Overview

The infrastructure includes the following services:

- **PostgreSQL 15** - Primary database with optimized configuration
- **Redis 7** - Caching and session storage
- **Apache Kafka** - Message streaming platform
- **Zookeeper** - Kafka coordination service
- **MinIO** - S3-compatible object storage
- **pgAdmin** (dev) - PostgreSQL administration interface
- **Kafka UI** (dev) - Kafka management interface
- **Redis Commander** (dev) - Redis management interface

## Quick Start

1. **Setup Infrastructure:**
   ```bash
   cd /path/to/autoroot
   ./scripts/setup.sh
   ```

2. **Start with Development Tools:**
   ```bash
   ./scripts/setup.sh --dev
   ```

## Manual Setup

If you prefer manual setup or need to troubleshoot:

1. **Copy Environment File:**
   ```bash
   cp infrastructure/.env .env
   # Edit .env with your specific configuration
   ```

2. **Start Core Services:**
   ```bash
   docker-compose up -d postgres redis zookeeper kafka minio
   ```

3. **Start Development Tools (Optional):**
   ```bash
   docker-compose --profile dev up -d
   ```

## Service Details

### PostgreSQL Database
- **Port:** 5432
- **Database:** autoroot
- **Username:** autoroot (configurable)
- **Password:** Set in .env file
- **Features:**
  - Optimized configuration for performance
  - Pre-configured with AutoRoot schema
  - Sample data included
  - Health checks enabled
  - Custom extensions (uuid-ossp, pg_trgm)

### Redis Cache
- **Port:** 6379
- **Password:** Set in .env file
- **Features:**
  - Persistent data storage
  - Password authentication
  - Health checks enabled

### Kafka Message Broker
- **Port:** 9092
- **Zookeeper Port:** 2181
- **Features:**
  - Single broker setup (suitable for development)
  - Auto-topic creation enabled
  - Pre-configured topics: incidents, logs, metrics, health-checks, notifications
  - Health checks enabled

### MinIO Object Storage
- **API Port:** 9000
- **Console Port:** 9001
- **Access Key:** Set in .env file
- **Secret Key:** Set in .env file
- **Features:**
  - S3-compatible API
  - Web console interface
  - Pre-configured buckets: logs, backups, exports, uploads
  - Health checks enabled

## Database Schema

The PostgreSQL database includes the following main tables:

- `tenants` - Multi-tenant support
- `services` - Monitored services
- `incidents` - Service incidents tracking
- `log_entries` - Application and system logs
- `health_checks` - Health check results
- `metrics` - Performance and business metrics

### Sample Queries

```sql
-- View service health summary
SELECT * FROM service_health_summary;

-- View recent incidents
SELECT * FROM recent_incidents;

-- Get service metrics for the last 24 hours
SELECT * FROM get_service_metrics('service-uuid', 24);

-- Clean up old logs (older than 30 days)
SELECT cleanup_old_logs(30);
```

## Development Tools

When started with `--profile dev`, additional management interfaces are available:

### pgAdmin
- **URL:** http://localhost:5050
- **Email:** admin@autoroot.dev (configurable)
- **Password:** Set in .env file
- **Features:**
  - Pre-configured AutoRoot database connection
  - Query tool and database browser
  - Performance monitoring

### Kafka UI
- **URL:** http://localhost:8080
- **Features:**
  - Topic management
  - Message browsing
  - Consumer group monitoring
  - Cluster health monitoring

### Redis Commander
- **URL:** http://localhost:8081
- **Features:**
  - Key browsing and editing
  - Memory usage monitoring
  - Command-line interface

## Configuration

### Environment Variables

Key environment variables (see `infrastructure/.env` for complete list):

```env
# PostgreSQL
POSTGRES_USER=autoroot
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=autoroot

# Redis
REDIS_PASSWORD=your_redis_password

# MinIO
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=your_minio_password

# Development Tools
PGADMIN_EMAIL=admin@autoroot.dev
PGADMIN_PASSWORD=your_pgadmin_password
```

### Custom Configuration Files

- `postgres/postgres.conf` - PostgreSQL server configuration
- `postgres/pg_hba.conf` - PostgreSQL authentication configuration
- `postgres/init.sql` - Database initialization script
- `pgadmin/servers.json` - pgAdmin server pre-configuration

## Networking

All services run on a custom Docker network (`autoroot-network`) with the subnet `172.20.0.0/16`. This ensures:

- Service-to-service communication via service names
- Isolation from other Docker projects
- Consistent IP addressing

## Data Persistence

The following volumes are used for data persistence:

- `postgres_data` - PostgreSQL database files
- `redis_data` - Redis data files
- `kafka_data` - Kafka log segments
- `zookeeper_data` - Zookeeper data
- `minio_data` - MinIO object storage
- `pgadmin_data` - pgAdmin settings (dev only)

## Health Checks

All services include comprehensive health checks:

- **PostgreSQL:** Connection test and schema validation
- **Redis:** Basic ping test
- **Kafka:** Broker API availability
- **Zookeeper:** Port connectivity
- **MinIO:** HTTP health endpoint

## Security Considerations

### Development Setup
- Default passwords are included for development convenience
- Services are accessible via localhost ports
- No SSL/TLS encryption configured

### Production Recommendations
1. **Change all default passwords** to strong, randomly generated values
2. **Enable SSL/TLS** for all services
3. **Restrict network access** using firewall rules
4. **Use secrets management** instead of environment variables
5. **Enable audit logging** for all services
6. **Regular security updates** of Docker images
7. **Backup and disaster recovery** procedures

## Backup and Recovery

### PostgreSQL Backup
```bash
# Create backup
docker-compose exec postgres pg_dump -U autoroot autoroot > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U autoroot autoroot < backup.sql
```

### Redis Backup
```bash
# Redis automatically creates snapshots in the volume
# To create manual backup:
docker-compose exec redis redis-cli --rdb /data/backup.rdb
```

### MinIO Backup
```bash
# Use MinIO client to sync buckets
mc mirror autoroot-local/logs s3://backup-bucket/logs
```

## Monitoring and Logging

### Service Logs
```bash
# View logs for all services
docker-compose logs

# View logs for specific service
docker-compose logs postgres
docker-compose logs kafka

# Follow logs in real-time
docker-compose logs -f
```

### Resource Monitoring
```bash
# View resource usage
docker stats

# View service status
docker-compose ps
```

## Troubleshooting

### Common Issues

1. **Services won't start:**
   ```bash
   # Check Docker is running
   docker info
   
   # Check port availability
   netstat -tulpn | grep :5432
   
   # Check logs for errors
   docker-compose logs postgres
   ```

2. **Database connection errors:**
   ```bash
   # Test database connectivity
   docker-compose exec postgres pg_isready -U autoroot
   
   # Connect to database directly
   docker-compose exec postgres psql -U autoroot -d autoroot
   ```

3. **Kafka connection issues:**
   ```bash
   # Check Kafka broker status
   docker-compose exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092
   
   # List topics
   docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
   ```

4. **MinIO access issues:**
   ```bash
   # Check MinIO health
   curl http://localhost:9000/minio/health/live
   
   # Test with MinIO client
   mc ls autoroot-local
   ```

### Performance Tuning

1. **Increase PostgreSQL memory settings** in `postgres/postgres.conf`
2. **Adjust Kafka retention policies** based on your needs
3. **Configure Redis memory limits** for your use case
4. **Monitor disk space** for all persistent volumes

### Recovery Procedures

1. **Complete reset:**
   ```bash
   docker-compose down -v
   docker system prune -f
   ./scripts/setup.sh
   ```

2. **Reset specific service:**
   ```bash
   docker-compose stop postgres
   docker volume rm autoroot_postgres_data
   docker-compose up -d postgres
   ```

## Contributing

When adding new services or modifying the infrastructure:

1. Update this README with new service information
2. Add appropriate health checks
3. Include the service in the setup script
4. Add environment variables to `.env` template
5. Test the complete setup from scratch

## Support

For infrastructure-related issues:

1. Check the troubleshooting section above
2. Review service logs: `docker-compose logs [service]`
3. Verify environment configuration
4. Check Docker and system resources

## Version Information

- PostgreSQL: 15-alpine
- Redis: 7-alpine  
- Kafka: 7.4.0 (Confluent Platform)
- Zookeeper: 7.4.0 (Confluent Platform)
- MinIO: Latest
- pgAdmin: Latest
- Kafka UI: Latest