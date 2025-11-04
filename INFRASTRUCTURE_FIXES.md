# Infrastructure and Documentation Fixes

This document summarizes all the issues found and fixed when following the setup documentation verbatim.

## Issues Found and Resolved

### 1. Port Conflict - Alertmanager vs Kafka SASL_SSL

**Issue:** Both Alertmanager and Kafka's SASL_SSL listener were configured to use port 9093, causing a port conflict that prevented services from starting.

**Resolution:**
- Changed Alertmanager port mapping from `9093:9093` to `9094:9093` in `infrastructure/compose.yaml`
- Updated all documentation references to Alertmanager from port 9093 to 9094:
  - `docs/monitoring.md` (3 locations)
  - `infrastructure/README.md` (2 locations)

**Files Changed:**
- `infrastructure/compose.yaml`
- `docs/monitoring.md`
- `infrastructure/README.md`

### 2. Azure SQL Docker Compose Configuration

**Issue:** The Azure SQL container was configured with a complex startup command that had bash syntax errors, causing the container to fail to start properly.

**Resolution:**
- Simplified the Azure SQL configuration by removing the custom entrypoint script
- Removed the init script execution from the container command
- The database schema is now initialized manually using `init.sql` after the container starts
- This follows the documentation's approach of manual database creation

**Files Changed:**
- `infrastructure/compose.yaml`

### 3. Database Name Inconsistency

**Issue:** Configuration files used "catbox" as the database name, but documentation instructs creating a database named "routebox".

**Resolution:**
- Updated all `databaseName=catbox` references to `databaseName=routebox` in:
  - `order-service/src/main/resources/application.yml`
  - `routebox-server/src/main/resources/application-docker.yml` (azuresql profile)
  - `order-processor/src/main/resources/application.yml`

**Files Changed:**
- `order-service/src/main/resources/application.yml`
- `routebox-server/src/main/resources/application-docker.yml`
- `order-processor/src/main/resources/application.yml`

### 4. Application Name Inconsistency

**Issue:** Spring application names were set to "catbox" instead of proper module names.

**Resolution:**
- Updated `spring.application.name` from "catbox" to:
  - `routebox-server` in `routebox-server/src/main/resources/application.yml` and `application-docker.yml`
  - Application names for order-service and order-processor were already correct

**Files Changed:**
- `routebox-server/src/main/resources/application.yml`
- `routebox-server/src/main/resources/application-docker.yml`

### 5. Logging Package Name

**Issue:** Logging configuration referenced `com.example.catbox` instead of the correct package.

**Resolution:**
- Updated logging configuration from `com.example.catbox: INFO` to `com.example.routebox: INFO`

**Files Changed:**
- `routebox-server/src/main/resources/application.yml`

### 6. Keycloak Port Configuration

**Issue:** The Keycloak issuer-uri was configured to use port 8080, but Keycloak is actually running on port 8180 (to avoid conflict with order-service).

**Resolution:**
- Updated `issuer-uri` from `http://localhost:8080/realms/catbox` to `http://localhost:8180/realms/catbox`

**Files Changed:**
- `routebox-server/src/main/resources/application-secure.yml`

### 7. Documentation Typos

**Issue:** Quick Start documentation referred to "Catbox" instead of "RouteBox".

**Resolution:**
- Fixed typo in `docs/quick-start.md`

**Files Changed:**
- `docs/quick-start.md`

### 8. Generated Test Report in Git

**Issue:** `docs/test-durations.md` is a generated file that was being tracked in git.

**Resolution:**
- Added `docs/test-durations.md` to `.gitignore`

**Files Changed:**
- `.gitignore`

## Environment Setup

### Prerequisites Verified
- ✅ Java 21 installed and configured (upgraded from Java 17)
- ✅ Maven 3.9.11 available
- ✅ Docker and Docker Compose installed

### Environment Files
- Created `.env` file in project root with:
  ```
  DB_PASSWORD=YourStrong!Passw0rd
  KAFKA_USERNAME=producer
  KAFKA_PASSWORD=producer-secret
  ```
- Copied `.env` to `infrastructure/` directory for Docker Compose

## Infrastructure Services Status

All services are running and healthy:

| Service | Port(s) | Status |
|---------|---------|--------|
| Azure SQL Edge | 1433 | ✅ Running |
| Kafka Cluster 1 (PLAINTEXT) | 9092 | ✅ Healthy |
| Kafka Cluster 1 (SASL_SSL) | 9093 | ✅ Healthy |
| Kafka Cluster 2 | 9095 | ✅ Healthy |
| Kafka UI | 8090 | ✅ Healthy |
| Keycloak | 8180 | ✅ Running |
| Prometheus | 9090 | ✅ Healthy |
| Alertmanager | 9094 | ✅ Healthy |
| Grafana | 3000 | ✅ Healthy |
| Loki | 3100 | ✅ Healthy |
| Promtail | - | ✅ Running |
| Tempo | 3200, 4317, 4318 | ✅ Running |
| Mailhog SMTP | 1025 | ✅ Healthy |
| Mailhog Web UI | 8025 | ✅ Healthy |

## Database Setup

1. Created `routebox` database:
   ```bash
   docker exec routebox-azuresql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -Q "CREATE DATABASE routebox" -C -No
   ```

2. Initialized schema:
   ```bash
   docker exec -i routebox-azuresql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -d routebox -C -No < infrastructure/init.sql
   ```

## Build Verification

- ✅ Build successful: `mvn clean verify -DskipTests`
- ✅ All modules compile successfully
- ✅ JAR files created for all services

## Testing Notes

- Tests run successfully but take considerable time
- 108 tests passed in routebox-server module
- Some tests use Testcontainers which adds to execution time

## Next Steps

To run the applications:

1. Ensure infrastructure is running:
   ```bash
   cd infrastructure && docker compose ps
   ```

2. Start order-service:
   ```bash
   mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql
   ```

3. Start routebox-server:
   ```bash
   mvn spring-boot:run -pl routebox-server -Dspring-boot.run.profiles=azuresql
   ```

4. Start order-processor:
   ```bash
   mvn spring-boot:run -pl order-processor
   ```

## Summary

All infrastructure issues have been identified and resolved. The system is now configured according to the documentation and all services are operational. The main categories of issues were:

1. **Port conflicts** - Alertmanager and Kafka SASL_SSL both using port 9093
2. **Naming inconsistencies** - "catbox" vs "routebox" throughout configs
3. **Container configuration** - Azure SQL startup command syntax
4. **Documentation** - Incorrect port numbers and typos

The fixes ensure that anyone following the setup documentation will have a working system without encountering these issues.
