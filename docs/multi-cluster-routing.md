# Multi-Cluster Kafka Routing

This document describes the advanced multi-cluster routing capabilities in the Catbox outbox pattern implementation.

## Overview

The outbox publisher supports advanced routing rules to publish a single event to multiple Kafka clusters with different success strategies. This is configured under the `outbox.routing.rules` key in `application.yml`.

## Basic Single-Cluster Routing

The simplest configuration routes events to a single Kafka cluster. For simple routing, you can provide a string value representing the target cluster key. This is the default and backward-compatible format.

```yaml
outbox:
  routing:
    rules:
      OrderCreated: cluster-a
      OrderStatusChanged: cluster-a
```

This is backward compatible with simple outbox implementations.

## Advanced Multi-Cluster Publishing Strategies

For more complex scenarios, you can define a rule object with `clusters`, `strategy`, and `optional` keys.

### Strategy 1: ALL_MUST_SUCCEED (Default)

The event is only marked as "sent" if publishing succeeds to **all** clusters listed in the `clusters` list. This ensures complete consistency across all clusters.

**Configuration:**
```yaml
outbox:
  routing:
    rules:
      # This event must be published to both cluster-a and cluster-b
      InventoryEvent:
        clusters: [cluster-a, cluster-b]
        strategy: all-must-succeed 
```

**Use Cases:**
- Critical events that must reach all systems
- Ensuring data consistency across multiple regions
- Synchronous replication requirements

**Behavior:**
- Event publishes to all clusters in parallel
- If any cluster fails, the entire operation fails
- Event remains in outbox for retry
- All clusters will receive the event again on retry

### Strategy 2: AT_LEAST_ONE

The event is marked as "sent" if publishing succeeds to **at least one** of the clusters in the `clusters` list. This is useful for high-availability geographic replication. This prioritizes availability over consistency.

**Configuration:**
```yaml
outbox:
  routing:
    rules:
      # This event will be sent to both, but is considered "successful"
      # if it reaches either the east or west region.
      PaymentEvent:
        clusters: [cluster-east, cluster-west]
        strategy: at-least-one
```

**Use Cases:**
- High availability scenarios
- Geographic redundancy
- Systems where partial delivery is acceptable
- Failover configurations

**Behavior:**
- Event publishes to all clusters in parallel
- Success if at least one cluster accepts the event
- Failed clusters won't receive the event (no retry for that event)
- Consider implementing consumer-side deduplication

### Strategy 3: optional Clusters

You can specify `optional` clusters. Publishing failures to these clusters are logged but **ignored** and will not prevent the event from being marked as "sent". This is only valid when the `strategy` is `ALL_MUST_SUCCEED`. Some clusters are **required** while others are **optional**. Optional cluster failures don't affect overall success.

**Configuration:**
```yaml
outbox:
  routing:
    rules:
      # This event MUST go to the primary-cluster.
      # It will also *try* to go to analytics and audit-log,
      # but failures on those are ignored.
      NotificationEvent:
        clusters: [primary-cluster]        # Required
        optional: [analytics, audit-log]   # Optional - failures ignored
        strategy: all-must-succeed         # Applies only to required clusters
```

**Use Cases:**
- Primary + secondary systems (e.g., transactional + analytics)
- Required + best-effort delivery
- Critical path + audit logging
- Core system + experimental features

**Behavior:**
- Required clusters must succeed (based on strategy)
- Optional clusters are attempted but failures are ignored
- Event is marked as sent if required clusters succeed
- Useful for non-critical downstream systems

### Benefits of Multi-Cluster Routing

This flexibility enables:

- **Geographic replication**: Publish to multiple regional clusters.
- **High availability**: Mark as "sent" if any cluster succeeds.
- **Best-effort delivery**: Use `optional` for non-critical systems like analytics or logging.
- **Zero-downtime migrations**: Route to both old and new clusters during a migration.

## Cluster Configuration

Define Kafka clusters in your application configuration:

```yaml
kafka:
  clusters:
    cluster-a:
      bootstrap-servers: localhost:9092
      properties:
        # Optional: cluster-specific properties
        
    cluster-b:
      bootstrap-servers: localhost:9093
      ssl:
        bundle: kafka-client
      properties:
        security.protocol: SASL_SSL
        sasl.mechanism: SCRAM-SHA-512
        sasl.jaas.config: org.apache.kafka.common.security.scram.ScramLoginModule required username="producer" password="producer-secret";
        
    cluster-c:
      bootstrap-servers: remote-kafka:9092
      properties:
        # Remote cluster configuration
```

## Routing Rules

### Event Type to Cluster Mapping

Map specific event types to clusters or cluster groups:

```yaml
outbox:
  routing:
    rules:
      # Simple single cluster
      OrderCreated: cluster-a
      
      # Multi-cluster with all-must-succeed
      PaymentProcessed:
        clusters: [cluster-a, cluster-b]
        strategy: all-must-succeed
      
      # Multi-cluster with at-least-one
      NotificationSent:
        clusters: [email-cluster, sms-cluster, push-cluster]
        strategy: at-least-one
      
      # Required + optional clusters
      UserRegistered:
        clusters: [primary]
        optional: [analytics, crm]
        strategy: all-must-succeed
```

## Use Case Examples

### Geographic Replication

Publish events to multiple regional Kafka clusters:

```yaml
outbox:
  routing:
    rules:
      OrderEvent:
        clusters: [us-east, us-west, eu-central, asia-pacific]
        strategy: all-must-succeed
```

**Benefits:**
- Data locality for regional consumers
- Reduced latency for regional systems
- Compliance with data residency requirements

### High Availability Setup

Ensure events reach at least one cluster even during outages:

```yaml
outbox:
  routing:
    rules:
      CriticalEvent:
        clusters: [primary-dc, secondary-dc, tertiary-dc]
        strategy: at-least-one
```

**Benefits:**
- System continues functioning during cluster outages
- No events lost if at least one cluster is available
- Graceful degradation

### Zero-Downtime Migration

Route to both old and new clusters during migration:

```yaml
outbox:
  routing:
    rules:
      OrderEvent:
        clusters: [legacy-cluster, new-cluster]
        strategy: all-must-succeed
```

**Migration Process:**
1. Start dual-publishing to both clusters
2. Migrate consumers from legacy to new cluster
3. Verify all consumers are using new cluster
4. Update routing to only use new cluster
5. Decommission legacy cluster

### Primary + Analytics Pattern

Critical events go to primary, with best-effort delivery to analytics:

```yaml
outbox:
  routing:
    rules:
      TransactionCompleted:
        clusters: [transaction-cluster]
        optional: [analytics-cluster, data-warehouse]
        strategy: all-must-succeed
```

**Benefits:**
- Primary business logic not affected by analytics failures
- Analytics receives events when available
- Simplified error handling for non-critical systems

## Error Handling

### Retry Behavior

When publishing fails:

1. **all-must-succeed:** Event remains in outbox if any required cluster fails
2. **at-least-one:** Event remains in outbox if all clusters fail
3. **Optional clusters:** Failures in optional clusters are logged but ignored

### Retry Timing

Failed events are automatically retried based on the `inProgressUntil` timeout:

- Default timeout: 5 minutes
- After timeout expires, event becomes eligible for retry
- Poller reclaims the event in next polling cycle
- Publishing is attempted again to all configured clusters

### Monitoring Failures

Use custom metrics to monitor multi-cluster publishing:

```bash
# View failure metrics
curl http://localhost:8081/actuator/prometheus | grep outbox_events_published_failure
```

Consider alerting on:
- High failure rates for specific clusters
- Repeated failures for the same events
- Cluster-specific availability issues

## Performance Considerations

### Parallel Publishing

Events publish to multiple clusters in parallel using virtual threads:

- Each cluster publication runs in its own virtual thread
- No blocking between cluster publications
- Thousands of concurrent publications possible with Java 21 virtual threads

### Throughput

Multi-cluster publishing affects throughput:

- **all-must-succeed:** Limited by slowest cluster
- **at-least-one:** Limited by fastest cluster
- **optional clusters:** Optional clusters don't affect throughput

### Network Latency

Consider network latency when configuring timeouts:

```yaml
spring:
  kafka:
    producer:
      properties:
        # Adjust based on cluster locations
        request.timeout.ms: 30000
        delivery.timeout.ms: 120000
```

## Testing Multi-Cluster Setup

The project includes two Kafka clusters for testing:

```bash
# Start both clusters
cd infrastructure && docker compose up -d kafka kafka-2

# Test multi-cluster routing
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName": "Test", "productName": "Widget", "amount": 99.99}'

# Verify events in both clusters using Kafka UI
# Navigate to http://localhost:8090
```

## Best Practices

1. **Choose the Right Strategy:**
   - Use `all-must-succeed` for critical consistency requirements
   - Use `at-least-one` for high availability needs
   - Use optional clusters for non-critical systems

2. **Monitor Cluster Health:**
   - Track per-cluster failure rates
   - Alert on cluster-specific issues
   - Use metrics to identify slow clusters

3. **Plan for Failures:**
   - Test failure scenarios (cluster down, network issues)
   - Verify retry behavior meets requirements
   - Document recovery procedures

4. **Consider Costs:**
   - Multi-cluster publishing increases infrastructure costs
   - Network bandwidth for cross-region replication
   - Storage costs for event duplication

5. **Implement Consumer Deduplication:**
   - Use `at-least-one` strategy with idempotent consumers
   - Implement consumer-side deduplication for critical events
   - Store processed event IDs to prevent duplicate processing
