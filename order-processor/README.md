# Order Processor

The Order Processor is a Kafka consumer application that demonstrates the use of the OutboxFilter for message deduplication. It subscribes to order event topics and processes them with guaranteed exactly-once semantics using the database-backed deduplication filter.

## Features

- **Kafka Event Consumption**: Listens to `OrderCreated` and `OrderStatusChanged` topics
- **Deduplication**: Uses `OutboxFilter` to prevent duplicate message processing
- **Manual Acknowledgment**: Fine-grained control over message acknowledgment
- **Simulated Processing**: Demonstrates happy path, duplicate handling, and intermittent failures
- **Virtual Threads**: Leverages Java 21 virtual threads for efficient concurrency
- **Database Persistence**: Tracks processed messages in the database for multi-instance safety

## Architecture

The Order Processor follows a clean architecture with:

```
order-processor/
├── config/           # Configuration classes (Kafka, Jackson)
├── listener/         # Kafka listeners with OutboxFilter integration
├── model/            # Event payload models
└── service/          # Business logic for processing events
```

## How It Works

### 1. Event Reception

The `OrderEventListener` subscribes to Kafka topics and receives messages:

```java
@KafkaListener(topics = "OrderCreated", groupId = "order-processor")
public void handleOrderCreated(
    @Payload String message,
    @Header("correlationId") String correlationId,
    Acknowledgment acknowledgment)
```

### 2. Deduplication Check

Before processing, the listener checks if the message has already been processed:

```java
if (outboxFilter.deduped(correlationId, "order-processor")) {
    log.info("Skipping duplicate message - correlationId: {}", correlationId);
    acknowledgment.acknowledge();
    return;
}
```

The `OutboxFilter.deduped()` method:
- Returns `false` and records the correlation ID on first call
- Returns `true` on subsequent calls with the same correlation ID
- Uses a database table for persistence across restarts
- Supports multiple consumer groups independently

### 3. Processing

If not a duplicate, the message is parsed and processed:

```java
OrderCreatedPayload payload = objectMapper.readValue(message, OrderCreatedPayload.class);
processingService.processOrderCreated(payload, correlationId);
```

### 4. Acknowledgment

Messages are acknowledged only after successful processing:

```java
acknowledgment.acknowledge();
```

If processing fails with an intermittent error, the message is **not acknowledged**, allowing Kafka to redeliver it for retry.

## Simulated Processing Logic

The `OrderEventProcessingService` simulates realistic scenarios:

- **Happy Path (90%)**: Successful processing with simulated business logic
- **Intermittent Failures (10%)**: Random failures that trigger message retry
- **Duplicate Handling**: Duplicates are filtered out by OutboxFilter before reaching the service

## Configuration

### Application Properties

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=catbox
    username: sa
    password: ${DB_PASSWORD}
  
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: order-processor
      auto-offset-reset: earliest
      enable-auto-commit: false
```

### Kafka Consumer Configuration

- **Manual Acknowledgment**: `enable-auto-commit: false`
- **Earliest Offset**: Processes all messages from the beginning if no offset exists
- **Concurrency**: 3 consumer threads using virtual threads

## Running the Application

### Prerequisites

1. **Java 21** or higher
2. **Running infrastructure**:
   - SQL Server (Azure SQL Edge)
   - Kafka
   - RouteBox Server (to publish events)
   - Order Service (to create events)

### Start Infrastructure

```bash
cd infrastructure
docker compose up -d
```

### Create Database

```bash
DB_PASSWORD="YourStrong!Passw0rd"
docker exec routebox-azuresql /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "${DB_PASSWORD}" \
  -Q "CREATE DATABASE catbox" -C -No
```

### Run the Order Processor

```bash
# From the project root
mvn spring-boot:run -pl order-processor -Dspring-boot.run.profiles=azuresql

# Or use the convenience script
./run-order-processor.sh
```

The application will start on port 8082.

## Testing

### Unit Tests

```bash
mvn test -pl order-processor
```

Unit tests cover:
- Listener behavior with deduplication
- Service processing logic
- Error handling

### Integration Tests

The E2E tests (`OrderProcessorE2ETest`) prove the deduplication logic works:

1. **Happy Path**: Single message is processed successfully
2. **Deduplication**: Duplicate messages are filtered out (3 deliveries → 1 processed)
3. **Multiple Unique Messages**: All unique messages are processed
4. **Mixed Scenario**: Combination of unique and duplicate messages
5. **No Correlation ID**: Messages without correlation ID are still processed

Run integration tests:

```bash
mvn verify -pl order-processor
```

## Monitoring Processing

### View Logs

```bash
# Filter for processing logs
docker logs -f order-processor | grep "Processing\|Skipping"
```

### Sample Log Output

```
INFO  OrderEventListener - Received OrderCreated message - correlationId: abc123
INFO  OrderEventProcessingService - Processing OrderCreated event - correlationId: abc123, orderId: 1
INFO  OrderEventProcessingService - Successfully processed OrderCreated - correlationId: abc123
INFO  OrderEventListener - Acknowledged OrderCreated message - correlationId: abc123

INFO  OrderEventListener - Received OrderCreated message - correlationId: abc123
INFO  OrderEventListener - Skipping duplicate OrderCreated message - correlationId: abc123
```

### Check Processed Messages

Connect to the database and query the processed messages table:

```sql
SELECT correlation_id, consumer_group, processed_at, event_type
FROM processed_messages
WHERE consumer_group = 'order-processor'
ORDER BY processed_at DESC;
```

## Deduplication Details

### Database Table

The `processed_messages` table tracks which messages have been processed:

```sql
CREATE TABLE processed_messages (
    id BIGINT PRIMARY KEY IDENTITY,
    correlation_id VARCHAR(255) NOT NULL,
    consumer_group VARCHAR(100) NOT NULL,
    processed_at DATETIME2(6) NOT NULL,
    event_type VARCHAR(100),
    aggregate_type VARCHAR(100),
    aggregate_id VARCHAR(100),
    UNIQUE INDEX idx_correlation_consumer (correlation_id, consumer_group)
);
```

### Multi-Instance Safety

The deduplication mechanism is safe for running multiple instances:
- Unique constraint on `(correlation_id, consumer_group)` prevents duplicates
- Database transactions ensure atomic check-and-insert
- Race conditions are handled gracefully

### Consumer Group Isolation

Each consumer group maintains its own set of processed messages:
- Different groups can process the same correlation ID independently
- Supports multiple downstream consumers for the same events

## Use Cases

This module demonstrates patterns useful for:

1. **Event-Driven Microservices**: Consuming events with exactly-once processing
2. **Message Replay**: Safely reprocessing messages without duplicates
3. **Multi-Instance Deployments**: Running multiple consumers safely
4. **Failure Recovery**: Handling transient failures with automatic retry
5. **Order Processing**: Real-world order fulfillment workflows

## Extending the Processor

To add new event types:

1. **Create Payload Model**:
```java
public record OrderShippedPayload(
    Long orderId,
    String trackingNumber,
    String carrier
) {}
```

2. **Add Listener Method**:
```java
@KafkaListener(topics = "OrderShipped", groupId = "order-processor")
public void handleOrderShipped(
    @Payload String message,
    @Header("correlationId") String correlationId,
    Acknowledgment acknowledgment) {
    // ... deduplication and processing logic
}
```

3. **Implement Processing Logic**:
```java
public void processOrderShipped(OrderShippedPayload payload, String correlationId) {
    // ... business logic
}
```

## Performance Considerations

- **Virtual Threads**: Uses Java 21 virtual threads for efficient concurrency
- **Batch Processing**: Kafka consumers can process messages in batches
- **Database Indexes**: Unique index on correlation ID for fast lookups
- **Connection Pooling**: Database connection pool for optimal performance

## Troubleshooting

### Messages Not Being Consumed

1. Check Kafka connection:
```bash
docker logs order-processor | grep "Kafka"
```

2. Verify topics exist:
```bash
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

3. Check consumer group offset:
```bash
docker exec kafka kafka-consumer-groups --describe \
  --group order-processor --bootstrap-server localhost:9092
```

### Duplicate Processing

If messages are being processed multiple times:

1. Verify correlation IDs are being set correctly
2. Check database connectivity
3. Look for transaction rollbacks in logs
4. Query `processed_messages` table for the correlation ID

### Performance Issues

1. Adjust consumer concurrency in `KafkaConsumerConfig`
2. Tune Kafka consumer settings (`max.poll.records`, `fetch.min.bytes`)
3. Monitor database query performance
4. Consider archiving old processed messages

## Related Components

- **routebox-client**: Provides the `OutboxFilter` interface and `DatabaseOutboxFilter` implementation
- **routebox-server**: Publishes events to Kafka from the outbox table
- **order-service**: Creates orders and writes to the outbox table
- **routebox-common**: Contains shared entities and repositories

## License

This is part of the RouteBox project and follows the same license.
