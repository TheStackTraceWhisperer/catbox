# Catbox - Spring Boot WebMVC Application with Transactional Outbox Pattern

A Spring Boot 3.5.7 WebMVC application demonstrating the transactional outbox pattern using Java 21, Spring Data JPA, Spring Kafka, and Docker Compose.

## Features

- **Spring Boot 3.5.7** with WebMVC
- **Java 21** (with virtual threads for high-performance concurrent processing)
- **Spring Data JPA** for database access
- **Spring Kafka** for event publishing
- **Transactional Outbox Pattern** for reliable event publishing
- **Docker Compose** support for Azure SQL Edge and Kafka
- **H2 In-Memory Database** for easy local development
- **RESTful API** for order management
- **Comprehensive Testing** with unit and integration tests

## Architecture

This project demonstrates a transactional outbox pattern using a decoupled, multi-module architecture. The system is composed of two primary Spring Boot applications:

* **`order-service` (Port 8080):** A business-facing service responsible for creating and updating orders. When it writes to its `orders` table, it also writes an `OutboxEvent` to a shared table in the *same transaction*, ensuring data consistency.
* **`catbox-server` (Port 8081):** A standalone processor that polls the `outbox_events` table. It uses a `SELECT FOR UPDATE SKIP LOCKED` query (via `OutboxEventClaimer`) to safely claim events and publish them to Kafka using a dynamic, multi-cluster routing factory.

This separation ensures that the business service (`order-service`) is lightweight and not burdened with event publishing logic, while the `catbox-server` can be scaled independently to handle event throughput.

### Phase 1: Order Submission (Atomic Transaction)

When an order is created or updated:
1. Both the **Order** and **OutboxEvent** are saved in the **same database transaction** (REQUIRES_NEW)
2. This ensures atomicity - either both are saved or neither is saved
3. Prevents dual-write inconsistencies

### Phase 2: Event Publishing (Concurrent Multi-Node Processing)

The poller runs every 2 seconds on each node:

1. **Event Claiming** (Transaction with REQUIRES_NEW):
   - Uses `SELECT FOR UPDATE SKIP LOCKED` for row-level pessimistic locking
   - Each node claims a batch of unclaimed events
   - `inProgressUntil` field prevents other nodes from processing the same events
   - Locked rows are skipped by other nodes, enabling true concurrent processing

2. **Event Processing** (Concurrent Threads):
   - Each claimed event is processed in its own thread
   - For Java 21+, virtual threads provide lightweight concurrency (thousands of threads possible)
   - Each thread runs in a separate transaction (REQUIRES_NEW)

3. **Publishing & Marking**:
   - Event is published to Kafka/message broker
   - On success, `sentAt` is set and `inProgressUntil` is cleared
   - On failure, `inProgressUntil` expires after 5 minutes, allowing retry

4. **At-Least-Once Delivery**:
   - If Kafka send succeeds but DB update fails, event will be retried
   - Idempotent producer keys and consumer deduplication prevent duplicates

This design allows **horizontal scaling** across multiple nodes while maintaining exactly-once processing guarantees.

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (optional, for running Azure SQL and Kafka)

## Quick Start

To run the system, you must start both applications (and the required Docker services).

### Start Infrastructure

```bash
docker compose up -d
```

This starts:
- Azure SQL Edge on port 1433
- Kafka on port 9092

### Run the order-service

In one terminal:
```bash
# From the project root
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql
```

### Run the catbox-server

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

## API Endpoints

### Order Service (http://localhost:8080)

* `POST /api/orders`: Create a new order.
* `GET /api/orders`: Get all orders.
* `GET /api/orders/{id}`: Get a single order by ID.
* `PATCH /api/orders/{id}/status`: Update an order's status.

### Catbox Server (http://localhost:8081)

* `GET /api/outbox-events`: Get all outbox events.
* `GET /api/outbox-events/pending`: Get only unsent events.
* `GET /api/outbox-events/search`: Paginated search for events.
* `POST /api/outbox-events/{id}/mark-unsent`: Manually mark a sent event for reprocessing.

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

## Virtual Threads

The application leverages Java 21 virtual threads for high-performance concurrent processing:

- **Web Request Handling**: Tomcat protocol handler uses virtual thread executor
- **Async Task Execution**: Spring's async task executor uses virtual threads
- **Event Processing**: Each outbox event is processed in its own virtual thread

Virtual threads are lightweight (thousands can run concurrently) and provide better scalability than platform threads, especially for I/O-bound operations like Kafka publishing.

## High-Performance Outbox Implementation

### Key Features

1. **Row-Level Locking**: `SELECT FOR UPDATE SKIP LOCKED` prevents lock contention
2. **Multi-Node Support**: Multiple application instances can run concurrently
3. **Event Claiming**: `inProgressUntil` field tracks which node is processing each event
4. **Retry Logic**: Failed events automatically retry after 5 minutes
5. **Separate Transactions**: Polling and processing use different transactions (REQUIRES_NEW)
6. **Virtual Thread Workers**: Each event gets its own lightweight thread

### Outbox Event Lifecycle

- **Created**: `sentAt = NULL`, `inProgressUntil = NULL`
- **Claimed**: `inProgressUntil = now + 5 minutes`
- **Sent**: `sentAt = now`, `inProgressUntil = NULL`
- **Retry**: If `inProgressUntil` expires without `sentAt`, event is reclaimed

## Database

The applications use Azure SQL Edge when running with Docker Compose. The `catbox-server` also supports H2 in-memory database for development/testing without the `azuresql` profile.

## Transactional Outbox Pattern

### Polling Strategy

The `OutboxEventPoller` service runs every **2 seconds**:

1. **Claim Events** (REQUIRES_NEW transaction):
   - Query: `SELECT FOR UPDATE SKIP LOCKED`
   - Batch size: 100 events
   - Sets `inProgressUntil = now + 5 minutes`
   - Row locks prevent concurrent claims

2. **Process Events** (Virtual Threads):
   - Each event spawns a virtual thread
   - `OutboxEventPublisher` runs in REQUIRES_NEW transaction
   - Publishes to Kafka/message broker
   - Sets `sentAt = now` on success

### Production Integration

Replace the simulated Kafka call in `OutboxEventPublisher.publishToKafka()`:

```java
// Current (demo):
Thread.sleep(100);

// Production:
kafkaTemplate.send(topic, event.getPayload()).get();
```

Use idempotent producer settings and consumer deduplication to handle at-least-once delivery.

## Testing

The project includes comprehensive unit and integration tests.

### Run All Tests

```bash
mvn test
```

### Test Coverage

1. **Unit Tests**:
   - `OrderServiceTest` - Tests for order creation and status updates with outbox events
   - `KafkaTemplateConfigTest` - Tests for Kafka configuration beans

2. **Integration Tests**:
   - `KafkaIntegrationTest` - Integration test using EmbeddedKafka for message publishing
   - `CatboxApplicationTests` - Context load test

### Testing with Docker Compose

To test with the actual Kafka and Azure SQL services:

```bash
# Start services
docker compose up -d

# Wait for services to be ready
sleep 10

# Run tests with Azure SQL profile
mvn test -Dspring.profiles.active=azuresql

# Stop services
docker compose down
```

### Testing Framework

- **JUnit 5** for test structure
- **AssertJ** for fluent assertions
- **Spring Kafka Test** with EmbeddedKafka for integration tests
- **Testcontainers** for containerized testing support
- **Spring Boot Test** for application context testing

## Project Structure

```
catbox-parent
├── catbox-common     # Shared code: OutboxEvent entity and repository
├── catbox-client     # Simple client for creating events (used by order-service)
├── catbox-server     # Standalone poller/publisher application (runs on 8081)
├── order-service     # Business service application (runs on 8080)
├── monitoring        # Prometheus, Grafana, and Loki configurations
├── compose.yaml      # Docker Compose for infrastructure
└── pom.xml           # Parent POM
```

## Docker Compose Setup

The project includes a `compose.yaml` file that sets up:

1. **Azure SQL Edge** - Microsoft SQL Server compatible database
   - Port: 1433
   - Username: `sa`
   - Password: `YourStrong@Passw0rd`
   - Compatible with Azure SQL Database

2. **Apache Kafka** - Message broker using KRaft mode (no Zookeeper)
   - Port: 9092
   - 3 partitions for outbox events
   - Ready for horizontal scaling

### Using Docker Compose

Start services:
```bash
docker compose up -d
```

Check service health:
```bash
docker compose ps
```

View logs:
```bash
docker compose logs -f
```

Stop services:
```bash
docker compose down
```

Clean up volumes:
```bash
docker compose down -v
```

### Connecting to Azure SQL

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
- Database: Azure SQL Edge
- Kafka: localhost:9092 (catbox-server only)

## License

This project is open source and available under the MIT License.
## Observability & Metrics

The application includes comprehensive custom metrics for monitoring the outbox pattern health and performance.

### Custom Outbox Metrics

All metrics are exposed via the Prometheus actuator endpoint at `/actuator/prometheus`.

#### Gauges
- **`outbox_events_pending`** - Number of pending events in the outbox (not yet sent)
- **`outbox_events_oldest_age_seconds`** - Age of the oldest unsent event (in seconds)

#### Counters
- **`outbox_events_published_success_total`** - Total successful event publications
- **`outbox_events_published_failure_total`** - Total failed event publications

#### Histograms
- **`outbox_events_processing_duration_seconds`** - Event processing duration from claim to publish (includes p50, p95, p99 percentiles)

### Grafana Dashboard

A pre-configured Grafana dashboard is available in `monitoring/grafana/dashboards/catbox-dashboard.json` with:
- **Outbox Pending Events** gauge
- **Oldest Unsent Event Age** gauge  
- **Event Publishing Rate** (success/failure) timeseries
- **Event Processing Duration** percentiles (p50, p95, p99)
- **Total Events Published** counters

### Accessing Metrics

View metrics locally:
```bash
curl http://localhost:8080/actuator/prometheus | grep outbox_events
```

### Monitoring Recommendations

**Set up alerts for:**
- Pending event count > 100 (potential backlog)
- Oldest event age > 300 seconds (5 minutes - processing delays)
- Sustained increase in failure counter

**Monitor trends:**
- Publishing throughput over time
- Processing duration percentiles
- Success/failure ratios
