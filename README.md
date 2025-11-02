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
- **JMeter Test Suite** for load and stress testing

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
- Kafka on port 9092 (PLAINTEXT) and 9093 (SASL_SSL)
- Keycloak on port 8080 (identity provider)

**Optional - Enable Kafka Security:**

To use secure Kafka connections with SSL/TLS and SASL authentication:

1. Generate SSL certificates (first-time only):
   ```bash
   cd kafka-security/certs && ./generate-certs.sh && cd ../..
   ```

2. After Kafka starts, initialize security configuration:
   ```bash
   ./kafka-security/init-kafka-security.sh
   ```

See the [Security Configuration](#security-configuration) section for details.

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

### Load and Stress Testing

The project includes comprehensive JMeter test suites for performance testing using Docker containers:

```bash
# Navigate to JMeter tests directory
cd jmeter-tests

# Run individual test (uses Docker - no JMeter installation needed)
./scripts/run-test.sh order    # Order Service load test
./scripts/run-test.sh outbox   # Outbox Service load test
./scripts/run-test.sh stress   # End-to-end stress test

# Run all tests sequentially
./scripts/run-all-tests.sh
```

See [jmeter-tests/README.md](jmeter-tests/README.md) for detailed documentation on:
- Test plan descriptions and configurations
- Performance benchmarks
- Running tests with custom parameters
- Analyzing results

**Prerequisites for load testing:**
1. Docker (tests run in JMeter Docker container - no local installation needed)
2. Both Order Service and Catbox Server running
3. Infrastructure (Azure SQL, Kafka) running via Docker Compose

## Project Structure

```
catbox-parent
├── catbox-common     # Shared code: OutboxEvent entity and repository
├── catbox-client     # Simple client for creating events (used by order-service)
├── catbox-server     # Standalone poller/publisher application (runs on 8081)
├── order-service     # Business service application (runs on 8080)
├── jmeter-tests      # JMeter load and stress test suites
├── monitoring        # Prometheus, Grafana, and Loki configurations
├── compose.yaml      # Docker Compose for infrastructure
└── pom.xml           # Parent POM
```

## Docker Compose Setup

The project includes a `compose.yaml` file that sets up:

1. **Azure SQL Edge** - Microsoft SQL Server compatible database
   - Port: 1433
   - Username: `sa`
   - Password: Set via `DB_PASSWORD` environment variable
   - Compatible with Azure SQL Database

2. **Apache Kafka** - Message broker using KRaft mode (no Zookeeper)
   - Port 9092 (PLAINTEXT - for backward compatibility)
   - Port 9093 (SASL_SSL - secure with authentication and encryption)
   - SSL/TLS encryption enabled
   - SASL SCRAM-SHA-512 authentication configured
   - ACL-based authorization enabled
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

## Security Configuration

The project supports comprehensive Kafka security features including SSL/TLS encryption, SASL authentication, and ACL-based access control.

### Security Features

1. **SSL/TLS Encryption** - Encrypts data in transit between clients and Kafka brokers
2. **SASL Authentication (SCRAM-SHA-512)** - Authenticates clients before allowing connections
3. **ACLs (Access Control Lists)** - Controls which users can access which topics

### Quick Start with Security

The Kafka broker is configured with two listeners:
- **Port 9092** - PLAINTEXT (for backward compatibility and testing)
- **Port 9093** - SASL_SSL (secure with authentication and encryption)

#### 1. Generate SSL Certificates

First-time setup requires generating SSL certificates:

```bash
cd kafka-security/certs
./generate-certs.sh
cd ../..
```

This creates:
- Self-signed Certificate Authority (CA)
- Kafka broker keystore with signed certificate
- Client truststore for secure connections
- JAAS configuration files for SASL authentication

#### 2. Start Kafka with Security Enabled

```bash
docker compose up -d kafka
```

The Kafka container will start with:
- SSL/TLS enabled on port 9093
- SASL SCRAM-SHA-512 authentication configured
- ACL authorization enabled (deny by default)

#### 3. Initialize Security Configuration

After Kafka starts, run the initialization script to create SASL users and configure ACLs:

```bash
./kafka-security/init-kafka-security.sh
```

This script:
- Creates SCRAM-SHA-512 credentials for admin, producer, and consumer users
- Creates test topics (OrderCreated, OrderStatusChanged)
- Configures ACLs to grant appropriate permissions

#### 4. Using Secure Kafka Connection

The application is pre-configured to use the secure cluster (cluster-b) on port 9093:

```yaml
kafka:
  clusters:
    cluster-b:
      bootstrap-servers: localhost:9093
      ssl:
        bundle: kafka-client
      properties:
        security.protocol: SASL_SSL
        sasl.mechanism: SCRAM-SHA-512
        sasl.jaas.config: ...username="producer" password="producer-secret"...
```

To route events to the secure cluster, update the routing rules in `application.yml`:

```yaml
outbox:
  routing:
    rules:
      OrderCreated: cluster-b        # Routes to secure cluster
      OrderStatusChanged: cluster-b  # Routes to secure cluster
```

### Security Credentials (Development)

**IMPORTANT**: These are development credentials. In production, use strong passwords and secure credential management.

**SASL Users:**
- Admin: `admin` / `admin-secret` (superuser - full access)
- Producer: `producer` / `producer-secret` (write access to order topics)
- Consumer: `consumer` / `consumer-secret` (read access to order topics)

**Keystore/Truststore Passwords:**
- All passwords: `changeit`

### Production Deployment

For production, refer to:
- `kafka-security/README.md` - Detailed security documentation
- `catbox-server/src/main/resources/application-production.yml.example` - Production configuration template

**Production Security Checklist:**
1. ✅ Use CA-signed certificates (not self-signed)
2. ✅ Store credentials in environment variables or secret management tools
3. ✅ Use strong passwords (not default "changeit")
4. ✅ Enable mutual TLS (mTLS) for additional security
5. ✅ Configure proper ACLs based on principle of least privilege
6. ✅ Enable Kafka audit logging
7. ✅ Rotate credentials regularly
8. ✅ Use network segmentation and firewalls

### Verifying Security Configuration

Check SCRAM users:
```bash
docker exec catbox-kafka kafka-configs.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --entity-type users
```

List configured ACLs:
```bash
docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --list
```

### Troubleshooting

**SSL Handshake Failures:**
- Verify truststore path is correct in application.yml
- Check certificate validity: `keytool -list -v -keystore kafka-security/certs/kafka-client-truststore.jks`

**SASL Authentication Failures:**
- Verify username/password in JAAS configuration
- Check that SCRAM users were created: see "Verifying Security Configuration" above

**ACL Permission Denied:**
- List ACLs to verify user has proper permissions
- Ensure super users are configured correctly in compose.yaml

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

## Security with Keycloak

The catbox-server supports OAuth2/OIDC authentication via Keycloak. Security is **disabled by default** and can be enabled via Spring Boot profile.

### Keycloak Setup

The Docker Compose configuration includes a Keycloak container that is automatically configured with:

- **Realm**: `catbox`
- **User**: `catbox` with password `catbox`
- **Client ID**: `catbox-server`
- **Client Secret**: `catbox-server-secret`

### Running with Security Enabled

To enable authentication, use the `secure` profile:

```bash
# Start Keycloak and other infrastructure
docker compose up -d

# Run catbox-server with security enabled
mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql,secure
```

### Accessing the Application

1. Navigate to `http://localhost:8081`
2. You will be redirected to Keycloak login page
3. Login with:
   - **Username**: `catbox`
   - **Password**: `catbox`
4. After successful authentication, you'll be redirected back to the application

### Keycloak Admin Console

Access the Keycloak admin console at `http://localhost:8080`:
- **Username**: `admin`
- **Password**: `admin`

From the admin console, you can:
- Add more users
- Configure additional clients
- Manage realm settings
- View user sessions and events

### Security Configuration

Security is configured via Spring profiles:

- **Default profile**: Security disabled (all requests permitted)
- **`secure` profile**: OAuth2/OIDC authentication enabled with Keycloak

The configuration allows:
- Unauthenticated access to health checks (`/actuator/health/**`) and metrics (`/actuator/prometheus`)
- All other endpoints require authentication

### Customizing Keycloak Configuration

The Keycloak realm configuration is defined in `keycloak/catbox-realm.json`. You can modify this file to:
- Add additional users
- Configure roles and permissions
- Set up client scopes
- Enable/disable features

After modifying the realm file, restart Keycloak:
```bash
docker compose restart keycloak
```
