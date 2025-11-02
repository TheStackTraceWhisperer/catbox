# Docker Setup

This document describes the Docker Compose infrastructure setup for the Catbox project.

## Overview

The project includes an `infrastructure` directory with a `compose.yaml` file that sets up all required services.

## Services

### 1. Azure SQL Edge

Microsoft SQL Server compatible database.

- **Port:** 1433
- **Username:** `sa`
- **Password:** Set via `DB_PASSWORD` environment variable
- **Compatible with:** Azure SQL Database

**Resource Limits:**
- CPU: 2 cores (max), 0.5 cores (reserved)
- Memory: 2GB (max), 512MB (reserved)

### 2. Apache Kafka Cluster 1

Primary message broker using KRaft mode (no Zookeeper).

- **Port 9092:** PLAINTEXT (for backward compatibility)
- **Port 9093:** SASL_SSL (secure with authentication and encryption)
- **Features:**
  - SSL/TLS encryption enabled
  - SASL SCRAM-SHA-512 authentication configured
  - ACL-based authorization enabled
  - 3 partitions for outbox events
  - Ready for horizontal scaling

**Resource Limits:**
- CPU: 2 cores (max), 0.5 cores (reserved)
- Memory: 2GB (max), 512MB (reserved)

### 3. Apache Kafka Cluster 2

Secondary message broker for multi-cluster testing.

- **Port 9095:** PLAINTEXT
- **Purpose:** Independent broker for testing multi-cluster routing
- **Features:** 3 partitions for outbox events

**Resource Limits:**
- CPU: 2 cores (max), 0.5 cores (reserved)
- Memory: 2GB (max), 512MB (reserved)

### 4. Kafka UI

Web interface for managing both Kafka clusters.

- **Port:** 8090
- **Features:**
  - Pre-configured with both clusters
  - Browse topics, messages, and consumer groups
  - Monitor cluster health and metrics

**Resource Limits:**
- CPU: 0.5 cores (max), 0.1 cores (reserved)
- Memory: 512MB (max), 128MB (reserved)

### 5. Keycloak

Identity provider for OAuth2/OIDC authentication.

- **Port:** 8180 (changed from 8080 to avoid conflict with order-service)
- **Admin Username:** `admin`
- **Admin Password:** `admin`
- **Realm:** `catbox`

**Resource Limits:**
- CPU: 1.5 cores (max), 0.25 cores (reserved)
- Memory: 1GB (max), 256MB (reserved)

See [Security](security.md#keycloak) for more details.

### 6. Prometheus

Metrics collection and storage.

- **Port:** 9090
- **Purpose:** Collects metrics from applications
- **Configuration:** `infrastructure/monitoring/prometheus/prometheus.yml`

**Resource Limits:**
- CPU: 1 core (max), 0.25 cores (reserved)
- Memory: 1GB (max), 256MB (reserved)

### 7. Grafana

Dashboards and visualization.

- **Port:** 3000
- **Default Login:** `admin` / `admin`
- **Pre-configured Dashboard:** `infrastructure/monitoring/grafana/dashboards/catbox-dashboard.json`

**Resource Limits:**
- CPU: 0.5 cores (max), 0.1 cores (reserved)
- Memory: 512MB (max), 128MB (reserved)

### 8. Loki

Log aggregation.

- **Port:** 3100
- **Purpose:** Centralized log storage and querying
- **Configuration:** `infrastructure/monitoring/loki/loki-config.yml`

**Resource Limits:**
- CPU: 1 core (max), 0.1 cores (reserved)
- Memory: 512MB (max), 128MB (reserved)

### 9. Promtail

Log shipper for Loki.

- **Purpose:** Collects and forwards logs to Loki
- **Configuration:** `infrastructure/monitoring/promtail/promtail-config.yml`

**Resource Limits:**
- CPU: 0.5 cores (max), 0.1 cores (reserved)
- Memory: 256MB (max), 64MB (reserved)

## Total Resource Requirements

**Maximum:** 8.5 CPU cores / 7.25GB RAM  
**Minimum Reserved:** 1.8 CPU cores / 1.81GB RAM

These limits ensure stable operation and prevent any single service from consuming excessive resources. The configured limits are suitable for development and testing environments.

**For production deployments**, consider increasing these limits based on your actual workload requirements, especially for Azure SQL Edge and Kafka which may need significantly more resources under heavy load. Adjust the limits in `compose.yaml` based on your system capacity and environment type.

## Using Docker Compose

### Start Services

```bash
cd infrastructure && docker compose up -d
```

### Check Service Health

```bash
cd infrastructure && docker compose ps
```

### View Logs

```bash
# All services
cd infrastructure && docker compose logs -f

# Specific service
cd infrastructure && docker compose logs -f kafka
```

### Stop Services

```bash
cd infrastructure && docker compose down
```

### Clean Up Volumes

```bash
cd infrastructure && docker compose down -v
```

## Connecting Applications to Infrastructure

When running with docker-compose, use the `azuresql` profile for both services:

**Order Service:**
```bash
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql
```

**Catbox Server:**
```bash
mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql
```

Or set in your IDE's run configuration:
```
Active profiles: azuresql
```

Both applications will automatically use:
- Database: Azure SQL Edge on port 1433
- Kafka: localhost:9092 (catbox-server only)

## Database Setup

The applications use Azure SQL Edge when running with Docker Compose. The `catbox-server` also supports H2 in-memory database for development/testing without the `azuresql` profile.

### Creating the Database

After starting the infrastructure, create the required database:

```bash
docker exec catbox-azuresql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -Q "CREATE DATABASE catbox" -C -No
```

## Additional Configuration

For detailed configuration of specific components:

- **Kafka Security:** See [Security](security.md#kafka-security)
- **Keycloak:** See [Security](security.md#keycloak)
- **Monitoring:** See [Monitoring](monitoring.md)
- **Multi-Cluster Routing:** See [Multi-Cluster Routing](multi-cluster-routing.md)
