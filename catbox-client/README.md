# Catbox Client

The Catbox Client library provides utilities for working with the transactional outbox pattern.

## Features

### OutboxClient

Write events to the outbox table:

```java
@Autowired
private OutboxClient outboxClient;

public void createOrder(Order order) {
    // Write both order and outbox event in same transaction
    orderRepository.save(order);
    outboxClient.write("Order", order.getId(), "OrderCreated", order);
}
```

### OutboxFilter - Production-Ready Deduplication for Kafka Consumers

The `OutboxFilter` provides a database-backed mechanism to deduplicate Kafka event listener messages based on correlation IDs and consumer groups. This is essential for implementing idempotent consumers that can handle at-least-once delivery semantics.

#### Quick Start

1. **Auto-Configuration**: The library automatically provides a `DatabaseOutboxFilter` bean that persists processed message records to the database.

2. **Usage in Kafka Listener**:

```java
@Service
public class OrderEventConsumer {
    
    @Autowired
    private OutboxFilter outboxFilter;
    
    @KafkaListener(topics = "OrderCreated", groupId = "order-processor")
    public void handleOrderEvent(String message, 
                                  @Header("correlationId") String correlationId) {
        // Check if this message has already been processed by this consumer group
        if (outboxFilter.deduped(correlationId, "order-processor")) {
            // This message has already been processed, acknowledge and skip
            log.info("Skipping duplicate message with correlationId: {}", correlationId);
            return;
        }
        
        // Process the message...
        processOrderCreatedEvent(message);
    }
}
```

#### API Reference

**`boolean deduped(String correlationId, String consumerGroup)`**
- Checks if a correlation ID has been processed by a specific consumer group
- On first call with a correlation ID for a consumer group, returns `false` and records it
- On subsequent calls with the same ID and group, returns `true`
- Thread-safe for concurrent use across multiple consumer instances
- Uses database for persistence (survives restarts)

**`void markProcessed(String correlationId, String consumerGroup)`**
- Manually marks a correlation ID as processed for a consumer group
- Useful for pre-populating the filter or recovery scenarios

**`boolean isProcessed(String correlationId, String consumerGroup)`**
- Read-only check if correlation ID has been processed by a consumer group
- Does not record the correlation ID

**`void markUnprocessed(String correlationId, String consumerGroup)`**
- Removes a processed message record, allowing reprocessing
- Available through the admin UI for manual intervention

#### Default Implementation: DatabaseOutboxFilter

The default `DatabaseOutboxFilter` implementation:
- **Persistent**: Uses a database table (`processed_messages`) for storage
- **Multi-instance safe**: Works correctly across multiple consumer instances
- **Per-consumer-group tracking**: Supports different consumer groups processing the same messages
- **Production-ready**: Designed for high-concurrency environments
- **Automatic archival**: Old records are archived to prevent unbounded growth

Database schema:
```sql
CREATE TABLE processed_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    correlation_id VARCHAR(255) NOT NULL,
    consumer_group VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL,
    event_type VARCHAR(100),
    aggregate_type VARCHAR(100),
    aggregate_id VARCHAR(100),
    UNIQUE INDEX idx_correlation_consumer (correlation_id, consumer_group),
    INDEX idx_processed_at (processed_at)
);
```

#### Admin UI Management

The Catbox admin UI (available at `/admin/processed-messages`) provides:
- **View processed messages**: Browse all processed messages with filtering by consumer group and correlation ID
- **Mark as unprocessed**: Manually remove processed message records to allow reprocessing
- **Statistics**: View counts and trends

Access the admin UI at: `http://localhost:8081/admin/processed-messages`

#### Archival

Old processed message records are automatically archived to prevent database growth:
- Default retention: Same as outbox events (configurable via `outbox.archival.retention-days`)
- Archive schedule: Daily at 3 AM (configurable via `outbox.processed-messages.archival.schedule`)
- Archived records are moved to `processed_messages_archive` table

#### Consumer Group Isolation

Each consumer group maintains its own set of processed messages:
```java
// Different groups can process the same correlation ID independently
OutboxFilter filter = ...;

// Group 1 processes the message
filter.deduped("corr-123", "group-1"); // returns false (first time)
filter.deduped("corr-123", "group-1"); // returns true (duplicate)

// Group 2 can also process the same message
filter.deduped("corr-123", "group-2"); // returns false (first time for this group)
filter.deduped("corr-123", "group-2"); // returns true (duplicate)
```

#### Testing

For testing, you can create a simple test implementation or use mocks:

```java
@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public OutboxFilter outboxFilter() {
        // Return a mock or simple test implementation
        return Mockito.mock(OutboxFilter.class);
    }
}
```

Alternatively, use an in-memory test implementation:

```java
// Simple test implementation for unit tests
public class TestOutboxFilter implements OutboxFilter {
    private final Map<String, Set<String>> processedIdsByGroup = new HashMap<>();

    @Override
    public boolean deduped(String correlationId, String consumerGroup) {
        if (correlationId == null || consumerGroup == null) return false;
        Set<String> ids = processedIdsByGroup
            .computeIfAbsent(consumerGroup, k -> new HashSet<>());
        return !ids.add(correlationId);
    }
    
    // Implement other methods...
}
```

For integration tests, use the real `DatabaseOutboxFilter` with a test database.

#### Use Cases

1. **Kafka Consumer Idempotency**: Prevent duplicate processing when Kafka delivers the same message multiple times
2. **Multi-Instance Deployments**: Ensure only one instance processes a given message per consumer group
3. **Recovery After Failures**: Track which messages have been successfully processed
4. **Event Replay**: Skip already-processed events during event replay scenarios
5. **Manual Reprocessing**: Mark messages as unprocessed through the admin UI to trigger reprocessing

#### Best Practices

1. **Always use consumer groups**: Pass the Kafka consumer group ID to the filter
2. **Use correlation IDs**: Ensure your outbox events have unique correlation IDs
3. **Monitor the admin UI**: Regularly check processed message counts and trends
4. **Configure archival**: Set appropriate retention periods for your use case
5. **Handle null correlation IDs**: The filter treats null/empty correlation IDs as not processed

#### Example with Correlation ID Generation

```java
@Service
public class OrderService {
    
    @Autowired
    private OutboxClient outboxClient;
    
    @Transactional
    public void createOrder(CreateOrderRequest request) {
        Order order = new Order(request);
        orderRepository.save(order);
        
        // Generate a unique correlation ID
        String correlationId = UUID.randomUUID().toString();
        
        // Write to outbox with correlation ID
        outboxClient.write(
            "Order", 
            order.getId(), 
            "OrderCreated", 
            correlationId,
            order
        );
    }
}
```

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>catbox-client</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

The library will auto-configure and provide:
- `OutboxClient` bean
- `OutboxFilter` bean (DatabaseOutboxFilter by default)

## Configuration

```yaml
# Archival configuration (applies to both outbox events and processed messages)
outbox:
  archival:
    retention-days: 30  # Keep records for 30 days before archiving
    schedule: "0 0 2 * * *"  # Run daily at 2 AM
  processed-messages:
    archival:
      schedule: "0 0 3 * * *"  # Run daily at 3 AM (after outbox archival)
```
