# Catbox - Transactional Outbox Pattern

A Spring Boot 3.5.7 application demonstrating the transactional outbox pattern with Java 21 virtual threads, multi-cluster Kafka routing, and comprehensive observability.

## Features

- **Transactional Outbox Pattern** - Reliable event publishing with at-least-once delivery guarantees
- **Java 21 Virtual Threads** - High-performance concurrent event processing
- **Multi-Cluster Kafka Routing** - Flexible routing strategies for geographic replication and high availability
- **Admin Web UI** - Professional dashboard for monitoring and managing outbox events
- **Event Lifecycle Management** - Automatic archival and dead letter queue for failed events
- **Spring Boot 3.5.7** - Modern Spring ecosystem with WebMVC, Data JPA, and Kafka
- **Comprehensive Observability** - Custom Prometheus metrics, Grafana dashboards, Loki log aggregation, and alerting with Alertmanager
- **Production-Ready Alerting** - 12 pre-configured alerts for outbox health, application status, and JVM metrics with email notifications
- **Security** - OAuth2/OIDC with Keycloak, Kafka SSL/SASL authentication
- **Docker Compose** - Full infrastructure stack with Azure SQL Edge, Kafka, and monitoring
- **Load Testing** - JMeter test suites for performance validation
- **Architecture Testing** - ArchUnit for automated design validation

## Quick Start

See the [Quick Start Guide](docs/quick-start.md) for detailed setup instructions.

### Prerequisites

- Java 21 or higher (required - enforced by Maven Enforcer Plugin)
  - A `.java-version` file is provided for SDK version managers (jenv, asdf, etc.)
- Maven 3.6+
- Docker and Docker Compose

### Basic Setup

1. Create `.env` file with database password:
   ```bash
   echo "DB_PASSWORD=YourStrong!Passw0rd" > .env
   ```

2. Start infrastructure:
   ```bash
   cd infrastructure && docker compose up -d
   ```

3. Create database:
   ```bash
   docker exec catbox-azuresql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -Q "CREATE DATABASE catbox" -C -No
   ```

4. Run applications:
   ```bash
   # Terminal 1 - Order Service (port 8080)
   mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql
   
   # Terminal 2 - Catbox Server (port 8081)
   mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql
   
   # Terminal 3 - Order Processor (port 8082)
   mvn spring-boot:run -pl order-processor -Dspring-boot.run.profiles=azuresql
   ```

5. Test the API:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{"customerName": "Alice", "productName": "Widget", "amount": 99.99}'
   ```

### SQL Debug Profile

By default, Hibernate SQL logging is disabled for cleaner console output. To enable SQL logging for debugging:

```bash
# Run with SQL debug profile
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql,sql-debug
mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql,sql-debug
```

The `sql-debug` profile enables:
- SQL statement output (`show-sql: true`)
- SQL formatting (`format_sql: true`)
- SQL parameter binding (`org.hibernate.type.descriptor.sql.BasicBinder: TRACE`)

## Architecture

The system uses a decoupled, multi-module architecture with three Spring Boot applications:

- **order-service (Port 8080)** - Business service that creates orders and writes outbox events in the same transaction
- **catbox-server (Port 8081)** - Standalone processor that polls and publishes events to Kafka
- **order-processor (Port 8082)** - Kafka consumer that processes order events with deduplication

This separation allows independent scaling and keeps business logic lightweight.

For detailed architecture information, see [Architecture Documentation](docs/architecture.md).

## Documentation

- **[Quick Start Guide](docs/quick-start.md)** - Setup and installation instructions
- **[Architecture](docs/architecture.md)** - System design and outbox pattern implementation
- **[API Reference](docs/api-reference.md)** - REST API endpoints and examples
- **[Docker Setup](docs/docker-setup.md)** - Infrastructure services and Docker Compose
- **[Security](docs/security.md)** - Kafka SSL/SASL and Keycloak OAuth2 configuration
- **[Monitoring](docs/monitoring.md)** - Metrics, dashboards, and observability
- **[Multi-Cluster Routing](docs/multi-cluster-routing.md)** - Advanced Kafka routing strategies
- **[Virtual Threads](docs/virtual-threads.md)** - Java 21 virtual threads implementation
- **[Testing Guide](docs/testing.md)** - Testing framework and coverage reports
- **[Troubleshooting](docs/troubleshooting.md)** - Known issues and workarounds

## Building and Testing

### Build the Application

```bash
mvn clean verify
```

### Run Tests

```bash
mvn test
```

### Generate Coverage Reports

```bash
mvn clean verify
open coverage-report/target/site/jacoco-aggregate/index.html
```

See [Testing Guide](docs/testing.md) for comprehensive testing documentation.

### Load Testing

```bash
cd jmeter-tests
./scripts/run-test.sh stress
```

See [jmeter-tests/README.md](jmeter-tests/README.md) for performance testing details.

## Project Structure

```
catbox-parent
├── catbox-common        # Shared code: OutboxEvent entity and repository
├── catbox-client        # Client library for creating events
├── catbox-server        # Standalone event processor (port 8081)
├── order-service        # Business service (port 8080)
├── order-processor      # Order event consumer with deduplication (port 8082)
├── catbox-archunit      # Architecture testing with ArchUnit
├── coverage-report      # Aggregated test coverage reports
├── jmeter-tests         # JMeter load and stress test suites
├── infrastructure       # Docker Compose and infrastructure
│   ├── compose.yaml        # Docker Compose configuration
│   ├── monitoring          # Prometheus, Grafana, Loki
│   ├── kafka-security      # Kafka SSL/TLS and SASL
│   └── keycloak            # Keycloak realm configuration
├── docs                 # Documentation
└── pom.xml              # Parent POM
```

## Contributing

Contributions are welcome! Please ensure:
- All tests pass: `mvn test`
- Code coverage is maintained
- Documentation is updated for new features

## License

This project is open source and available under the MIT License.
