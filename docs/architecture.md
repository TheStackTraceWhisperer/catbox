# Architecture

This document describes the architecture and design of the Catbox transactional outbox pattern implementation.

## Overview

This project demonstrates a transactional outbox pattern using a decoupled, multi-module architecture. The system is composed of two primary Spring Boot applications:

* **`order-service` (Port 8080):** A business-facing service responsible for creating and updating orders. When it writes to its `orders` table, it also writes an `OutboxEvent` to a shared table in the *same transaction*, ensuring data consistency.
* **`catbox-server` (Port 8081):** A standalone processor that polls the `outbox_events` table. It uses a `SELECT FOR UPDATE SKIP LOCKED` query (via `OutboxEventClaimer`) to safely claim events and publish them to Kafka using a dynamic, multi-cluster routing factory.

This separation ensures that the business service (`order-service`) is lightweight and not burdened with event publishing logic, while the `catbox-server` can be scaled independently to handle event throughput.

## Phase 1: Order Submission (Atomic Transaction)

When an order is created or updated:
1. Both the **Order** and **OutboxEvent** are saved in the **same database transaction** (REQUIRES_NEW)
2. This ensures atomicity - either both are saved or neither is saved
3. Prevents dual-write inconsistencies

## Phase 2: Event Publishing (Concurrent Multi-Node Processing)

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

## Project Structure

```
catbox-parent
├── catbox-common     # Shared code: OutboxEvent entity and repository
├── catbox-client     # Simple client for creating events (used by order-service)
├── catbox-server     # Standalone poller/publisher application (runs on 8081)
├── order-service     # Business service application (runs on 8080)
├── coverage-report   # Aggregated test coverage reports
├── jmeter-tests      # JMeter load and stress test suites
├── infrastructure    # Docker Compose and infrastructure configurations
│   ├── compose.yaml     # Docker Compose for infrastructure services
│   ├── monitoring       # Prometheus, Grafana, and Loki configurations
│   ├── kafka-security   # Kafka SSL/TLS and SASL configurations
│   └── keycloak         # Keycloak realm configuration
└── pom.xml           # Parent POM
```
