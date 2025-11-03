# Known Issues

This document tracks known issues with the Catbox project and their workarounds.

## Kafka Docker Image SSL/SASL Configuration Issue

**Status:** Open  
**Severity:** Medium  
**Affects:** Docker Compose setup with SSL/SASL enabled

### Description

The official Apache Kafka Docker image (`apache/kafka:latest`) has compatibility issues with certain environment variable configurations, specifically when using `KAFKA_OPTS` to set the JAAS configuration file path. The startup script fails with an "unbound variable" error.

```
/etc/kafka/docker/configure: line 18: !1: unbound variable
```

### Impact

- The Kafka container fails to start when SSL/SASL security features are fully enabled via the configuration in `compose.yaml`
- This prevents testing of secure Kafka connections out of the box

### Workaround

For development and testing, use a simplified Kafka configuration with PLAINTEXT security:

1. Create a `compose.override.yml` file in the project root:

```yaml
services:
  kafka:
    volumes:
      - kafka-data:/var/lib/kafka/data
    environment:
      # Simplified listener configuration (PLAINTEXT only)
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9094
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
      KAFKA_SSL_CLIENT_AUTH: "none"
      KAFKA_SASL_ENABLED_MECHANISMS: ""
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OPTS: ""
      KAFKA_AUTHORIZER_CLASS_NAME: ""
      KAFKA_ALLOW_EVERYONE_IF_NO_ACL_FOUND: "true"
```

2. Start Docker Compose as normal:

```bash
docker compose up -d
```

### Alternative Solutions

1. **Use a different Kafka Docker image**: Consider using `confluentinc/cp-kafka` which has better support for SSL/SASL configuration
2. **Run Kafka outside Docker**: Install Kafka directly on the host machine for development
3. **Wait for upstream fix**: Monitor the Apache Kafka Docker image repository for updates

### References

- Related to Apache Kafka Docker image environment variable handling
- Affects version 4.0.0 (latest as of November 2025)

### Long-term Solution

The project should either:
1. Switch to a more stable Kafka Docker image (e.g., Confluent Platform)
2. Simplify the default configuration to use PLAINTEXT and make SSL/SASL truly optional
3. Document the SSL/SASL setup as an advanced configuration requiring manual steps

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
