# Troubleshooting Guide

Common issues and solutions for the RouteBox project.

## Kafka Configuration

The project uses Confluent Platform Kafka Docker image (`confluentinc/cp-kafka:7.6.0`) which provides:

- Reliable SSL/SASL support and configuration
- Better environment variable handling
- Enterprise-grade features and stability
- Full compatibility with SSL/SASL security configuration

Both PLAINTEXT (port 9092) and SASL_SSL (port 9093) listeners are fully functional.

**Reference:** [Confluent Platform Kafka](https://hub.docker.com/r/confluentinc/cp-kafka)

---

## Port Configuration

### Keycloak Port

Keycloak runs on port **8180** (not 8080) to avoid conflicts with order-service which uses the Spring Boot default port 8080.

- **Keycloak URL:** `http://localhost:8180`
- **Order Service URL:** `http://localhost:8080`

---

## Database Setup

### Manual Database Creation Required

The application uses `hibernate.ddl-auto: update` which creates tables automatically, but the database itself must be created manually before starting the applications.

**Required Step:**

```bash
docker exec routebox-azuresql /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "${DB_PASSWORD}" \
  -Q "CREATE DATABASE routebox" -C -No
```

See the [Quick Start Guide](../README.md#quick-start) for complete setup instructions.

### Alternative Database Options

For different use cases, consider:

1. **Automated setup:** Add an init script to Docker Compose that creates the database
2. **Schema migrations:** Use Flyway or Liquibase for full database lifecycle management
3. **Local development:** Use H2 in-memory database (supported via default Spring profile)

---

## Environment Configuration

### Required `.env` File

Docker Compose requires a `.env` file in the project root with database credentials.

**Create from template:**

```bash
cp .env.example .env
```

**Required variables:**

- `DB_PASSWORD` - Azure SQL password (minimum 8 characters, must include uppercase, lowercase, numbers, and symbols)
- `KAFKA_USERNAME` - Kafka SASL username
- `KAFKA_PASSWORD` - Kafka SASL password

See `.env.example` for a complete template.
