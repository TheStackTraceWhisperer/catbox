# Infrastructure

This directory contains all Docker infrastructure files and configurations for the RouteBox project.

## Contents

- **compose.yaml** - Docker Compose configuration for all infrastructure services
- **monitoring/** - Prometheus, Grafana, and Loki configurations for observability
- **kafka-security/** - Kafka SSL/TLS certificates and SASL authentication configurations
- **keycloak/** - Keycloak realm configuration for OAuth2/OIDC authentication

## Quick Start

To start all infrastructure services:

```bash
cd infrastructure
docker compose up -d
```

To check service health:

```bash
cd infrastructure
docker compose ps
```

To stop all services:

```bash
cd infrastructure
docker compose down
```

## Infrastructure Services

### Database
- **Azure SQL Edge** - Microsoft SQL Server compatible database on port 1433

### Messaging
- **Confluent Platform Kafka Cluster 1** - Primary message broker using KRaft mode (confluentinc/cp-kafka:7.6.0)
  - Port 9092 (PLAINTEXT - for backward compatibility)
  - Port 9093 (SASL_SSL - secure with authentication and encryption)
- **Confluent Platform Kafka Cluster 2** - Secondary message broker for multi-cluster testing (confluentinc/cp-kafka:7.6.0)
  - Port 9095 (PLAINTEXT)
- **Kafka UI** - Web interface for managing and monitoring both Kafka clusters on port 8090

### Identity & Access Management
- **Keycloak** - OAuth2/OIDC provider on port 8080

### Observability
- **Prometheus** - Metrics collection on port 9090
- **Alertmanager** - Alert routing and notifications on port 9094
- **Mailhog** - Email testing server on ports 1025 (SMTP) and 8025 (Web UI)
- **Grafana** - Metrics visualization on port 3000
- **Loki** - Log aggregation on port 3100
- **Promtail** - Log shipping agent

## Configuration

### Kafka Security

For information on configuring Kafka SSL/TLS and SASL authentication, see [kafka-security/README.md](kafka-security/README.md).

To enable Kafka security:

1. Generate SSL certificates (first-time only):
   ```bash
   cd kafka-security/certs
   ./generate-certs.sh
   cd ../..
   ```

2. After starting Kafka, initialize security configuration:
   ```bash
   ./kafka-security/init-kafka-security.sh
   ```

### Keycloak Configuration

For information on configuring Keycloak, see [keycloak/README.md](keycloak/README.md).

The default realm includes:
- Realm: `catbox`
- User: `catbox` / `catbox`
- Client ID: `routebox-server`

### Kafka UI Configuration

Kafka UI provides a web interface for managing and monitoring both Kafka clusters:

- **Access**: http://localhost:8090
- **Clusters**:
  - `cluster-a` - Connected to kafka:9092 (main cluster with PLAINTEXT and SASL_SSL)
  - `cluster-c` - Connected to kafka-2:9095 (secondary cluster for multi-cluster testing)

**Note on cluster naming:** The application configuration uses logical cluster names that map to physical Kafka brokers:
- **cluster-a**: Primary Kafka broker (kafka:9092 PLAINTEXT, kafka:9093 SASL_SSL)
- **cluster-b**: Same physical broker as cluster-a, but configured to use the secure SASL_SSL listener on port 9093
- **cluster-c**: Secondary Kafka broker (kafka-2:9095 PLAINTEXT)

Features:
- Browse topics, messages, and consumer groups
- Create and manage topics
- View cluster metrics and broker information
- Monitor consumer lag
- Inspect message schemas

The application is configured to route different event types to different clusters:
- `OrderCreated` events → cluster-a (kafka:9092)
- `OrderStatusChanged` events → cluster-a (kafka:9092)

For multi-cluster routing examples, see the [Multi-Cluster Routing documentation](../docs/multi-cluster-routing.md).

### Monitoring Configuration

The monitoring stack includes pre-configured dashboards, data sources, and alerting:

- **Prometheus**: Scrapes metrics from both order-service (port 8080) and routebox-server (port 8081)
- **Alertmanager**: Routes alerts to Mailhog for testing the alerting pipeline
- **Mailhog**: Captures alert emails for testing - Web UI at http://localhost:8025
- **Grafana**: Includes a pre-configured dashboard at `monitoring/grafana/dashboards/routebox-dashboard.json`
- **Loki**: Aggregates logs from all Docker containers via Promtail

### Alerting Configuration

The system includes a complete alerting pipeline for monitoring the outbox pattern:

#### Alertmanager
Alertmanager receives alerts from Prometheus and routes them based on severity:

- **Access**: http://localhost:9094
- **Configuration**: `monitoring/alertmanager/alertmanager.yml`
- **Alert Rules**: `monitoring/alertmanager/alert-rules.yml`

Alert rules include:
- High outbox backlog (>100 pending events)
- Stalled event processing (oldest event >5 minutes)
- High failure rates
- Dead letter queue growth
- Application health checks
- JVM memory and GC alerts

#### Mailhog (Email Testing)
Mailhog acts as a fake SMTP server to capture alert emails without sending real emails:

- **Web UI**: http://localhost:8025
- **SMTP Port**: 1025
- **Features**: View all alert emails, test notification templates, inspect email content

To test the alerting pipeline:
1. Start all services: `docker compose up -d`
2. Wait for services to be healthy
3. Create a condition that triggers an alert (e.g., stop routebox-server to trigger `NoEventProcessing`)
4. Check Prometheus alerts: http://localhost:9090/alerts
5. View emails in Mailhog: http://localhost:8025

## Environment Variables

The following environment variables can be set before starting services:

- `DB_PASSWORD` - Password for Azure SQL Edge (default: set in your environment)

## Troubleshooting

### Services won't start
1. Check Docker is running: `docker info`
2. Check ports aren't already in use: `netstat -tuln | grep -E '1433|9092|9093|8080|9090|3000|3100|8025'`
3. Check Docker logs: `docker compose logs -f`

### Cannot connect to services
1. Verify services are running: `docker compose ps`
2. Wait for health checks to pass
3. Check firewall settings

### Kafka authentication failures
See [kafka-security/README.md](kafka-security/README.md) for detailed troubleshooting steps.

## Production Deployment

⚠️ **IMPORTANT**: This configuration is designed for **DEVELOPMENT ONLY**.

For production deployments, see the comprehensive [Production Deployment Guide](../docs/production-deployment.md).

Key production considerations:
1. **Use CA-signed certificates** instead of self-signed
2. **Use strong passwords** (not defaults like "changeit" or "admin")
3. **Store credentials securely** using environment variables or secret management tools
4. **Enable SSL/TLS** for all services
5. **Configure proper network segmentation**
6. **Set up backup and disaster recovery**
7. **Review and harden security settings** in all configuration files
8. **Scale Kafka resources** (4-8 GB RAM, 4+ CPUs per broker)
9. **Use managed services** where appropriate (e.g., Azure SQL Database, Confluent Cloud)
10. **Implement monitoring and alerting**

## Additional Resources

- [Main Project README](../README.md)
- [Testing Guide](../docs/testing.md)
- [Kafka Security Documentation](kafka-security/README.md)
- [Keycloak Configuration](keycloak/README.md)
