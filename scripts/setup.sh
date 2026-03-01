#!/bin/bash

# AutoRoot Infrastructure Setup Script
# This script sets up the complete Docker Compose infrastructure for AutoRoot

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Log function
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR:${NC} $1"
}

# Check if Docker is installed and running
check_docker() {
    log "Checking Docker installation..."
    
    if ! command -v docker &> /dev/null; then
        error "Docker is not installed. Please install Docker Desktop from https://www.docker.com/products/docker-desktop"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        error "Docker is not running. Please start Docker Desktop."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        error "Docker Compose is not available. Please ensure Docker Desktop is properly installed."
        exit 1
    fi
    
    log "Docker and Docker Compose are installed and running ✅"
}

# Check if required files exist
check_files() {
    log "Checking required files..."
    
    local files=(
        "$PROJECT_DIR/docker-compose.yml"
        "$PROJECT_DIR/infrastructure/postgres/init.sql"
        "$PROJECT_DIR/infrastructure/.env"
    )
    
    for file in "${files[@]}"; do
        if [[ ! -f "$file" ]]; then
            error "Required file not found: $file"
            exit 1
        fi
    done
    
    log "All required files found ✅"
}

# Create environment file if it doesn't exist
setup_env() {
    log "Setting up environment configuration..."
    
    local env_file="$PROJECT_DIR/.env"
    local env_template="$PROJECT_DIR/infrastructure/.env"
    
    if [[ ! -f "$env_file" ]]; then
        log "Creating .env file from template..."
        cp "$env_template" "$env_file"
        warn "Please review and update the .env file with appropriate values for your environment"
    else
        log "Environment file already exists ✅"
    fi
}

# Stop and remove existing containers
cleanup_existing() {
    log "Cleaning up existing containers..."
    
    cd "$PROJECT_DIR"
    
    # Stop all services
    if docker-compose ps -q 2>/dev/null | grep -q .; then
        docker-compose down -v --remove-orphans
        log "Stopped existing services ✅"
    else
        log "No existing services to stop ✅"
    fi
}

# Pull latest images
pull_images() {
    log "Pulling latest Docker images..."
    cd "$PROJECT_DIR"
    docker-compose pull
    log "Images pulled successfully ✅"
}

# Start infrastructure services
start_services() {
    log "Starting AutoRoot infrastructure services..."
    cd "$PROJECT_DIR"
    
    # Start core services first
    log "Starting core services (PostgreSQL, Redis, Zookeeper)..."
    docker-compose up -d postgres redis zookeeper
    
    # Wait for core services to be healthy
    log "Waiting for core services to be healthy..."
    
    local max_attempts=30
    local attempt=0
    
    while [[ $attempt -lt $max_attempts ]]; do
        if docker-compose ps postgres | grep -q "healthy\|Up"; then
            break
        fi
        sleep 2
        ((attempt++))
    done
    
    if [[ $attempt -eq $max_attempts ]]; then
        error "Core services failed to start properly"
        docker-compose logs postgres redis zookeeper
        exit 1
    fi
    
    # Start remaining services
    log "Starting remaining services (Kafka, MinIO)..."
    docker-compose up -d kafka minio
    
    # Wait a bit for services to stabilize
    sleep 10
    
    log "All services started successfully ✅"
}

# Verify services are running
verify_services() {
    log "Verifying services are running..."
    cd "$PROJECT_DIR"
    
    local services=("postgres" "redis" "zookeeper" "kafka" "minio")
    local all_healthy=true
    
    for service in "${services[@]}"; do
        local status=$(docker-compose ps -q "$service" 2>/dev/null)
        if [[ -z "$status" ]]; then
            error "$service is not running"
            all_healthy=false
        else
            local health=$(docker inspect --format='{{.State.Health.Status}}' "$(docker-compose ps -q "$service")" 2>/dev/null || echo "unknown")
            if [[ "$health" == "healthy" || "$health" == "unknown" ]]; then
                log "$service is running ✅"
            else
                warn "$service is running but may not be healthy (status: $health)"
            fi
        fi
    done
    
    if [[ "$all_healthy" == false ]]; then
        error "Some services are not running properly"
        return 1
    fi
    
    log "All services are running ✅"
}

# Test database connectivity
test_database() {
    log "Testing database connectivity..."
    cd "$PROJECT_DIR"
    
    local max_attempts=10
    local attempt=0
    
    while [[ $attempt -lt $max_attempts ]]; do
        if docker-compose exec -T postgres pg_isready -U autoroot -d autoroot &>/dev/null; then
            log "Database connectivity test passed ✅"
            return 0
        fi
        sleep 3
        ((attempt++))
    done
    
    error "Database connectivity test failed"
    return 1
}

# Create Kafka topics
setup_kafka_topics() {
    log "Setting up Kafka topics..."
    cd "$PROJECT_DIR"
    
    # Wait for Kafka to be ready
    local max_attempts=30
    local attempt=0
    
    while [[ $attempt -lt $max_attempts ]]; do
        if docker-compose exec -T kafka kafka-broker-api-versions --bootstrap-server localhost:9092 &>/dev/null; then
            break
        fi
        sleep 2
        ((attempt++))
    done
    
    if [[ $attempt -eq $max_attempts ]]; then
        warn "Kafka not ready, skipping topic creation"
        return 1
    fi
    
    # Create topics
    local topics=("incidents" "logs" "metrics" "health-checks" "notifications")
    
    for topic in "${topics[@]}"; do
        if ! docker-compose exec -T kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic "$topic" &>/dev/null; then
            log "Creating Kafka topic: $topic"
            docker-compose exec -T kafka kafka-topics \
                --bootstrap-server localhost:9092 \
                --create \
                --topic "$topic" \
                --partitions 3 \
                --replication-factor 1 \
                --if-not-exists
        else
            log "Kafka topic already exists: $topic ✅"
        fi
    done
    
    log "Kafka topics setup completed ✅"
}

# Setup MinIO buckets
setup_minio_buckets() {
    log "Setting up MinIO buckets..."
    cd "$PROJECT_DIR"
    
    # Install MinIO client if not present
    if ! command -v mc &> /dev/null; then
        warn "MinIO client (mc) not found. Installing..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            if command -v brew &> /dev/null; then
                brew install minio/stable/mc
            else
                curl -O https://dl.min.io/client/mc/release/darwin-amd64/mc
                chmod +x mc
                sudo mv mc /usr/local/bin/
            fi
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            curl -O https://dl.min.io/client/mc/release/linux-amd64/mc
            chmod +x mc
            sudo mv mc /usr/local/bin/
        else
            warn "Unsupported OS for MinIO client installation. Please install manually."
            return 1
        fi
    fi
    
    # Configure MinIO client
    mc alias set autoroot-local http://localhost:9000 minioadmin minioadmin_dev_password_change_in_production &>/dev/null || true
    
    # Create buckets
    local buckets=("logs" "backups" "exports" "uploads")
    
    for bucket in "${buckets[@]}"; do
        if ! mc ls "autoroot-local/$bucket" &>/dev/null; then
            log "Creating MinIO bucket: $bucket"
            mc mb "autoroot-local/$bucket"
        else
            log "MinIO bucket already exists: $bucket ✅"
        fi
    done
    
    log "MinIO buckets setup completed ✅"
}

# Display service information
display_info() {
    log "AutoRoot Infrastructure Setup Complete! 🎉"
    echo
    echo -e "${BLUE}=== Service Information ===${NC}"
    echo -e "${YELLOW}PostgreSQL:${NC}     localhost:5432 (user: autoroot)"
    echo -e "${YELLOW}Redis:${NC}          localhost:6379"
    echo -e "${YELLOW}Kafka:${NC}          localhost:9092"
    echo -e "${YELLOW}Zookeeper:${NC}      localhost:2181"
    echo -e "${YELLOW}MinIO API:${NC}       http://localhost:9000"
    echo -e "${YELLOW}MinIO Console:${NC}   http://localhost:9001"
    echo
    echo -e "${BLUE}=== Development Tools (use --profile dev) ===${NC}"
    echo -e "${YELLOW}pgAdmin:${NC}        http://localhost:5050"
    echo -e "${YELLOW}Kafka UI:${NC}       http://localhost:8080"
    echo
    echo -e "${BLUE}=== Useful Commands ===${NC}"
    echo -e "${YELLOW}Check service status:${NC}  docker-compose ps"
    echo -e "${YELLOW}View logs:${NC}             docker-compose logs [service_name]"
    echo -e "${YELLOW}Stop services:${NC}         docker-compose down"
    echo -e "${YELLOW}Start dev tools:${NC}       docker-compose --profile dev up -d"
    echo -e "${YELLOW}Connect to PostgreSQL:${NC} docker-compose exec postgres psql -U autoroot -d autoroot"
    echo -e "${YELLOW}Connect to Redis:${NC}      docker-compose exec redis redis-cli -a redis_dev_password_change_in_production"
    echo
    echo -e "${GREEN}All services are ready! 🚀${NC}"
}

# Main function
main() {
    log "Starting AutoRoot Infrastructure Setup..."
    
    # Parse command line arguments
    local dev_mode=false
    while [[ $# -gt 0 ]]; do
        case $1 in
            --dev)
                dev_mode=true
                shift
                ;;
            --help|-h)
                echo "Usage: $0 [OPTIONS]"
                echo "Options:"
                echo "  --dev      Start development tools (pgAdmin, Kafka UI)"
                echo "  --help     Show this help message"
                exit 0
                ;;
            *)
                error "Unknown option: $1"
                exit 1
                ;;
        esac
    done
    
    # Run setup steps
    check_docker
    check_files
    setup_env
    cleanup_existing
    pull_images
    start_services
    
    # Wait for services to be ready
    sleep 5
    
    verify_services
    test_database
    setup_kafka_topics
    setup_minio_buckets
    
    # Start development tools if requested
    if [[ "$dev_mode" == true ]]; then
        log "Starting development tools..."
        cd "$PROJECT_DIR"
        docker-compose --profile dev up -d
    fi
    
    display_info
    
    log "Setup completed successfully! 🎉"
}

# Run main function with all arguments
main "$@"