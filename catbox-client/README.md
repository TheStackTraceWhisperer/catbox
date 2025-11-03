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

### OutboxFilter - Deduplication for Kafka Consumers

The `OutboxFilter` provides a mechanism to deduplicate Kafka event listener messages based on correlation IDs. This is essential for implementing idempotent consumers that can handle at-least-once delivery semantics.

#### Quick Start

1. **Auto-Configuration**: The library automatically provides an `InMemoryOutboxFilter` bean when you include the dependency.

2. **Usage in Kafka Listener**:

```java
@Service
public class OrderEventConsumer {
    
    @Autowired
    private OutboxFilter outboxFilter;
    
    @KafkaListener(topics = "OrderCreated")
    public void handleOrderEvent(String message, 
                                  @Header("correlationId") String correlationId) {
        // Check if this message has already been processed
        if (outboxFilter.deduped(correlationId)) {
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

**`boolean deduped(String correlationId)`**
- Checks if a correlation ID has been processed before
- On first call with a correlation ID, returns `false` and records it
- On subsequent calls with the same ID, returns `true`
- Thread-safe for concurrent use

**`void markProcessed(String correlationId)`**
- Manually marks a correlation ID as processed
- Useful for pre-populating the filter or recovery scenarios

**`boolean isProcessed(String correlationId)`**
- Read-only check if correlation ID has been processed
- Does not record the correlation ID

#### Default Implementation

The default `InMemoryOutboxFilter` implementation:
- Uses a `ConcurrentHashMap` for thread-safe operations
- Stores correlation IDs in memory (state lost on restart)
- Provides O(1) lookup performance
- Suitable for development and non-critical use cases

#### Custom Implementation

For production use cases requiring persistence across restarts, you can provide your own implementation:

```java
@Configuration
public class OutboxFilterConfig {
    
    @Bean
    public OutboxFilter outboxFilter(OutboxEventRepository repository) {
        return new DatabaseOutboxFilter(repository);
    }
}

public class DatabaseOutboxFilter implements OutboxFilter {
    
    private final OutboxEventRepository repository;
    
    public DatabaseOutboxFilter(OutboxEventRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public boolean deduped(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            return false;
        }
        
        // Check if correlation ID exists in database
        boolean exists = repository.existsByCorrelationId(correlationId);
        
        if (!exists) {
            // Create a processed event record
            ProcessedEvent event = new ProcessedEvent(correlationId);
            repository.save(event);
            return false;
        }
        
        return true;
    }
    
    // Implement other methods...
}
```

#### Use Cases

1. **Kafka Consumer Idempotency**: Prevent duplicate processing when Kafka delivers the same message multiple times
2. **Multi-Instance Deployments**: Ensure only one instance processes a given correlation ID
3. **Recovery After Failures**: Track which messages have been successfully processed
4. **Event Replay**: Skip already-processed events during event replay scenarios

#### Best Practices

1. **Always use correlation IDs**: Ensure your outbox events have unique correlation IDs
2. **Persistent implementation for production**: Use a database-backed filter for production environments
3. **Cleanup strategy**: Implement a cleanup strategy to prevent unbounded growth of processed IDs
4. **Monitoring**: Monitor the filter's size and performance
5. **Null handling**: The filter handles null/empty correlation IDs gracefully

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
- `OutboxFilter` bean (InMemoryOutboxFilter by default)
