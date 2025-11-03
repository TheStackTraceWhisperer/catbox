package com.example.catbox.client;

/**
 * Filter interface for preventing duplicate processing of Kafka event
 * listener messages. Implementations track processed correlation IDs
 * per consumer group to prevent duplicate processing.
 *
 * <p>Example usage in a Kafka listener:
 * <pre>
 * {@code
 * @KafkaListener(topics = "orders", groupId = "order-processor")
 * public void handleOrderEvent(String message,
 *                               @Header("correlationId") String correlationId) {
 *     if (outboxFilter.deduped(correlationId, "order-processor")) {
 *         // This message has already been processed, acknowledge and skip
 *         return;
 *     }
 *     // Process the message...
 * }
 * }
 * </pre>
 */
public interface OutboxFilter {

    /**
     * Checks if a message with the given correlation ID has already been
     * processed by the specified consumer group. If this is the first time
     * seeing this correlation ID for this consumer group, it will be
     * recorded and this method returns false. Subsequent calls with the
     * same correlation ID and consumer group will return true.
     *
     * @param correlationId the correlation ID from the Kafka message header
     * @param consumerGroup the Kafka consumer group name
     * @return true if this correlation ID has been processed before,
     *         false otherwise
     */
    boolean deduped(String correlationId, String consumerGroup);

    /**
     * Manually marks a correlation ID as processed for a specific consumer
     * group without checking if it was already processed. This can be
     * useful for pre-populating the filter or recovering from failures.
     *
     * @param correlationId the correlation ID to mark as processed
     * @param consumerGroup the Kafka consumer group name
     */
    void markProcessed(String correlationId, String consumerGroup);

    /**
     * Checks if a correlation ID has been processed by a specific consumer
     * group without recording it. This is useful for read-only checks.
     *
     * @param correlationId the correlation ID to check
     * @param consumerGroup the Kafka consumer group name
     * @return true if this correlation ID has been processed,
     *         false otherwise
     */
    boolean isProcessed(String correlationId, String consumerGroup);

    /**
     * Marks a correlation ID as unprocessed by removing it from the
     * processed records. This allows the message to be reprocessed.
     *
     * @param correlationId the correlation ID to mark as unprocessed
     * @param consumerGroup the Kafka consumer group name
     */
    void markUnprocessed(String correlationId, String consumerGroup);
}
