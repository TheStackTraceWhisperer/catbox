package com.example.catbox.client;

/**
 * Filter interface for preventing duplicate processing of Kafka event
 * listener messages. Implementations track processed correlation IDs to
 * prevent duplicate processing.
 *
 * <p>Example usage in a Kafka listener:
 * <pre>
 * {@code
 * @KafkaListener(topics = "orders")
 * public void handleOrderEvent(String message,
 *                               @Header("correlationId") String correlationId) {
 *     if (outboxFilter.deduped(correlationId)) {
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
     * processed. If this is the first time seeing this correlation ID, it
     * will be recorded and this method returns false. Subsequent calls with
     * the same correlation ID will return true.
     *
     * @param correlationId the correlation ID from the Kafka message header
     * @return true if this correlation ID has been processed before,
     *         false otherwise
     */
    boolean deduped(String correlationId);

    /**
     * Manually marks a correlation ID as processed without checking if it
     * was already processed. This can be useful for pre-populating the
     * filter or recovering from failures.
     *
     * @param correlationId the correlation ID to mark as processed
     */
    void markProcessed(String correlationId);

    /**
     * Checks if a correlation ID has been processed without recording it.
     * This is useful for read-only checks.
     *
     * @param correlationId the correlation ID to check
     * @return true if this correlation ID has been processed,
     *         false otherwise
     */
    boolean isProcessed(String correlationId);
}
