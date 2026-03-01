# AutoRoot Backend - Spring Boot Multi-Module Application

AutoRoot is an AI-powered API observability platform built with Spring Boot 3.2 and Java 21, featuring multi-tenancy, Kafka integration, and comprehensive monitoring capabilities.

## 🏗 Architecture

This is a multi-module Maven project with the following structure:

```
backend/
├── pom.xml                     # Parent POM with dependency management
├── autoroot-common/            # Shared entities, DTOs, configurations
├── autoroot-api-gateway/       # Main REST API + WebSocket gateway
├── autoroot-incident-service/  # Incident management service
├── autoroot-log-processor/     # Kafka log processing
├── autoroot-correlation-engine/# Log correlation logic
├── autoroot-ai-engine/         # OpenAI integration
├── autoroot-alert-service/     # Alerting and notifications
└── autoroot-deployment-analyzer/ # CI/CD integration
```

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.2
- **Language**: Java 21
- **Database**: PostgreSQL with JPA/Hibernate
- **Caching**: Redis
- **Messaging**: Apache Kafka
- **Security**: Spring Security
- **Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, TestContainers, Mockito
- **Build**: Maven

## 🎯 Key Features

### Multi-Tenancy
- Row-level security with `tenant_id` column
- Automatic tenant context management
- Per-request tenant isolation

### Core Modules
- **Common Module**: Shared entities, DTOs, and configurations
- **API Gateway**: REST endpoints, WebSocket support, security
- **Incident Service**: CRUD operations for incident management
- **Health Monitoring**: Comprehensive health checks and metrics

### Database Schema
- Multi-tenant entities (tenants, services, incidents, log_entries)
- PostgreSQL with proper indexing for performance
- Audit trail with created/updated timestamps

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL, Redis, Kafka (via Docker Compose)

### 1. Start Infrastructure

From the project root:

```bash
# Start all infrastructure services
docker-compose up -d

# Verify services are running
docker-compose ps
```

This starts:
- PostgreSQL (port 5432)
- Redis (port 6379)
- Kafka + Zookeeper (ports 9092, 2181)
- MinIO (port 9000)
- pgAdmin (port 5050)
- Kafka UI (port 8080)

### 2. Build the Application

```bash
# From the backend directory
cd backend

# Clean and compile all modules
mvn clean compile

# Run tests
mvn test

# Package all modules
mvn clean package

# Install modules to local repository
mvn clean install
```

### 3. Run the API Gateway

```bash
# Run the main application
cd autoroot-api-gateway
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on http://localhost:8080

### 4. Verify Installation

- Health Check: http://localhost:8080/api/v1/health
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator: http://localhost:8080/actuator/health

## 📡 API Endpoints

### Health & Monitoring
- `GET /api/v1/health` - Application health status
- `GET /api/v1/health/ready` - Readiness probe
- `GET /api/v1/health/live` - Liveness probe
- `GET /actuator/health` - Spring Actuator health

### Services Management
- `GET /api/v1/services` - List all services
- `GET /api/v1/services/{id}` - Get service by ID
- `POST /api/v1/services` - Create new service
- `PUT /api/v1/services/{id}` - Update service
- `DELETE /api/v1/services/{id}` - Delete service

### Incident Management
- `GET /api/v1/incidents` - List all incidents (paginated)
- `GET /api/v1/incidents/{id}` - Get incident by ID
- `POST /api/v1/incidents` - Create new incident
- `PUT /api/v1/incidents/{id}` - Update incident
- `DELETE /api/v1/incidents/{id}` - Delete incident
- `GET /api/v1/incidents/status/{status}` - Get incidents by status
- `GET /api/v1/incidents/open` - Get open incidents

## 🔧 Configuration

### Application Properties

Key configuration files:
- `autoroot-api-gateway/src/main/resources/application.yml`

### Environment Variables

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/autoroot
DATABASE_USERNAME=autoroot
DATABASE_PASSWORD=autoroot123

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# OpenAI (when implemented)
OPENAI_API_KEY=your-api-key
```

### Profiles

- `dev` (default): Development configuration
- `test`: Testing configuration with H2 database
- `prod`: Production configuration

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run specific module tests
cd autoroot-common
mvn test

# Run with coverage
mvn clean verify jacoco:report
```

## 🚦 Multi-Tenancy Usage

### Request Headers

All API requests must include tenant information:

```bash
curl -H "X-Tenant-ID: 123e4567-e89b-12d3-a456-426614174000" \
     -H "X-Tenant-Slug: my-company" \
     http://localhost:8080/api/v1/services
```

### Tenant Context

The system automatically:
- Extracts tenant info from request headers
- Sets thread-local tenant context
- Filters all database queries by tenant_id
- Clears context after request completion

## 📊 Database Schema

### Core Tables

- **tenants**: Multi-tenant organization data
- **services**: Monitored microservices
- **incidents**: Service incidents and issues
- **log_entries**: Log metadata (actual logs in MinIO)
- **health_checks**: Service health monitoring
- **metrics**: Performance and business metrics

### Sample Data

The database initialization script creates:
- 3 sample tenants
- Multiple services per tenant
- Sample incidents and log entries
- Proper indexes for performance

## 🔍 Troubleshooting

### Common Issues

1. **Port conflicts**
   ```bash
   # Check if ports are in use
   lsof -i :8080  # API Gateway
   lsof -i :5432  # PostgreSQL
   lsof -i :6379  # Redis
   lsof -i :9092  # Kafka
   ```

2. **Database connection issues**
   ```bash
   # Check PostgreSQL is running
   docker-compose ps postgres
   
   # Check database exists
   docker-compose exec postgres psql -U autoroot -d autoroot -c "\\dt"
   ```

3. **Maven dependency issues**
   ```bash
   # Clean and reinstall dependencies
   mvn clean install -U
   
   # Clear local repository cache
   rm -rf ~/.m2/repository/com/autoroot/
   mvn clean install
   ```

### Logs and Debugging

```bash
# View application logs
tail -f autoroot-api-gateway/target/logs/application.log

# Enable debug logging
export LOGGING_LEVEL_COM_AUTOROOT=DEBUG
mvn spring-boot:run
```

## 🚀 Deployment

### Docker Build (Future)

```bash
# Build Docker image
docker build -t autoroot/api-gateway .

# Run container
docker run -p 8080:8080 autoroot/api-gateway
```

### Production Checklist

- [ ] Set production database credentials
- [ ] Configure Redis clustering
- [ ] Set up Kafka cluster
- [ ] Enable security authentication
- [ ] Configure logging to external system
- [ ] Set up monitoring and alerting
- [ ] Configure load balancer

## 📈 Monitoring

### Health Checks
- Application: `/actuator/health`
- Database connectivity
- Redis connectivity
- Kafka connectivity

### Metrics
- JVM metrics via Micrometer
- Custom business metrics
- Database query performance
- Request/response times

## 🤝 Development

### Code Structure

- **Entities**: Multi-tenant JPA entities in `autoroot-common`
- **DTOs**: Request/response objects with validation
- **Mappers**: MapStruct for entity-DTO conversion
- **Controllers**: REST endpoints with proper error handling
- **Services**: Business logic with transaction management
- **Repositories**: Data access with multi-tenant filtering

### Adding New Features

1. Add shared entities/DTOs to `autoroot-common`
2. Create repositories with tenant filtering
3. Implement service layer with business logic
4. Add REST controllers in `autoroot-api-gateway`
5. Write comprehensive tests
6. Update API documentation

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

---

**Status**: ✅ Foundation Complete - Ready for Phase 2 Development

Last Updated: 2026-03-01