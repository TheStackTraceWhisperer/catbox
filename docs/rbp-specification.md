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

### 4.1 Cloud Storage Backend

RouteBox RBP uses cloud object storage for reliable, scalable payload storage. The design supports multiple cloud providers through a common abstraction layer.

**Benefits:**
- **Scalability**: Virtually unlimited storage capacity
- **Durability**: Built-in redundancy (99.999999999% durability for most services)
- **High Availability**: Multi-region replication options
- **Cost-Effective**: Pay-per-use pricing for large storage volumes
- **Separation of Concerns**: Storage decoupled from application servers
- **Performance**: High-throughput parallel reads/writes
- **Security**: Built-in encryption at rest and in transit
- **Lifecycle Management**: Automatic expiration policies

**Considerations:**
- External dependency (network latency, availability)
- Additional cloud service costs
- IAM and access control configuration required

#### Supported Cloud Providers

**Azure Blob Storage** and **Google Cloud Storage** are the recommended cloud storage providers for RouteBox RBP.

**Storage Interface:**
```java
public interface RbpStorageService {
    /**
     * Store payload and return URI for retrieval.
     * @param correlationId Unique identifier for this payload
     * @param payload Payload bytes to store
     * @return Storage URI (e.g., "azure://container/correlation-id" or "gcs://bucket/correlation-id")
     */
    String store(String correlationId, byte[] payload);
    
    /**
     * Retrieve payload by URI.
     * @param uri Storage URI returned from store()
     * @return Payload bytes
     */
    byte[] retrieve(String uri);
    
    /**
     * Delete payload from storage.
     * @param uri Storage URI
     */
    void delete(String uri);
}
```

#### Azure Blob Storage Implementation

**Features:**
- Hot, Cool, and Archive storage tiers for cost optimization
- Geo-redundant storage (GRS) for cross-region redundancy
- Lifecycle management policies for automatic archival/deletion
- Azure AD integration for secure access control
- Server-side encryption with customer-managed keys

**Implementation Example:**
```java
@Service
public class AzureBlobRbpStorageService implements RbpStorageService {
    private final BlobServiceClient blobServiceClient;
    private final String containerName;
    
    @Override
    public String store(String correlationId, byte[] payload) {
        BlobClient blobClient = blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(correlationId);
        
        blobClient.upload(new ByteArrayInputStream(payload), payload.length, true);
        
        return String.format("azure://%s/%s", containerName, correlationId);
    }
    
    @Override
    public byte[] retrieve(String uri) {
        // Parse URI: azure://container/correlation-id
        String correlationId = extractCorrelationId(uri);
        
        BlobClient blobClient = blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(correlationId);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.downloadStream(outputStream);
        return outputStream.toByteArray();
    }
    
    @Override
    public void delete(String uri) {
        String correlationId = extractCorrelationId(uri);
        blobServiceClient
            .getBlobContainerClient(containerName)
            .getBlobClient(correlationId)
            .delete();
    }
}
```

**Configuration:**
- Use Managed Identity for authentication (recommended for Azure VMs/AKS)
- Connection string for development/testing
- Configure lifecycle policies for automatic cleanup

#### Google Cloud Storage Implementation

**Features:**
- Standard, Nearline, Coldline, and Archive storage classes
- Multi-regional and dual-regional replication
- Object lifecycle management with automatic deletion rules
- IAM and signed URLs for fine-grained access control
- Customer-managed encryption keys (CMEK)

**Implementation Example:**
```java
@Service
public class GcsRbpStorageService implements RbpStorageService {
    private final Storage storage;
    private final String bucketName;
    
    @Override
    public String store(String correlationId, byte[] payload) {
        BlobId blobId = BlobId.of(bucketName, correlationId);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType("application/octet-stream")
            .build();
        
        storage.create(blobInfo, payload);
        
        return String.format("gcs://%s/%s", bucketName, correlationId);
    }
    
    @Override
    public byte[] retrieve(String uri) {
        // Parse URI: gcs://bucket/correlation-id
        String correlationId = extractCorrelationId(uri);
        
        BlobId blobId = BlobId.of(bucketName, correlationId);
        Blob blob = storage.get(blobId);
        
        return blob.getContent();
    }
    
    @Override
    public void delete(String uri) {
        String correlationId = extractCorrelationId(uri);
        BlobId blobId = BlobId.of(bucketName, correlationId);
        storage.delete(blobId);
    }
}
```

**Configuration:**
- Use Workload Identity for GKE clusters (recommended)
- Service account key file for VMs
- Configure object lifecycle rules for automatic expiration

### 4.2 Data Model Changes

#### RBPSpec Value Object

First, we introduce a value object to encapsulate RBP metadata:

```java
/**
 * Really Big Payload specification containing metadata about externally stored payloads.
 * This is stored as a JSON column in the database.
 */
public record RBPSpec(
    String uri,                    // Storage URI (e.g., "azure://container/correlation-id")
    Long sizeBytes,                // Original payload size in bytes
    LocalDateTime storedAt         // When payload was stored externally
) {
    public RBPSpec {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("RBP URI cannot be null or blank");
        }
        if (sizeBytes == null || sizeBytes <= 0) {
            throw new IllegalArgumentException("RBP size must be positive");
        }
        if (storedAt == null) {
            throw new IllegalArgumentException("RBP storedAt cannot be null");
        }
    }
}
```

#### OutboxEvent Entity Enhancement

```java
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    // ... existing fields ...
    
    /**
     * Optional RBP specification. Present when payload is stored externally.
     * Stored as JSON in the database.
     */
    @Column(columnDefinition = "TEXT")
    @Convert(converter = RBPSpecConverter.class)
    private RBPSpec rbp;
    
    // When rbp is present, the `payload` column contains metadata/reference only
    
    /**
     * Canonical-style accessor for RBP specification.
     * @return Optional containing RBPSpec if present, empty otherwise
     */
    public Optional<RBPSpec> rbp() {
        return Optional.ofNullable(rbp);
    }
    
    /**
     * Traditional getter for RBP specification (for JPA/framework compatibility).
     * Use rbp() for cleaner code.
     */
    public Optional<RBPSpec> getRbp() {
        return rbp();
    }
    
    public void setRbp(RBPSpec rbp) {
        this.rbp = rbp;
    }
}
```

**Note on Records**: While JPA entities cannot be records (they need to be mutable for JPA to work), we provide a canonical-style accessor `rbp()` that mimics the record pattern for cleaner code. This allows usage like:

```java
event.rbp().ifPresent(spec -> ...);  // Clean, canonical style
event.getRbp().ifPresent(spec -> ...);  // Traditional style (also works)
```

#### JPA Converter for RBPSpec

```java
@Converter
public class RBPSpecConverter implements AttributeConverter<RBPSpec, String> {
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    
    @Override
    public String convertToDatabaseColumn(RBPSpec rbpSpec) {
        if (rbpSpec == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(rbpSpec);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize RBPSpec", e);
        }
    }
    
    @Override
    public RBPSpec convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, RBPSpec.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize RBPSpec", e);
        }
    }
}
```

**Migration Strategy:**
- Add single `rbp` column with TEXT type (stores JSON)
- Existing events continue to work (rbp=null)
- No data migration required
- **Correlation ID Requirement**: For RBP events, a correlation ID is required and will be auto-generated if not provided. This correlation ID serves as the unique retrieval key for the payload.

#### Database Schema

```sql
ALTER TABLE outbox_events 
ADD COLUMN rbp TEXT;

-- Optional index for RBP cleanup queries (PostgreSQL example with JSON path)
CREATE INDEX idx_outbox_events_rbp_cleanup 
ON outbox_events((rbp->>'storedAt'))
WHERE rbp IS NOT NULL AND sent_at IS NOT NULL;

-- For databases without JSON support, the converter handles JSON serialization
```

**Benefits of the Optional Approach:**
- **Type Safety**: `Optional<RBPSpec>` provides compile-time safety and clear semantics
- **Null Safety**: Eliminates need for null checks on individual fields
- **Clean API**: `event.rbp().ifPresent(payload -> ...)` provides elegant conditional logic with canonical-style accessor
- **Cohesion**: Related RBP fields are grouped together in a single value object
- **Extensibility**: Easy to add new RBP metadata fields without schema changes (just update JSON)
- **Single Column**: Reduces schema complexity - one TEXT column instead of multiple columns

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
        
        // Create RBPSpec with metadata
        RBPSpec rbpSpec = new RBPSpec(
            rbpUri,
            (long) payloadBytes.length,
            LocalDateTime.now()
        );
        
        // Create lightweight reference event
        OutboxEvent event = new OutboxEvent(
            aggregateType, aggregateId, eventType, effectiveCorrelationId,
            createRbpReferencePayload(effectiveCorrelationId)
        );
        event.setRbp(rbpSpec);  // Set the RBP specification
        
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
        
        // Use Optional pattern with canonical-style accessor
        return event.rbp()
            .map(rbpSpec -> storageService.retrieve(rbpSpec.uri()))
            .orElseThrow(() -> new IllegalStateException(
                "Event is not an RBP: " + correlationId));
    }
    
    @Override
    public boolean exists(String correlationId) {
        return repository.findByCorrelationId(correlationId)
            .flatMap(OutboxEvent::rbp)
            .isPresent();
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
- For RBP events (when `event.rbp().isPresent()`), it publishes only the small reference payload
- Consumers detect the RBP reference and use RbpClient to fetch the actual data

**No changes required to OutboxEventPublisher** - it remains payload-agnostic.

**Example Usage in Event Processing:**
```java
// Processing outbox events
public void processEvent(OutboxEvent event) {
    // Use Optional pattern with canonical-style accessor
    event.rbp().ifPresent(rbpSpec -> {
        log.info("Publishing RBP event with URI: {}, size: {} bytes", 
                 rbpSpec.uri(), rbpSpec.sizeBytes());
        // Additional RBP-specific logging or metrics
    });
    
    // Publish to Kafka (works for both RBP and normal events)
    kafkaTemplate.send(event.getEventType(), event.getPayload());
}
```

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
        // Use Optional pattern with canonical-style accessor
        event.rbp().ifPresent(rbpSpec -> {
            try {
                storageService.delete(rbpSpec.uri());
                // Option: delete entire event or just clear RBP field
                event.setRbp(null);
                repository.save(event);
            } catch (Exception e) {
                log.error("Failed to cleanup RBP: {}", rbpSpec.uri(), e);
            }
        });
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
      type: azure  # or 'gcs'
      # Azure Blob Storage configuration
      azure:
        connection-string: ${AZURE_STORAGE_CONNECTION_STRING}
        container-name: routebox-rbp-prod
        # Optional: Use Managed Identity instead of connection string
        # account-name: ${AZURE_STORAGE_ACCOUNT_NAME}
        # use-managed-identity: true
      # Google Cloud Storage configuration
      gcs:
        bucket-name: routebox-rbp-prod
        project-id: ${GCP_PROJECT_ID}
        # Optional: specify credentials file
        # credentials-file: /path/to/service-account-key.json
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

**Cloud Storage Access Control:**
- **Azure**: Use Managed Identity for authentication (no credentials in config)
  - Assign "Storage Blob Data Contributor" role to RouteBox service principal
  - Configure container-level access policies
- **GCP**: Use Workload Identity for GKE or service account with minimal permissions
  - Grant "Storage Object Admin" role for specific bucket
  - Use signed URLs for time-limited access if needed

### 5.2 Data Encryption

**In Transit:**
- HTTPS/TLS for cloud storage API calls (enforced by default)
- Kafka SSL/TLS (already supported)

**At Rest:**
- **Azure**: Enable server-side encryption with platform-managed or customer-managed keys
  - Platform-managed keys: Enabled by default
  - Customer-managed keys: Use Azure Key Vault for key management
- **GCP**: Server-side encryption enabled by default
  - Google-managed encryption keys: Automatic
  - Customer-managed encryption keys (CMEK): Use Cloud KMS for key management

### 5.3 Correlation ID as Security Key

The correlation ID serves as the retrieval key. Considerations:
- **Uniqueness**: The correlationId field in OutboxEvent is marked as `unique = true` in the database schema, ensuring global uniqueness across all events
- **Generation**: When not provided, the system automatically generates a UUID v4, providing unpredictability
- **Security**: Consider adding HMAC signature for additional security in high-security environments
- **Rate Limiting**: Implement rate limiting on RbpClient.get() to prevent abuse and enumeration attacks
- **Access Control**: For sensitive payloads, consider additional authentication/authorization checks beyond correlation ID

**Note on Uniqueness**: Since correlation IDs are globally unique in the current schema, they can safely be used as the sole retrieval key. If the schema changes in the future to allow non-unique correlation IDs, the retrieval API should be updated to use a composite key (e.g., `aggregateType + aggregateId + correlationId` or the outbox event ID).

## 6. Performance and Scalability

### 6.1 Performance Impact

**Write Path:**
- Additional latency for external storage write:
  - Azure Blob Storage: 10-50ms for standard tier, 50-100ms for cool tier
  - Google Cloud Storage: 10-50ms for standard class, varies by region
- Still within same database transaction (ACID guarantees maintained)
- Configurable: only large payloads incur this cost

**Read Path:**
- Consumers retrieve payload on-demand (not in critical path)
- Parallel retrieval possible for batch consumers
- Caching layer can be added if needed

### 6.2 Scalability

**Azure Blob Storage:**
- Up to 20,000 requests per second per blob storage account
- Maximum throughput: 60 Gb/s ingress, 120 Gb/s egress per account
- Virtually unlimited storage capacity
- Auto-scales with demand

**Google Cloud Storage:**
- 5,000 write requests per second and 10,000 read requests per second per bucket
- Bandwidth: Up to 200 Gb/s per bucket
- Unlimited storage capacity
- Auto-scales with demand

**Multi-Region Considerations:**
- Use geo-replication for cross-region disaster recovery
- Configure region-specific buckets/containers to minimize latency

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
- [ ] Implement RbpStorageService interface
- [ ] Implement Azure Blob Storage backend
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

### Phase 4: Google Cloud Storage Support (Sprint 6)
- [ ] Implement GCS backend implementation
- [ ] Add GCS-specific configuration options
- [ ] Integration tests for GCS backend
- [ ] Documentation updates for multi-cloud support

### Phase 5: Production Hardening (Sprint 7-8)
- [ ] Load testing with large payloads
- [ ] Security audit and penetration testing
- [ ] Multi-region failover testing
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
- RbpStorageService implementations (Azure, GCS with mocked clients)
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
- Storage backend comparison (Azure vs GCS)
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
- Multi-region replication strategies and failover
- RBP analytics and usage reporting
- Async retrieval API with callbacks
- RBP payload transformation/filtering APIs
- Support for additional cloud providers (AWS S3, Alibaba Cloud OSS)

## 12. References

- [Azure Blob Storage Documentation](https://docs.microsoft.com/en-us/azure/storage/blobs/)
- [Azure Blob Storage Best Practices](https://docs.microsoft.com/en-us/azure/storage/blobs/storage-performance-checklist)
- [Google Cloud Storage Documentation](https://cloud.google.com/storage/docs)
- [Google Cloud Storage Best Practices](https://cloud.google.com/storage/docs/best-practices)
- [Kafka Message Size Best Practices](https://kafka.apache.org/documentation/#design_messages)
- [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)

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

### Azure Blob Storage Configuration Example

```yaml
# application.yml
routebox:
  rbp:
    enabled: true
    threshold-bytes: 102400  # 100KB
    storage:
      type: azure
      azure:
        connection-string: ${AZURE_STORAGE_CONNECTION_STRING}
        container-name: routebox-rbp
        # Alternative: Use Managed Identity (recommended for production)
        # account-name: ${AZURE_STORAGE_ACCOUNT_NAME}
        # use-managed-identity: true
    retention:
      days: 7
      cleanup-cron: "0 0 2 * * *"

# Azure authentication options:
# 1. Connection string (development/testing)
# 2. Managed Identity (recommended for Azure VMs/AKS - no credentials needed)
# 3. Service Principal with client ID/secret
```

### Google Cloud Storage Configuration Example

```yaml
# application.yml
routebox:
  rbp:
    enabled: true
    threshold-bytes: 102400  # 100KB
    storage:
      type: gcs
      gcs:
        bucket-name: routebox-rbp
        project-id: ${GCP_PROJECT_ID}
        # Optional: specify credentials file for development
        # credentials-file: /path/to/service-account-key.json
    retention:
      days: 7
      cleanup-cron: "0 0 2 * * *"

# GCP authentication options:
# 1. Workload Identity (recommended for GKE - no credentials needed)
# 2. Service account key file (development/testing)
# 3. Application Default Credentials (ADC)
```

### Maven Dependencies

#### Azure Blob Storage

```xml
<!-- Azure Storage Blob SDK -->
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-storage-blob</artifactId>
    <version>12.25.0</version>
</dependency>

<!-- Azure Identity for Managed Identity support -->
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-identity</artifactId>
    <version>1.11.0</version>
</dependency>
```

#### Google Cloud Storage

```xml
<!-- Google Cloud Storage SDK -->
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-storage</artifactId>
    <version>2.30.0</version>
</dependency>

<!-- Spring Cloud GCP (optional, provides auto-configuration) -->
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-storage</artifactId>
    <version>4.9.0</version>
</dependency>
```

---

**Specification Version:** 1.0  
**Last Updated:** 2025-11-05  
**Status:** Draft  
**Owner:** RouteBox Team
