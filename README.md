# Catbox - Spring Boot WebMVC Application with Transactional Outbox Pattern

A Spring Boot 3.5.7 WebMVC application demonstrating the transactional outbox pattern using Java 21, Spring Data JPA, and virtual threads.

## Features

- **Spring Boot 3.5.7** with WebMVC
- **Java 21** with virtual threads for improved concurrency
- **Spring Data JPA** for database access
- **Transactional Outbox Pattern** for reliable event publishing
- **H2 In-Memory Database** for easy local development
- **RESTful API** for order management

## Architecture

The application implements a **high-performance transactional outbox pattern** with multi-node support:

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

2. **Event Processing** (Virtual Threads):
   - Each claimed event is processed in its own virtual thread
   - Virtual threads provide lightweight concurrency (thousands of threads possible)
   - Each virtual thread runs in a separate transaction (REQUIRES_NEW)

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

## Building the Application

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Create Order
```bash
POST /api/orders
Content-Type: application/json

{
  "customerName": "John Doe",
  "productName": "Laptop",
  "amount": 999.99
}
```

### Get All Orders
```bash
GET /api/orders
```

### Get Order by ID
```bash
GET /api/orders/{id}
```

### Update Order Status
```bash
PATCH /api/orders/{id}/status
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

### Get All Outbox Events
```bash
GET /api/outbox-events
```

### Get Pending Outbox Events
```bash
GET /api/outbox-events/pending
```

## Example Usage

1. Create an order:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Johnson",
    "productName": "Mechanical Keyboard",
    "amount": 149.99
  }'
```

2. View all orders:
```bash
curl http://localhost:8080/api/orders
```

3. View outbox events:
```bash
curl http://localhost:8080/api/outbox-events
```

4. Update order status:
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

The application uses H2 in-memory database. You can access the H2 console at:
```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:catboxdb`
- Username: `sa`
- Password: (empty)

## Transactional Outbox Pattern

### Polling Strategy

The `OutboxEventPublisher` service runs every **2 seconds**:

1. **Claim Events** (REQUIRES_NEW transaction):
   - Query: `SELECT FOR UPDATE SKIP LOCKED`
   - Batch size: 100 events
   - Sets `inProgressUntil = now + 5 minutes`
   - Row locks prevent concurrent claims

2. **Process Events** (Virtual Threads):
   - Each event spawns a virtual thread
   - `OutboxEventProcessor` runs in REQUIRES_NEW transaction
   - Publishes to Kafka/message broker
   - Sets `sentAt = now` on success

### Production Integration

Replace the simulated Kafka call in `OutboxEventProcessor.publishToKafka()`:

```java
// Current (demo):
Thread.sleep(100);

// Production:
kafkaTemplate.send(topic, event.getPayload()).get();
```

Use idempotent producer settings and consumer deduplication to handle at-least-once delivery.

## Testing

Run the tests with:
```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/catbox/
│   │   ├── CatboxApplication.java          # Main application class
│   │   ├── config/
│   │   │   └── VirtualThreadConfiguration.java  # Virtual threads config
│   │   ├── controller/
│   │   │   └── OrderController.java        # REST API endpoints
│   │   ├── entity/
│   │   │   ├── Order.java                  # Order entity
│   │   │   └── OutboxEvent.java            # Outbox event entity
│   │   ├── repository/
│   │   │   ├── OrderRepository.java        # Order repository
│   │   │   └── OutboxEventRepository.java  # Outbox repository
│   │   └── service/
│   │       ├── OrderService.java            # Order business logic
│   │       ├── OutboxEventPublisher.java    # Poller (claims events)
│   │       └── OutboxEventProcessor.java    # Processor (publishes events)
│   └── resources/
│       └── application.yml                 # Application configuration
└── test/
    └── java/com/example/catbox/
        ├── CatboxApplicationTests.java     # Context load test
        └── service/
            └── OrderServiceTest.java       # Service tests
```

## License

This project is open source and available under the MIT License.