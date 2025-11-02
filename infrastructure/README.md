# Infrastructure

This directory contains all Docker infrastructure files and configurations for the Catbox project.

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
- **Apache Kafka** - Message broker using KRaft mode
  - Port 9092 (PLAINTEXT - for backward compatibility)
  - Port 9093 (SASL_SSL - secure with authentication and encryption)

### Identity & Access Management
- **Keycloak** - OAuth2/OIDC provider on port 8080

### Observability
- **Prometheus** - Metrics collection on port 9090
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
- Client ID: `catbox-server`

### Monitoring Configuration

The monitoring stack includes pre-configured dashboards and data sources:

- **Prometheus**: Scrapes metrics from both order-service (port 8080) and catbox-server (port 8081)
- **Grafana**: Includes a pre-configured dashboard at `monitoring/grafana/dashboards/catbox-dashboard.json`
- **Loki**: Aggregates logs from all Docker containers via Promtail

## Environment Variables

The following environment variables can be set before starting services:

- `DB_PASSWORD` - Password for Azure SQL Edge (default: set in your environment)

## Troubleshooting

### Services won't start
1. Check Docker is running: `docker info`
2. Check ports aren't already in use: `netstat -tuln | grep -E '1433|9092|9093|8080|9090|3000|3100'`
3. Check Docker logs: `docker compose logs -f`

### Cannot connect to services
1. Verify services are running: `docker compose ps`
2. Wait for health checks to pass
3. Check firewall settings

### Kafka authentication failures
See [kafka-security/README.md](kafka-security/README.md) for detailed troubleshooting steps.

## Production Deployment

⚠️ **IMPORTANT**: This configuration is designed for **DEVELOPMENT ONLY**.

Before deploying to production:

1. **Use CA-signed certificates** instead of self-signed
2. **Use strong passwords** (not defaults like "changeit" or "admin")
3. **Store credentials securely** using environment variables or secret management tools
4. **Enable SSL/TLS** for all services
5. **Configure proper network segmentation**
6. **Set up backup and disaster recovery**
7. **Review and harden security settings** in all configuration files

## Additional Resources

- [Main Project README](../README.md)
- [Testing Guide](../TESTING.md)
- [Kafka Security Documentation](kafka-security/README.md)
- [Keycloak Configuration](keycloak/README.md)
