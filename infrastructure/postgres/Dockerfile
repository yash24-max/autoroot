# Custom PostgreSQL Dockerfile for AutoRoot
# This extends the official PostgreSQL image with additional extensions and configuration

FROM postgres:15-alpine

# Install additional extensions
RUN apk add --no-cache \
    postgresql-contrib \
    postgresql-dev \
    gcc \
    musl-dev \
    make

# Set locale
ENV LANG=en_US.utf8

# Install additional PostgreSQL extensions
# pg_trgm for full-text search, uuid-ossp for UUID generation
RUN echo "# Additional extensions" >> /usr/local/share/postgresql/postgresql.conf.sample && \
    echo "shared_preload_libraries = 'pg_stat_statements'" >> /usr/local/share/postgresql/postgresql.conf.sample

# Custom PostgreSQL configuration for better performance
COPY postgres.conf /etc/postgresql/postgresql.conf
COPY pg_hba.conf /etc/postgresql/pg_hba.conf

# Set custom configuration
ENV POSTGRES_CONFIG_FILE=/etc/postgresql/postgresql.conf
ENV POSTGRES_HBA_FILE=/etc/postgresql/pg_hba.conf

# Health check script
COPY health-check.sh /usr/local/bin/health-check.sh
RUN chmod +x /usr/local/bin/health-check.sh

# Custom entrypoint for additional setup
COPY docker-entrypoint-initdb.d/* /docker-entrypoint-initdb.d/

HEALTHCHECK --interval=10s --timeout=5s --start-period=10s --retries=5 \
    CMD /usr/local/bin/health-check.sh

EXPOSE 5432