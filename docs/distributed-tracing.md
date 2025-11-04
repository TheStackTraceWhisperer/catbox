# Distributed Tracing with OpenTelemetry and Tempo

This guide explains how to use distributed tracing in the RouteBox project to trace requests across services and observe event processing lifecycles.

## Overview

The RouteBox project implements distributed tracing using:
- **OpenTelemetry** - Industry-standard tracing instrumentation
- **Micrometer Tracing** - Spring Boot's observability abstraction
- **Grafana Tempo** - Trace storage and visualization backend
- **Correlation IDs** - Links traces with outbox events and Kafka messages

## Architecture

### Components

```
┌──────────────┐     HTTP      ┌──────────────┐     Kafka     ┌────────────┐
│ Order Service│───────────────▶│RouteBox Server │──────────────▶│  Consumers │
│  (Port 8080) │  Trace Context │ (Port 8081)  │  correlationId│            │
└──────────────┘                └──────────────┘               └────────────┘
       │                               │                             │
       │ OTLP/HTTP                    │ OTLP/HTTP                   │
       ▼                               ▼                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          Grafana Tempo                              │
│                       (Port 3200, 4317, 4318)                       │
└─────────────────────────────────────────────────────────────────────┘
       │                                │
       │ Trace Query                    │ Metrics/Logs Correlation
       ▼                                ▼
┌──────────────┐                 ┌──────────────┐
│   Grafana    │                 │ Prometheus   │
│  (Port 3000) │◀────────────────│   Loki       │
└──────────────┘                 └──────────────┘
```

### Trace Flow

1. **HTTP Request** → Order Service receives a request with optional trace context
2. **Span Creation** → `@Observed` methods automatically create spans
3. **Correlation ID** → Trace ID is used as the correlation ID for outbox events
4. **Event Storage** → OutboxEvent is saved with correlationId in database
5. **Event Publishing** → RouteBox Server claims event and creates new span
6. **Kafka Headers** → correlationId is added to Kafka message headers
7. **Trace Export** → All spans are sent to Tempo via OTLP

## Configuration

### Sampling Rate

To balance observability with performance, tracing uses a sampling strategy:

```yaml
# Default: Sample 10% of traces
management:
  tracing:
    sampling:
      probability: 0.1
```

**Recommendations:**
- **Development**: 1.0 (100%) - Full tracing for debugging
- **Staging**: 0.5 (50%) - Good coverage for testing
- **Production**: 0.1 (10%) - Minimal performance impact

### OTLP Endpoint

Both services send traces to Tempo via HTTP:

```yaml
management:
  otlp:
    tracing:
      endpoint: http://localhost:4318/v1/traces
```

**Note**: When running in Docker, use the service name: `http://tempo:4318/v1/traces`

### Log Correlation

Logs include trace and span IDs for correlation:

```yaml
logging:
  pattern:
    level: '%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]'
```

Example log output:
```
INFO  [order-service,64f84f7a9c1b2e3f4a5d6c7e8f9a0b1c,9c1b2e3f4a5d6c7e] Created order...
```

## Instrumentation

### Automatic Instrumentation

Spring Boot automatically instruments:
- **HTTP Endpoints** - All REST controllers
- **Database Queries** - JPA and JDBC operations
- **Kafka** - Producer operations (future enhancement)

### Custom Spans

Use `@Observed` annotation for custom spans:

```java
@Observed(
    name = "order.create",
    contextualName = "create-order"
)
@Transactional
public Order createOrder(CreateOrderRequest request) {
    // ... business logic
}
```

### Adding Tags

Use `Tracer` to add custom tags to spans:

```java
@Autowired
private Tracer tracer;

public void processEvent(OutboxEvent event) {
    if (tracer.currentSpan() != null) {
        tracer.currentSpan().tag("correlation.id", event.getCorrelationId());
        tracer.currentSpan().tag("event.type", event.getEventType());
    }
}
```

## Using Grafana for Tracing

### Accessing Tempo

1. Open Grafana: http://localhost:3000 (admin/admin)
2. Navigate to **Explore**
3. Select **Tempo** data source

### Finding Traces

#### By Trace ID
```
Query: <trace-id>
```

#### By Service
```
Service Name: order-service
OR
Service Name: routebox
```

#### By Correlation ID
Use Loki to find trace IDs by correlation ID:
```
{container_name="routebox-order-service"} |~ "correlation.id.*abc123"
```

### Trace-to-Logs Correlation

1. Click on any span in a trace
2. Click **Logs for this span** button
3. View related logs in Loki

### Trace-to-Metrics Correlation

1. Click on any span in a trace
2. View **Span Metrics** panel
3. See RED metrics (Rate, Errors, Duration) for the service

## Correlation ID Strategy

### Generation

- **With Trace Context**: Uses the trace ID from the current span
- **Without Trace Context**: Generates a new UUID

```java
private String getOrCreateCorrelationId() {
    if (tracer.currentSpan() != null) {
        return tracer.currentSpan().context().traceId();
    }
    return UUID.randomUUID().toString();
}
```

### Propagation

1. **HTTP Headers**: Automatic via Spring Boot
2. **Kafka Headers**: Manual via `correlationId` header
3. **Database**: Stored in `OutboxEvent.correlationId` column
4. **Logs**: Available as `%X{traceId}` in MDC

## Example Trace Scenarios

### Scenario 1: Order Creation Flow

1. User calls `POST /api/orders`
2. Trace starts with span: `POST /api/orders`
3. Child span: `order.create` (OrderService.createOrder)
4. Child span: `JPA INSERT` (save order)
5. Child span: `JPA INSERT` (save outbox event)
6. Trace ends, correlation ID stored in outbox

### Scenario 2: Event Publishing Flow

1. Poller claims events
2. New trace starts with span: `outbox.event.claim`
3. Virtual thread creates span: `outbox.event.publish`
4. Child span: `outbox.kafka.publish`
5. Child span: `Kafka SEND`
6. Trace ends, spans linked by correlation ID

### Scenario 3: End-to-End Trace

To trace a request from creation to publishing:
1. Find the trace ID from the order creation response headers
2. Search for the correlation ID in Tempo
3. View all related spans across both services

## Performance Considerations

### Sampling Impact

| Sampling Rate | Performance Impact | Trace Coverage |
|---------------|-------------------|----------------|
| 1.0 (100%)    | ~5-10% overhead   | All requests   |
| 0.5 (50%)     | ~2-5% overhead    | Every 2nd req  |
| 0.1 (10%)     | <1% overhead      | 1 in 10 reqs   |

### Resource Usage

- **Memory**: ~50-100 MB per service for trace buffers
- **Network**: ~1-5 KB per trace sent to Tempo
- **Storage**: Tempo configured for 48-hour retention

### Best Practices

1. **Use appropriate sampling** - Don't trace 100% in production
2. **Limit tag cardinality** - Avoid high-cardinality tags (e.g., user IDs)
3. **Keep spans focused** - Don't create too many child spans
4. **Use async export** - Let Spring Boot handle OTLP export asynchronously

## Troubleshooting

### No Traces Appearing

1. **Check sampling**: Increase probability to 1.0 temporarily
2. **Verify Tempo**: `curl http://localhost:3200/ready`
3. **Check endpoint**: Ensure services can reach Tempo
4. **Review logs**: Look for OTLP export errors

### Missing Correlation

1. **Verify trace propagation**: Check HTTP headers include traceparent
2. **Check Kafka headers**: Use kafka-ui to view message headers
3. **Review database**: Query `outbox_events.correlationId`

### High Latency

1. **Reduce sampling rate**: Lower the probability
2. **Batch spans**: Let OTLP exporter batch before sending
3. **Use async export**: Already default in Spring Boot

## Advanced Topics

### Custom Trace Propagation

To propagate trace context in custom scenarios:

```java
@Autowired
private Tracer tracer;

@Autowired
private ObservationRegistry observationRegistry;

public void processInVirtualThread(OutboxEvent event) {
    Observation observation = Observation.start(
        "event.process",
        observationRegistry
    );
    
    observation.lowCardinalityKeyValue("event.type", event.getEventType());
    
    try (Observation.Scope scope = observation.openScope()) {
        // Work happens here with trace context
        doWork(event);
    } finally {
        observation.stop();
    }
}
```

### Querying Tempo API

Direct API queries:

```bash
# Get trace by ID
curl "http://localhost:3200/api/traces/<trace-id>"

# Search traces
curl "http://localhost:3200/api/search?tags=service.name=order-service"
```

### Integration with APM Tools

Tempo can export to other tools:
- **Jaeger**: Use Jaeger query frontend
- **Zipkin**: Use Zipkin-compatible API
- **Datadog/New Relic**: Export via OTLP

## References

- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [Micrometer Tracing](https://micrometer.io/docs/tracing)
- [Grafana Tempo](https://grafana.com/docs/tempo/latest/)
- [Spring Boot Observability](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.observability)
