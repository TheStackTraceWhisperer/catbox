# Quick Start Guide

This guide will help you get Catbox up and running quickly.

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (optional, for running Azure SQL and Kafka)

## Quick Start

To run the system, you must start both applications (and the required Docker services).

### Step 1: Start Infrastructure

First, create a `.env` file in the project root to configure the database password:

```bash
# Create .env file with database password (minimum 8 characters required)
cat > .env << 'EOL'
DB_PASSWORD=YourStrong!Passw0rd
EOL
```

**Important:** The Azure SQL Edge container requires a strong password with at least 8 characters, including uppercase, lowercase, numbers, and special characters.

Then start the infrastructure:

```bash
cd infrastructure && docker compose up -d
```

This starts:
- Azure SQL Edge on port 1433
- Kafka Cluster 1 on ports 9092 (PLAINTEXT) and 9093 (SASL_SSL)
- Kafka Cluster 2 on port 9095 (PLAINTEXT) - for multi-cluster testing
- Kafka UI on port 8090 (web interface for both clusters)
- Keycloak on port 8180 (identity provider)
- Monitoring stack (Prometheus, Grafana, Loki)

### Step 2: Create the Database

The applications require a database named `catbox`. Create it manually:

```bash
# Create the catbox database
docker exec catbox-azuresql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -Q "CREATE DATABASE catbox" -C -No
```

Or if you prefer, connect using your favorite SQL client to `localhost:1433` with username `sa` and the password from your `.env` file, then run `CREATE DATABASE catbox`.

### Step 3: Run the order-service

In one terminal:
```bash
# From the project root
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql
```

### Step 4: Run the catbox-server

In a second terminal:
```bash
# From the project root
mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql
```

The `order-service` will be available at `http://localhost:8080` and the `catbox-server` will be available at `http://localhost:8081`.

## Building the Application

```bash
mvn clean install
```

## Example Usage

1. Create an order (order-service on port 8080):
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Johnson",
    "productName": "Mechanical Keyboard",
    "amount": 149.99
  }'
```

2. View all orders (order-service on port 8080):
```bash
curl http://localhost:8080/api/orders
```

3. View outbox events (catbox-server on port 8081):
```bash
curl http://localhost:8081/api/outbox-events
```

4. Update order status (order-service on port 8080):
```bash
curl -X PATCH http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
```

## Next Steps

- See [API Reference](api-reference.md) for complete API documentation
- See [Docker Setup](docker-setup.md) for infrastructure configuration
- See [Security](security.md) for Kafka SSL/SASL and Keycloak setup
- See [Monitoring](monitoring.md) for observability and metrics
