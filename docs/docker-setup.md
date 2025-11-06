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

### 2. Apache Kafka Cluster 1 (cluster-a)

Primary message broker using KRaft mode (no Zookeeper).

- **Port 9092:** PLAINTEXT (for backward compatibility and testing)
- **Port 9093:** SASL_SSL (secure with authentication and encryption)
- **Cluster Name:** cluster-a (in application configuration)
- **Features:**
  - SSL/TLS encryption enabled
  - SASL SCRAM-SHA-512 authentication configured
  - ACL-based authorization enabled
  - 3 partitions for outbox events
  - Ready for horizontal scaling

**Resource Limits:**
- CPU: 2 cores (max), 0.5 cores (reserved)
- Memory: 2GB (max), 512MB (reserved)

**Production Configuration:**
- Increase memory to 4-8GB for performance
- Increase CPUs to 4+ cores for high-throughput scenarios
- Use dedicated hosts or Kubernetes nodes
- Set replication factor to 3 for high availability
- Enable JMX monitoring for observability

### 3. Apache Kafka Cluster 2 (cluster-c)

Secondary message broker for multi-cluster testing.

- **Port 9095:** PLAINTEXT
- **Cluster Name:** cluster-c (in application configuration)
- **Purpose:** Independent broker for testing multi-cluster routing
- **Features:** 3 partitions for outbox events

**Resource Limits:**
- CPU: 2 cores (max), 0.5 cores (reserved)
- Memory: 2GB (max), 512MB (reserved)

**Production Note:** 
For production multi-cluster deployments:
- Deploy to separate physical infrastructure for true isolation
- Use the same resource recommendations as the primary cluster
- Consider geographic distribution for disaster recovery

### 4. Kafka UI

Web interface for managing both Kafka clusters.

- **Port:** 8090
- **Features:**
  - Pre-configured with cluster-a (port 9092) and cluster-c (port 9095)
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
- **Realm:** `routebox`

**Resource Limits:**
- CPU: 1.5 cores (max), 0.25 cores (reserved)
- Memory: 1GB (max), 256MB (reserved)

See [Security](security.md#keycloak) for more details.

### 6. Prometheus

Metrics collection and storage.

- **Port:** 9090
- **Purpose:** Collects metrics from applications and evaluates alert rules
- **Configuration:** `infrastructure/monitoring/prometheus/prometheus.yml`
- **Alert Rules:** `infrastructure/monitoring/alertmanager/alert-rules.yml`

**Resource Limits:**
- CPU: 1 core (max), 0.25 cores (reserved)
- Memory: 1GB (max), 256MB (reserved)

### 7. Alertmanager

Alert routing and notifications.

- **Port:** 9093
- **Purpose:** Routes alerts to notification channels (Mailhog for testing)
- **Configuration:** `infrastructure/monitoring/alertmanager/alertmanager.yml`
- **Web UI:** http://localhost:9093

**Resource Limits:**
- CPU: 0.5 cores (max), 0.1 cores (reserved)
- Memory: 256MB (max), 64MB (reserved)

### 8. Mailhog

Email testing server for alert notifications.

- **SMTP Port:** 1025
- **Web UI Port:** 8025
- **Purpose:** Captures alert emails for testing without sending real emails
- **Web UI:** http://localhost:8025

**Resource Limits:**
- CPU: 0.25 cores (max), 0.05 cores (reserved)
- Memory: 128MB (max), 32MB (reserved)

### 9. Grafana

Dashboards and visualization.

- **Port:** 3000
- **Default Login:** `admin` / `admin`
- **Pre-configured Dashboard:** `infrastructure/monitoring/grafana/dashboards/routebox-dashboard.json`

**Resource Limits:**
- CPU: 0.5 cores (max), 0.1 cores (reserved)
- Memory: 512MB (max), 128MB (reserved)

### 10. Loki

Log aggregation.

- **Port:** 3100
- **Purpose:** Centralized log storage and querying
- **Configuration:** `infrastructure/monitoring/loki/loki-config.yml`

**Resource Limits:**
- CPU: 1 core (max), 0.1 cores (reserved)
- Memory: 512MB (max), 128MB (reserved)

### 11. Promtail

Log shipper for Loki.

- **Purpose:** Collects and forwards logs to Loki
- **Configuration:** `infrastructure/monitoring/promtail/promtail-config.yml`

**Resource Limits:**
- CPU: 0.5 cores (max), 0.1 cores (reserved)
- Memory: 256MB (max), 64MB (reserved)

## Total Resource Requirements

**Maximum:** 9.25 CPU cores / 7.63GB RAM  
**Minimum Reserved:** 2.0 CPU cores / 1.91GB RAM
**Development Environment (Current):**
- Maximum: 11.5 CPU cores / 9.25GB RAM  
- Minimum Reserved: 2.55 CPU cores / 2.37GB RAM

**Production-Like Environment (Recommended):**
For a production-like development setup with better Kafka performance:
- Kafka Cluster 1: 4 CPUs / 4GB RAM
- Kafka Cluster 2: 4 CPUs / 4GB RAM
- Azure SQL Edge: 4 CPUs / 4GB RAM
- Other services: As configured above
- Total: ~15 CPUs / 15GB RAM

These limits ensure stable operation and prevent any single service from consuming excessive resources. The configured limits are suitable for development and testing environments.

**For production deployments**, resource requirements will vary significantly based on:
- Message throughput and size
- Number of topics and partitions
- Retention policies
- Replication requirements
- Number of concurrent connections

Consider:
- Running Kafka on dedicated infrastructure separate from application servers
- Using Kubernetes for orchestration with proper resource quotas
- Implementing horizontal pod autoscaling for application services
- Monitoring resource utilization and adjusting limits accordingly
- Using managed Kafka services (e.g., Confluent Cloud, AWS MSK) for simplified operations

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
mvn spring-boot:run -pl routebox-server -Dspring-boot.run.profiles=azuresql
```

Or set in your IDE's run configuration:
```
Active profiles: azuresql
```

Both applications will automatically use:
- Database: Azure SQL Edge on port 1433
- Kafka: localhost:9092 (routebox-server only)

## Database Setup

The applications use Azure SQL Edge when running with Docker Compose. The `routebox-server` also supports H2 in-memory database for development/testing without the `azuresql` profile.

### Creating the Database

After starting the infrastructure, create the required database:

```bash
docker exec routebox-azuresql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -Q "CREATE DATABASE routebox" -C -No
```

## Additional Configuration

For detailed configuration of specific components:

- **Kafka Security:** See [Security](security.md#kafka-security)
- **Keycloak:** See [Security](security.md#keycloak)
- **Monitoring:** See [Monitoring](monitoring.md)
- **Multi-Cluster Routing:** See [Multi-Cluster Routing](multi-cluster-routing.md)
