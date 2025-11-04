# Known Issues

This document tracks known issues with the Catbox project and their workarounds.

## Kafka Docker Image SSL/SASL Configuration Issue

**Status:** Resolved  
**Fixed in:** Migration to Confluent Platform Kafka (confluentinc/cp-kafka:7.6.0)  
**Severity:** Medium  
**Affects:** Docker Compose setup with SSL/SASL enabled (Apache Kafka image only)

### Description

The official Apache Kafka Docker image (`apache/kafka:latest`) had compatibility issues with certain environment variable configurations, specifically when using `KAFKA_OPTS` to set the JAAS configuration file path. The startup script failed with an "unbound variable" error.

```
/etc/kafka/docker/configure: line 18: !1: unbound variable
```

### Resolution

The project has been migrated to use the Confluent Platform Kafka Docker image (`confluentinc/cp-kafka:7.6.0`), which provides:

- Better SSL/SASL support and configuration
- More reliable environment variable handling
- Enterprise-grade features and stability
- Full compatibility with the existing SSL/SASL security configuration

The migration includes:
- Updated `compose.yaml` to use `confluentinc/cp-kafka:7.6.0` for both Kafka clusters
- Confluent-specific environment variables (`KAFKA_SSL_KEYSTORE_FILENAME` instead of `KAFKA_SSL_KEYSTORE_LOCATION`)
- Regenerated SSL certificates with proper CA extensions for Confluent compatibility
- Added required `CLUSTER_ID` for KRaft mode
- Added `credentials` file for Confluent SSL configuration

### Impact

SSL/SASL security features now work out of the box with the updated Docker Compose configuration. Both PLAINTEXT (port 9092) and SASL_SSL (port 9093) listeners are fully functional.

### References

- Migration completed in this PR
- Confluent Platform Kafka: https://hub.docker.com/r/confluentinc/cp-kafka

---

## Port Conflict: Keycloak vs Order Service (RESOLVED)

**Status:** Resolved  
**Fixed in:** This PR

### Description

The original `compose.yaml` configured Keycloak to use port 8080, which conflicts with the order-service that also runs on port 8080 by default (Spring Boot default port).

### Resolution

- Changed Keycloak's external port mapping from `8080:8080` to `8180:8080` in `compose.yaml`
- Updated all documentation to reference `http://localhost:8180` for Keycloak
- order-service can now use port 8080 as documented

---

## Database Not Created Automatically (RESOLVED)

**Status:** Resolved  
**Fixed in:** This PR

### Description

The application configuration uses `hibernate.ddl-auto: update` which should create tables but not the database itself. Azure SQL Edge requires the database to exist before Hibernate can create tables.

### Resolution

- Added database creation step to README.md Quick Start section
- Documents the `CREATE DATABASE catbox` command
- Users must create the database before starting the applications

### Alternative Approaches

For a fully automated setup, consider:
1. Adding an init script to the Docker Compose configuration that creates the database
2. Using Flyway or Liquibase for database migrations which can handle database creation
3. Changing to H2 in-memory database for local development (already supported in non-azuresql profile)

**Status:** Resolved  
**Fixed in:** This PR

### Description

Docker Compose requires a `.env` file with `DB_PASSWORD` set, but this wasn't documented in the Quick Start guide.

### Resolution

- Added `.env` file setup instructions to README.md Quick Start section
- Created `.env.example` template file
- Documented Azure SQL password requirements (minimum 8 characters, complexity requirements)
