package com.example.routebox.client;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example demonstrating how to use OutboxFilter in a Kafka consumer.
 * This test simulates a typical Kafka consumer scenario using a simple
 * test implementation.
 */
class OutboxFilterUsageExampleTest {

    private static final String CONSUMER_GROUP = "order-processor";

    @Test
    void demonstrateKafkaConsumerDeduplication() {
        // Given: A filter and some simulated Kafka messages
        OutboxFilter filter = new TestOutboxFilter();

        // Simulate messages with correlation IDs from Kafka headers
        String message1 = "{\"orderId\": \"123\", \"status\": \"CREATED\"}";
        String correlationId1 = "corr-id-001";

        String message2 = "{\"orderId\": \"124\", \"status\": \"CREATED\"}";
        String correlationId2 = "corr-id-002";

        // When: First message arrives
        boolean isDuplicate1 = filter.deduped(correlationId1, CONSUMER_GROUP);

        // Then: It's not a duplicate, should be processed
        assertThat(isDuplicate1).isFalse();
        processMessage(message1); // Process the message

        // When: Same message arrives again (Kafka retry or redelivery)
        boolean isDuplicate1Retry = filter.deduped(correlationId1,
                CONSUMER_GROUP);

        // Then: It's a duplicate, should be skipped
        assertThat(isDuplicate1Retry).isTrue();
        // Skip processing

        // When: Different message arrives
        boolean isDuplicate2 = filter.deduped(correlationId2, CONSUMER_GROUP);

        // Then: It's not a duplicate, should be processed
        assertThat(isDuplicate2).isFalse();
        processMessage(message2); // Process the message
    }

    @Test
    void demonstrateMultipleConsumerGroups() {
        // Given: A shared filter
        OutboxFilter filter = new TestOutboxFilter();

        String correlationId = "shared-corr-id-001";

        // Simulate two different consumer groups processing same message
        // Group 1
        boolean group1Result = filter.deduped(correlationId, "group-1");
        assertThat(group1Result).isFalse(); // Group 1 processes it

        // Group 2 (different group, same correlation ID)
        boolean group2Result = filter.deduped(correlationId, "group-2");
        assertThat(group2Result).isFalse(); // Group 2 also processes it

        // Group 1 again (duplicate)
        boolean group1Duplicate = filter.deduped(correlationId, "group-1");
        assertThat(group1Duplicate).isTrue(); // Group 1 skips duplicate
    }

    @Test
    void demonstratePrePopulationForRecovery() {
        // Given: A filter and some already-processed correlation IDs
        OutboxFilter filter = new TestOutboxFilter();

        // Pre-populate with already processed IDs
        filter.markProcessed("already-processed-1", CONSUMER_GROUP);
        filter.markProcessed("already-processed-2", CONSUMER_GROUP);
        filter.markProcessed("already-processed-3", CONSUMER_GROUP);

        // When: These messages arrive again
        // Then: They should be recognized as duplicates
        assertThat(filter.deduped("already-processed-1", CONSUMER_GROUP))
                .isTrue();
        assertThat(filter.deduped("already-processed-2", CONSUMER_GROUP))
                .isTrue();
        assertThat(filter.deduped("already-processed-3", CONSUMER_GROUP))
                .isTrue();

        // When: A new message arrives
        // Then: It should not be a duplicate
        assertThat(filter.deduped("new-message-1", CONSUMER_GROUP))
                .isFalse();
    }

    @Test
    void demonstrateReadOnlyCheck() {
        // Given: A filter with some processed IDs
        OutboxFilter filter = new TestOutboxFilter();
        filter.deduped("processed-id", CONSUMER_GROUP);

        // When: Checking if an ID is processed without marking it
        boolean isProcessed = filter.isProcessed("processed-id",
                CONSUMER_GROUP);
        boolean isNotProcessed = filter.isProcessed("not-processed-id",
                CONSUMER_GROUP);

        // Then: Can check without side effects
        assertThat(isProcessed).isTrue();
        assertThat(isNotProcessed).isFalse();

        // Verify that isProcessed didn't mark the ID
        assertThat(filter.deduped("not-processed-id", CONSUMER_GROUP))
                .isFalse();
    }

    @Test
    void demonstrateMarkUnprocessed() {
        // Given: A filter with a processed message
        OutboxFilter filter = new TestOutboxFilter();
        filter.deduped("correlation-id-1", CONSUMER_GROUP);
        assertThat(filter.isProcessed("correlation-id-1", CONSUMER_GROUP))
                .isTrue();

        // When: Marking the message as unprocessed
        filter.markUnprocessed("correlation-id-1", CONSUMER_GROUP);

        // Then: The message can be reprocessed
        assertThat(filter.isProcessed("correlation-id-1", CONSUMER_GROUP))
                .isFalse();
        assertThat(filter.deduped("correlation-id-1", CONSUMER_GROUP))
                .isFalse();
    }

    @Test
    void demonstrateNullAndEmptyCorrelationIdHandling() {
        // Given: A filter
        OutboxFilter filter = new TestOutboxFilter();

        // When: Messages arrive without correlation IDs
        boolean nullResult = filter.deduped(null, CONSUMER_GROUP);
        boolean emptyResult = filter.deduped("", CONSUMER_GROUP);

        // Then: They are treated as not processed (allowing processing)
        assertThat(nullResult).isFalse();
        assertThat(emptyResult).isFalse();

        // Note: Each call with null/empty is independent
        assertThat(filter.deduped(null, CONSUMER_GROUP)).isFalse();
        assertThat(filter.deduped("", CONSUMER_GROUP)).isFalse();
    }

    // Simulated message processing
    private void processMessage(final String message) {
        // In a real application, this would:
        // - Deserialize the message
        // - Validate the data
        // - Perform business logic
        // - Save to database
        // For this example, we just acknowledge the message was processed
    }

    /**
     * Simple test implementation of OutboxFilter for demonstration purposes.
     * In production, use DatabaseOutboxFilter.
     */
    private static class TestOutboxFilter implements OutboxFilter {
        private final Map<String, Set<String>> processedIdsByGroup =
                new HashMap<>();

        @Override
        public boolean deduped(final String correlationId,
                              final String consumerGroup) {
            if (correlationId == null || correlationId.isEmpty()
                    || consumerGroup == null || consumerGroup.isEmpty()) {
                return false;
            }

            Set<String> processedIds = processedIdsByGroup
                    .computeIfAbsent(consumerGroup, k -> new HashSet<>());
            return !processedIds.add(correlationId);
        }

        @Override
        public void markProcessed(final String correlationId,
                                 final String consumerGroup) {
            if (correlationId == null || correlationId.isEmpty()
                    || consumerGroup == null || consumerGroup.isEmpty()) {
                return;
            }

            processedIdsByGroup
                    .computeIfAbsent(consumerGroup, k -> new HashSet<>())
                    .add(correlationId);
        }

        @Override
        public boolean isProcessed(final String correlationId,
                                  final String consumerGroup) {
            if (correlationId == null || correlationId.isEmpty()
                    || consumerGroup == null || consumerGroup.isEmpty()) {
                return false;
            }

            Set<String> processedIds = processedIdsByGroup.get(consumerGroup);
            return processedIds != null
                    && processedIds.contains(correlationId);
        }

        @Override
        public void markUnprocessed(final String correlationId,
                                   final String consumerGroup) {
            if (correlationId == null || correlationId.isEmpty()
                    || consumerGroup == null || consumerGroup.isEmpty()) {
                return;
            }

            Set<String> processedIds = processedIdsByGroup.get(consumerGroup);
            if (processedIds != null) {
                processedIds.remove(correlationId);
            }
        }
    }

    /**
     * Example of a typical Kafka listener method signature.
     * This is just for documentation purposes to show how OutboxFilter
     * would be used in a real Kafka listener.
     */
    static class KafkaListenerExample {

        private final OutboxFilter outboxFilter;

        KafkaListenerExample(final OutboxFilter outboxFilter) {
            this.outboxFilter = outboxFilter;
        }

        /**
         * Typical Spring Kafka listener that uses OutboxFilter for
         * deduplication.
         *
         * In a real application, this method would be annotated with:
         * @KafkaListener(topics = "order-events", groupId = "order-processor")
         *
         * @param message the Kafka message payload
         * @param headers the Kafka message headers containing correlationId
         */
        public void handleOrderEvent(final String message,
                                     final Map<String, Object> headers) {
            String correlationId = (String) headers.get("correlationId");

            if (outboxFilter.deduped(correlationId, "order-processor")) {
                // Already processed, skip
                return;
            }

            // Process the message
            processOrderEvent(message);
        }

        private void processOrderEvent(final String message) {
            // Business logic here
        }
    }
}
