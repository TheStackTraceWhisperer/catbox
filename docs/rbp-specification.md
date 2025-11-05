# Really Big Payloads (RBP) Specification

## 1. Executive Summary

This specification defines a strategy for handling large payloads in RouteBox's transactional outbox pattern implementation. The goal is to avoid overwhelming Kafka and the database with very large messages while maintaining the reliability guarantees of the outbox pattern.

**Key Objectives:**
- Prevent large payloads from burdening Kafka IO and processing
- Maintain transactional consistency guarantees
- Provide simple, intuitive client API for payload retrieval
- Support multiple storage backend options
- Ensure secure access control
- Maintain system observability and monitoring

## 2. Problem Statement

### Current Architecture Constraints

The current RouteBox implementation stores the entire event payload in the `outbox_events` table as a `TEXT` column. This payload is then published directly to Kafka. This approach works well for typical event payloads (a few KB), but becomes problematic for large payloads:

**Database Issues:**
- Large TEXT columns impact database performance (indexing, query planning, buffer pool)
- Increased storage costs
- Slower backup and restore operations
- Memory pressure on application servers loading large rows

**Kafka Issues:**
- Kafka is optimized for high-throughput, small messages (recommended max: 1MB, default max: 1MB)
- Large messages reduce throughput and increase latency
- Broker memory and network bandwidth constraints
- Consumer lag issues when processing large messages
- Increased serialization/deserialization overhead

**Application Issues:**
- Increased heap memory usage when processing large events
- Potential OutOfMemoryError conditions
- Slower event processing throughput
- Network bandwidth consumption between services

### Use Cases

**Primary Use Case:**
A business service needs to publish an event with a large data payload (e.g., 10MB+ document, image, or data export) while maintaining transactional consistency with database operations.

**Example Scenarios:**
- Document management system publishing file uploads
- Image processing service with binary image data
- Data export operations with large CSV/JSON files
- Analytics events with large result sets
- Batch processing results

## 3. Solution Overview

The Really Big Payload (RBP) feature introduces an external storage mechanism for large payloads, while keeping a reference in the outbox event. The system automatically:

1. Detects payloads exceeding a configurable threshold
2. Stores the large payload in external storage (cloud or disk)
3. Stores only a reference URI in the outbox event
4. Provides client API to retrieve the payload when needed
5. Manages lifecycle and cleanup of stored payloads

## 4. Architecture Design

### 4.1 Storage Backend Options

#### Option A: Cloud Storage (Recommended for Production)

**Pros:**
- **Scalability**: Virtually unlimited storage capacity
- **Durability**: Built-in redundancy (e.g., S3 11 nines of durability)
- **High Availability**: Multi-region replication options
- **Cost-Effective**: Pay-per-use pricing for large storage volumes
- **Separation of Concerns**: Storage decoupled from application servers
- **Performance**: High-throughput parallel reads/writes
- **Security**: Built-in encryption at rest and in transit
- **Lifecycle Management**: Automatic expiration policies

**Cons:**
- External dependency (network latency, availability)
- Additional cloud service costs
- More complex configuration and IAM setup

**Recommended Services:**
- **AWS S3**: Industry-standard object storage
- **Azure Blob Storage**: Enterprise-grade storage
- **Google Cloud Storage**: Flexible storage classes
- **MinIO**: S3-compatible on-premises option

**Implementation Approach:**
```java
public interface RbpStorageService {
    String store(String correlationId, byte[] payload);
    byte[] retrieve(String uri);
    void delete(String uri);
}

@Service
public class S3RbpStorageService implements RbpStorageService {
    private final S3Client s3Client;
    private final String bucketName;
    // Implementation using AWS SDK v2
}
```

#### Option B: OutboxServer Local Disk

**Pros:**
- **Simplicity**: No external dependencies
- **Low Latency**: Direct filesystem access
- **Cost**: No additional service costs
- **Control**: Full control over storage location
- **Offline Operation**: Works without internet connectivity

**Cons:**
- **Limited Scalability**: Constrained by disk capacity
- **Single Point of Failure**: Data lost if server fails
- **No Redundancy**: Manual backup required
- **Operational Burden**: Disk monitoring and cleanup required
- **Multi-Node Complexity**: Requires shared filesystem (NFS, EFS) for horizontal scaling
- **Security**: Manual encryption and access control

**Implementation Approach:**
```java
@Service
public class DiskRbpStorageService implements RbpStorageService {
    private final Path storageRoot;
    // Implementation using java.nio.file APIs
}
```

#### Recommendation

**For Production**: Use **Cloud Storage (Option A)** for reliability, scalability, and operational simplicity.

**For Development/Testing**: Either option works; local disk may be simpler for quick setup.

**Hybrid Approach**: Support both through strategy pattern, allowing configuration-based selection.

### 4.2 Data Model Changes

#### OutboxEvent Entity Enhancement

```java
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    // ... existing fields ...
    
    @Column
    private Boolean isRbp = false; // Flag indicating payload is externally stored
    
    @Column
    private String rbpUri; // URI/path to external payload (e.g., "s3://bucket/key")
    
    @Column
    private Long rbpSizeBytes; // Original payload size in bytes
    
    @Column
    private LocalDateTime rbpStoredAt; // When payload was stored externally
    
    // When isRbp=true, the `payload` column contains metadata/reference only
}
```

**Migration Strategy:**
- Add new columns with default values (backwards compatible)
- Existing events continue to work (isRbp=false, rbpUri=null)
- No data migration required

#### Database Schema

```sql
ALTER TABLE outbox_events 
ADD COLUMN is_rbp BOOLEAN DEFAULT FALSE,
ADD COLUMN rbp_uri VARCHAR(2048),
ADD COLUMN rbp_size_bytes BIGINT,
ADD COLUMN rbp_stored_at TIMESTAMP;

-- Optional index for RBP cleanup queries
CREATE INDEX idx_outbox_events_rbp_cleanup 
ON outbox_events(rbp_stored_at, is_rbp) 
WHERE is_rbp = TRUE AND sent_at IS NOT NULL;
```

### 4.3 Client API Design

#### Writing Large Payloads

```java
public interface OutboxClient {
    // Existing methods remain unchanged
    void write(String aggregateType, String aggregateId, String eventType, Object payload);
    
    // New method for explicit RBP handling
    void writeRbp(String aggregateType, String aggregateId, String eventType, 
                  Object payload);
    
    void writeRbp(String aggregateType, String aggregateId, String eventType,
                  String correlationId, Object payload);
}
```

**Automatic Detection Variant:**
```java
@Service
public class DefaultOutboxClient implements OutboxClient {
    private final RbpStorageService rbpStorage;
    private final int rbpThresholdBytes; // Configurable, e.g., 100KB
    
    @Override
    public void write(String aggregateType, String aggregateId, 
                     String eventType, Object payload) {
        String jsonPayload = objectMapper.writeValueAsString(payload);
        
        if (jsonPayload.length() > rbpThresholdBytes) {
            // Automatically handle as RBP
            writeAsRbp(aggregateType, aggregateId, eventType, null, jsonPayload);
        } else {
            // Normal path
            writeAsNormal(aggregateType, aggregateId, eventType, null, jsonPayload);
        }
    }
    
    private void writeAsRbp(String aggregateType, String aggregateId,
                           String eventType, String correlationId, 
                           String jsonPayload) {
        byte[] payloadBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
        
        // Generate correlation ID if not provided
        String effectiveCorrelationId = correlationId != null 
            ? correlationId 
            : UUID.randomUUID().toString();
        
        // Store payload externally
        String rbpUri = rbpStorage.store(effectiveCorrelationId, payloadBytes);
        
        // Create lightweight reference event
        OutboxEvent event = new OutboxEvent(
            aggregateType, aggregateId, eventType, effectiveCorrelationId,
            createRbpReferencePayload(effectiveCorrelationId)
        );
        event.setIsRbp(true);
        event.setRbpUri(rbpUri);
        event.setRbpSizeBytes((long) payloadBytes.length);
        event.setRbpStoredAt(LocalDateTime.now());
        
        outboxEventRepository.save(event);
    }
    
    private String createRbpReferencePayload(String correlationId) {
        // Small JSON with reference information
        return String.format("{\"rbpRef\":\"%s\",\"message\":\"Retrieve via RbpClient.get()\"}", 
                           correlationId);
    }
}
```

#### Retrieving Large Payloads

```java
public interface RbpClient {
    /**
     * Retrieve Really Big Payload by correlation ID.
     * 
     * @param correlationId The correlation ID from the event
     * @param targetClass The class to deserialize into
     * @return Deserialized payload
     */
    <T> T get(String correlationId, Class<T> targetClass);
    
    /**
     * Retrieve raw payload bytes.
     * 
     * @param correlationId The correlation ID from the event
     * @return Raw payload bytes
     */
    byte[] getRaw(String correlationId);
    
    /**
     * Check if payload is available.
     */
    boolean exists(String correlationId);
}
```

**Implementation:**
```java
@Service
public class DefaultRbpClient implements RbpClient {
    private final OutboxEventRepository repository;
    private final RbpStorageService storageService;
    private final ObjectMapper objectMapper;
    
    @Override
    public <T> T get(String correlationId, Class<T> targetClass) {
        byte[] payloadBytes = getRaw(correlationId);
        return objectMapper.readValue(payloadBytes, targetClass);
    }
    
    @Override
    public byte[] getRaw(String correlationId) {
        OutboxEvent event = repository.findByCorrelationId(correlationId)
            .orElseThrow(() -> new RbpNotFoundException(correlationId));
            
        if (!Boolean.TRUE.equals(event.getIsRbp())) {
            throw new IllegalStateException("Event is not an RBP: " + correlationId);
        }
        
        return storageService.retrieve(event.getRbpUri());
    }
    
    @Override
    public boolean exists(String correlationId) {
        return repository.findByCorrelationId(correlationId)
            .map(e -> Boolean.TRUE.equals(e.getIsRbp()))
            .orElse(false);
    }
}
```

**Usage Example:**
```java
@Service
public class OrderEventConsumer {
    private final RbpClient rbpClient;
    
    @KafkaListener(topics = "OrderCreated")
    public void handleOrder(String message) {
        // Parse the lightweight reference message
        JsonNode node = objectMapper.readTree(message);
        String correlationId = node.get("rbpRef").asText();
        
        // Retrieve the actual large payload
        OrderData orderData = rbpClient.get(correlationId, OrderData.class);
        
        // Process the order...
    }
}
```

### 4.4 Publishing Behavior

The OutboxEventPublisher continues to work without modification:
- For normal events, it publishes the payload as usual
- For RBP events (isRbp=true), it publishes only the small reference payload
- Consumers detect the RBP reference and use RbpClient to fetch the actual data

**No changes required to OutboxEventPublisher** - it remains payload-agnostic.

### 4.5 Lifecycle Management

#### Cleanup Strategy

**Option 1: Time-Based Retention (Recommended)**
```java
@Scheduled(cron = "${rbp.cleanup.cron:0 0 2 * * *}") // Daily at 2 AM
public void cleanupExpiredRbps() {
    LocalDateTime cutoff = LocalDateTime.now()
        .minus(rbpRetentionDays, ChronoUnit.DAYS);
    
    List<OutboxEvent> expiredRbps = repository.findExpiredRbps(cutoff);
    
    for (OutboxEvent event : expiredRbps) {
        try {
            storageService.delete(event.getRbpUri());
            // Option: delete entire event or just clear RBP fields
            event.setRbpUri(null);
            event.setIsRbp(false);
            repository.save(event);
        } catch (Exception e) {
            log.error("Failed to cleanup RBP: {}", event.getRbpUri(), e);
        }
    }
}
```

**Option 2: Event-Based Cleanup**
- Delete RBP after event is successfully sent (sentAt != null)
- Configurable grace period (e.g., keep for 24 hours after send)
- Ensures consumers have time to retrieve

**Option 3: Manual/On-Demand**
- Expose admin API endpoint
- Allow manual cleanup via Admin UI

#### Configuration

```yaml
routebox:
  rbp:
    enabled: true
    threshold-bytes: 102400  # 100KB - payloads larger than this use RBP
    storage:
      type: s3  # or 'disk'
      # S3 configuration
      s3:
        bucket-name: routebox-rbp-prod
        region: us-east-1
        prefix: rbp/  # Optional path prefix
      # Disk configuration
      disk:
        base-path: /var/routebox/rbp
    retention:
      days: 7  # Keep RBPs for 7 days after event sent
      cleanup-cron: "0 0 2 * * *"  # Daily cleanup at 2 AM
```

## 5. Security Considerations

### 5.1 Access Control

**Cloud Storage:**
- Use IAM roles/service accounts (no hardcoded credentials)
- Least-privilege access (RouteBox servers only)
- Bucket policies restricting access
- VPC endpoints for private network access

**Local Disk:**
- File system permissions (e.g., 700 on storage directory)
- Run RouteBox server as dedicated user
- Consider encryption at rest (LUKS, dm-crypt)

### 5.2 Data Encryption

**In Transit:**
- HTTPS/TLS for cloud storage API calls
- Kafka SSL/TLS (already supported)

**At Rest:**
- Cloud storage: Enable server-side encryption (SSE-S3, SSE-KMS)
- Local disk: Consider filesystem-level encryption

### 5.3 Correlation ID as Security Key

The correlation ID serves as the retrieval key. Considerations:
- Use UUIDs for unpredictability
- Consider adding HMAC signature for additional security
- Implement rate limiting on RbpClient.get() to prevent abuse

## 6. Performance and Scalability

### 6.1 Performance Impact

**Write Path:**
- Additional latency for external storage write (10-100ms for S3)
- Still within same database transaction (ACID guarantees maintained)
- Configurable: only large payloads incur this cost

**Read Path:**
- Consumers retrieve payload on-demand (not in critical path)
- Parallel retrieval possible for batch consumers
- Caching layer can be added if needed

### 6.2 Scalability

**Cloud Storage:**
- S3: 3,500 PUT/s and 5,500 GET/s per prefix
- Virtually unlimited total throughput with partitioning
- Auto-scales with demand

**Local Disk:**
- Limited by disk IOPS and throughput
- Requires NFS/EFS for multi-node deployments
- May need separate storage tier for high volume

### 6.3 Monitoring Metrics

**New Metrics:**
```java
@Component
public class RbpMetricsService {
    private final MeterRegistry registry;
    
    // Counters
    Counter rbpStorageSuccess;
    Counter rbpStorageFailure;
    Counter rbpRetrievalSuccess;
    Counter rbpRetrievalFailure;
    Counter rbpCleanupSuccess;
    Counter rbpCleanupFailure;
    
    // Gauges
    Gauge rbpTotalCount;
    Gauge rbpTotalSizeBytes;
    
    // Timers
    Timer rbpStorageDuration;
    Timer rbpRetrievalDuration;
}
```

**Alerting:**
- RBP storage failure rate > 1%
- RBP retrieval latency > 1s (p95)
- RBP storage size exceeds threshold
- Cleanup job failures

## 7. Implementation Roadmap

### Phase 1: Foundation (Sprint 1-2)
- [ ] Add database schema changes (migration scripts)
- [ ] Implement RbpStorageService interface and S3 implementation
- [ ] Update OutboxEvent entity with RBP fields
- [ ] Add configuration support for RBP settings
- [ ] Unit tests for storage service

### Phase 2: Client Integration (Sprint 3-4)
- [ ] Enhance DefaultOutboxClient with RBP detection and handling
- [ ] Implement RbpClient for payload retrieval
- [ ] Add RbpNotFoundException and error handling
- [ ] Integration tests for write and read paths
- [ ] Add metrics and monitoring

### Phase 3: Lifecycle Management (Sprint 5)
- [ ] Implement scheduled cleanup job
- [ ] Add cleanup configuration options
- [ ] Admin UI integration for RBP status
- [ ] Cleanup monitoring and alerting

### Phase 4: Alternative Storage (Sprint 6 - Optional)
- [ ] Implement DiskRbpStorageService
- [ ] Add storage strategy selection
- [ ] Performance comparison testing
- [ ] Documentation updates

### Phase 5: Production Hardening (Sprint 7-8)
- [ ] Load testing with large payloads
- [ ] Security audit and penetration testing
- [ ] Failover and disaster recovery testing
- [ ] Production deployment guide
- [ ] Runbook for operations

## 8. Alternatives Considered

### Alternative 1: Kafka Large Message Support
**Approach:** Increase Kafka max.message.bytes and related configs.
**Pros:** No code changes required.
**Cons:** Degrades Kafka performance, affects all topics, not recommended by Kafka best practices.
**Decision:** Rejected - violates Kafka design principles.

### Alternative 2: Message Splitting/Chunking
**Approach:** Split large payloads into multiple smaller messages.
**Pros:** Works within Kafka limits.
**Cons:** Complex reassembly logic, ordering challenges, partial delivery issues.
**Decision:** Rejected - adds significant complexity.

### Alternative 3: Inline Compression
**Approach:** Compress payload before storing in database/Kafka.
**Pros:** Simple, reduces size.
**Cons:** Still limited by Kafka/database limits, CPU overhead, not effective for already-compressed data.
**Decision:** Rejected - doesn't solve the fundamental size problem.

## 9. Testing Strategy

### Unit Tests
- RbpStorageService implementations (mock S3, filesystem)
- OutboxClient RBP detection logic
- RbpClient retrieval and deserialization
- Cleanup job logic

### Integration Tests
- End-to-end write and retrieve flow
- Transaction rollback scenarios
- Multi-node concurrent access
- Cleanup with sent/unsent events

### Performance Tests
- Large payload write latency
- Concurrent retrieval throughput
- Storage backend comparison (S3 vs disk)
- Impact on normal (non-RBP) event processing

### Security Tests
- Unauthorized access attempts
- Correlation ID enumeration resistance
- Encryption verification (in-transit and at-rest)

## 10. Documentation Requirements

- [ ] Update API Reference with RbpClient methods
- [ ] Add RBP configuration guide
- [ ] Update Quick Start with RBP example
- [ ] Add troubleshooting section for RBP issues
- [ ] Create RBP best practices guide
- [ ] Add migration guide for existing deployments

## 11. Open Questions and Future Enhancements

### Open Questions
1. Should RBP threshold be per-event-type or global?
2. Should cleanup delete the outbox event or just the external payload?
3. Do we need RBP versioning support (same correlation ID, multiple versions)?
4. Should we support streaming API for very large payloads (>100MB)?

### Future Enhancements
- Support for streaming large payloads (avoid loading entire payload in memory)
- RBP payload compression option
- Multi-region replication for cloud storage
- RBP analytics and usage reporting
- Support for additional storage backends (Azure Blob, GCS)
- Async retrieval API with callbacks
- RBP payload transformation/filtering APIs

## 12. References

- [AWS S3 Best Practices](https://docs.aws.amazon.com/AmazonS3/latest/userguide/best-practices.html)
- [Kafka Message Size Best Practices](https://kafka.apache.org/documentation/#design_messages)
- [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)
- [Spring Cloud AWS S3 Integration](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html#s3-integration)

## 13. Appendix: Example Code

### Complete Usage Example

```java
// ========================================
// Producer Service (Writing Large Payload)
// ========================================
@Service
public class DocumentService {
    private final OutboxClient outboxClient;
    
    @Transactional
    public void uploadDocument(Document document) {
        // 1. Save document metadata to database
        documentRepository.save(document);
        
        // 2. Write event with large payload (automatically handled as RBP)
        LargeDocumentPayload payload = new LargeDocumentPayload(
            document.getId(),
            document.getName(),
            document.getContent() // Could be 10MB+ of data
        );
        
        outboxClient.write(
            "Document",
            document.getId().toString(),
            "DocumentUploaded",
            payload
        );
        
        // Both database writes are in the same transaction
        // If payload > threshold, it's automatically stored externally
    }
}

// ========================================
// Consumer Service (Reading Large Payload)
// ========================================
@Service
public class DocumentProcessorService {
    private final RbpClient rbpClient;
    
    @KafkaListener(topics = "DocumentUploaded")
    public void processDocument(String message) {
        // 1. Parse the lightweight message
        JsonNode node = objectMapper.readTree(message);
        
        // 2. Check if it's an RBP reference
        if (node.has("rbpRef")) {
            String correlationId = node.get("rbpRef").asText();
            
            // 3. Retrieve the actual large payload
            LargeDocumentPayload payload = rbpClient.get(
                correlationId, 
                LargeDocumentPayload.class
            );
            
            // 4. Process the large document
            processLargeDocument(payload);
        } else {
            // Normal small payload
            LargeDocumentPayload payload = objectMapper.readValue(
                message, 
                LargeDocumentPayload.class
            );
            processLargeDocument(payload);
        }
    }
}
```

### S3 Storage Configuration Example

```yaml
# application.yml
routebox:
  rbp:
    enabled: true
    threshold-bytes: 102400  # 100KB
    storage:
      type: s3
      s3:
        bucket-name: ${RBP_S3_BUCKET:routebox-rbp}
        region: ${AWS_REGION:us-east-1}
        prefix: ${RBP_S3_PREFIX:payloads/}
    retention:
      days: 7
      cleanup-cron: "0 0 2 * * *"

# AWS credentials should be provided via:
# - IAM instance role (recommended for EC2/ECS)
# - Environment variables (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
# - AWS credentials file (~/.aws/credentials)
```

### Maven Dependencies

```xml
<!-- For S3 storage -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.0</version>
</dependency>

<!-- For Spring Cloud AWS (optional, provides auto-configuration) -->
<dependency>
    <groupId>io.awspring.cloud</groupId>
    <artifactId>spring-cloud-aws-starter-s3</artifactId>
    <version>3.0.0</version>
</dependency>
```

---

**Specification Version:** 1.0  
**Last Updated:** 2025-11-05  
**Status:** Draft  
**Owner:** RouteBox Team
