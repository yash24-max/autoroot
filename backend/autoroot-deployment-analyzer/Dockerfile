# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy root pom and parent pom
COPY pom.xml .
COPY backend/pom.xml backend/pom.xml

# Copy all module poms
COPY backend/autoroot-common/pom.xml backend/autoroot-common/pom.xml
COPY backend/autoroot-api-gateway/pom.xml backend/autoroot-api-gateway/pom.xml
COPY backend/autoroot-incident-service/pom.xml backend/autoroot-incident-service/pom.xml
COPY backend/autoroot-log-processor/pom.xml backend/autoroot-log-processor/pom.xml
COPY backend/autoroot-correlation-engine/pom.xml backend/autoroot-correlation-engine/pom.xml
COPY backend/autoroot-ai-engine/pom.xml backend/autoroot-ai-engine/pom.xml
COPY backend/autoroot-alert-service/pom.xml backend/autoroot-alert-service/pom.xml
COPY backend/autoroot-deployment-analyzer/pom.xml backend/autoroot-deployment-analyzer/pom.xml

# Go offline to cache dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY backend/autoroot-common backend/autoroot-common
COPY backend/autoroot-api-gateway backend/autoroot-api-gateway

# Build the specific module
ARG MODULE_NAME
RUN mvn clean package -DskipTests -pl backend/autoroot-deployment-analyzer -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG MODULE_NAME
COPY --from=build /app/backend/autoroot-deployment-analyzer/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
