# Minimal Docker Compose Configuration

This minimal compose file (`compose-minimal.yaml`) provides a simplified infrastructure setup with only the essential services needed for RouteBox development.

## Services Included

- **Azure SQL Edge** - Database server on port 1433
- **Kafka** - Message broker on port 9092 (PLAINTEXT only)

## Services NOT Included

This minimal configuration excludes the following services that are in the full `compose.yaml`:
- Second Kafka cluster (kafka-2)
- Kafka UI
- Keycloak (authentication)
- Prometheus (metrics)
- Alertmanager (alerts)
- Mailhog (email testing)
- Grafana (dashboards)
- Loki (log aggregation)
- Promtail (log shipping)
- Tempo (distributed tracing)

## Usage

1. Create a `.env` file with the required database password:
   ```bash
   echo "DB_PASSWORD=YourStrong!Passw0rd" > .env
   ```

2. Start the minimal infrastructure:
   ```bash
   cd infrastructure
   docker compose -f compose-minimal.yaml up -d
   ```

3. Create the database:
   ```bash
   docker exec routebox-azuresql /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -Q "CREATE DATABASE routebox" -C -No
   ```

4. Stop the services when done:
   ```bash
   docker compose -f compose-minimal.yaml down
   ```

## Connection Details

- **Azure SQL**: `localhost:1433` (user: `sa`, password: from `.env` file)
- **Kafka**: `localhost:9092` (PLAINTEXT protocol)

## When to Use This

Use this minimal configuration when:
- You only need basic database and messaging functionality
- You want faster startup times
- You're working on core application features that don't require monitoring/alerting
- You have limited system resources

Use the full `compose.yaml` when:
- You need monitoring and observability features
- You're testing multi-cluster Kafka scenarios
- You need authentication/authorization with Keycloak
- You're working on alerting or monitoring features
