package com.example.catbox.client;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example demonstrating how to use OutboxFilter in a Kafka consumer.
 * This test simulates a typical Kafka consumer scenario.
 */
class OutboxFilterUsageExampleTest {

    @Test
    void demonstrateKafkaConsumerDeduplication() {
        // Given: A filter and some simulated Kafka messages
        OutboxFilter filter = new InMemoryOutboxFilter();
        
        // Simulate messages with correlation IDs from Kafka headers
        String message1 = "{\"orderId\": \"123\", \"status\": \"CREATED\"}";
        String correlationId1 = "corr-id-001";
        
        String message2 = "{\"orderId\": \"124\", \"status\": \"CREATED\"}";
        String correlationId2 = "corr-id-002";
        
        // When: First message arrives
        boolean isDuplicate1 = filter.deduped(correlationId1);
        
        // Then: It's not a duplicate, should be processed
        assertThat(isDuplicate1).isFalse();
        processMessage(message1); // Process the message
        
        // When: Same message arrives again (Kafka retry or redelivery)
        boolean isDuplicate1Retry = filter.deduped(correlationId1);
        
        // Then: It's a duplicate, should be skipped
        assertThat(isDuplicate1Retry).isTrue();
        // Skip processing
        
        // When: Different message arrives
        boolean isDuplicate2 = filter.deduped(correlationId2);
        
        // Then: It's not a duplicate, should be processed
        assertThat(isDuplicate2).isFalse();
        processMessage(message2); // Process the message
    }

    @Test
    void demonstrateMultipleConsumerInstances() {
        // Given: A shared filter (in production, this would be database-backed)
        OutboxFilter filter = new InMemoryOutboxFilter();
        
        String correlationId = "shared-corr-id-001";
        
        // Simulate two consumer instances receiving the same message
        // Instance 1
        boolean instance1Result = filter.deduped(correlationId);
        assertThat(instance1Result).isFalse(); // Instance 1 processes it
        
        // Instance 2 (receives duplicate)
        boolean instance2Result = filter.deduped(correlationId);
        assertThat(instance2Result).isTrue(); // Instance 2 skips it
    }

    @Test
    void demonstratePrePopulationForRecovery() {
        // Given: A filter and some already-processed correlation IDs
        // (e.g., loaded from database during startup)
        OutboxFilter filter = new InMemoryOutboxFilter();
        
        // Pre-populate with already processed IDs
        filter.markProcessed("already-processed-1");
        filter.markProcessed("already-processed-2");
        filter.markProcessed("already-processed-3");
        
        // When: These messages arrive again
        // Then: They should be recognized as duplicates
        assertThat(filter.deduped("already-processed-1")).isTrue();
        assertThat(filter.deduped("already-processed-2")).isTrue();
        assertThat(filter.deduped("already-processed-3")).isTrue();
        
        // When: A new message arrives
        // Then: It should not be a duplicate
        assertThat(filter.deduped("new-message-1")).isFalse();
    }

    @Test
    void demonstrateReadOnlyCheck() {
        // Given: A filter with some processed IDs
        OutboxFilter filter = new InMemoryOutboxFilter();
        filter.deduped("processed-id");
        
        // When: Checking if an ID is processed without marking it
        boolean isProcessed = filter.isProcessed("processed-id");
        boolean isNotProcessed = filter.isProcessed("not-processed-id");
        
        // Then: Can check without side effects
        assertThat(isProcessed).isTrue();
        assertThat(isNotProcessed).isFalse();
        
        // Verify that isProcessed didn't mark the ID
        assertThat(filter.deduped("not-processed-id")).isFalse();
    }

    @Test
    void demonstrateNullAndEmptyCorrelationIdHandling() {
        // Given: A filter
        OutboxFilter filter = new InMemoryOutboxFilter();
        
        // When: Messages arrive without correlation IDs
        boolean nullResult = filter.deduped(null);
        boolean emptyResult = filter.deduped("");
        
        // Then: They are treated as not processed (allowing processing)
        assertThat(nullResult).isFalse();
        assertThat(emptyResult).isFalse();
        
        // Note: Each call with null/empty is independent
        assertThat(filter.deduped(null)).isFalse();
        assertThat(filter.deduped("")).isFalse();
    }

    // Simulated message processing
    private void processMessage(String message) {
        // In a real application, this would:
        // - Deserialize the message
        // - Validate the data
        // - Perform business logic
        // - Save to database
        System.out.println("Processing message: " + message);
    }
    
    /**
     * Example of a typical Kafka listener method signature.
     * This is just for documentation purposes to show how OutboxFilter
     * would be used in a real Kafka listener.
     */
    static class KafkaListenerExample {
        
        private final OutboxFilter outboxFilter;
        
        public KafkaListenerExample(OutboxFilter outboxFilter) {
            this.outboxFilter = outboxFilter;
        }
        
        /**
         * Typical Spring Kafka listener that uses OutboxFilter for deduplication.
         * 
         * In a real application, this method would be annotated with:
         * @KafkaListener(topics = "order-events")
         * 
         * @param message the Kafka message payload
         * @param headers the Kafka message headers containing correlationId
         */
        public void handleOrderEvent(String message, Map<String, Object> headers) {
            String correlationId = (String) headers.get("correlationId");
            
            if (outboxFilter.deduped(correlationId)) {
                // Already processed, skip
                System.out.println("Skipping duplicate message: " + correlationId);
                return;
            }
            
            // Process the message
            processOrderEvent(message);
        }
        
        private void processOrderEvent(String message) {
            // Business logic here
        }
    }
}
