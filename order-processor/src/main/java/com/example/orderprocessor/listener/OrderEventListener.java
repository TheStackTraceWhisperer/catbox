package com.example.orderprocessor.listener;

import com.example.orderprocessor.model.OrderCreatedPayload;
import com.example.orderprocessor.model.OrderStatusChangedPayload;
import com.example.orderprocessor.service.OrderEventProcessingService;
import com.example.routebox.client.OutboxFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka listener for order events that uses OutboxFilter for deduplication.
 *
 * <p>This listener demonstrates:
 *
 * <ul>
 *   <li>Deduplication using OutboxFilter to prevent duplicate processing
 *   <li>Manual acknowledgment for fine-grained control over message processing
 *   <li>Handling of processing failures with proper error logging
 *   <li>Processing of multiple event types from different topics
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

  private static final String CONSUMER_GROUP = "order-processor";

  private final OutboxFilter outboxFilter;
  private final OrderEventProcessingService processingService;
  private final ObjectMapper objectMapper;

  /**
   * Listen to OrderCreated events from Kafka.
   *
   * <p>Uses OutboxFilter to deduplicate messages based on correlation ID. Only processes each
   * unique correlation ID once per consumer group.
   *
   * <p>The deduplication check and business logic processing happen within the same transaction to
   * ensure atomicity. If processing fails, the transaction is rolled back and the deduplication
   * marker is not saved, allowing the message to be retried.
   *
   * <p>Error handling is managed by Spring Kafka's DefaultErrorHandler, which retries transient
   * errors and routes permanent errors to a Dead Letter Topic (DLT).
   *
   * @param message the JSON message payload
   * @param correlationId the correlation ID from the message header
   * @param acknowledgment manual acknowledgment for the message
   */
  @KafkaListener(
      topics = "OrderCreated",
      groupId = CONSUMER_GROUP,
      containerFactory = "kafkaListenerContainerFactory")
  @Transactional
  public void handleOrderCreated(
      @Payload String message,
      @Header(value = "correlationId", required = false) String correlationId,
      Acknowledgment acknowledgment)
      throws Exception {

    log.debug("Received OrderCreated message - correlationId: {}", correlationId);

    // Check for deduplication using OutboxFilter (read-only check)
    if (correlationId != null && outboxFilter.isProcessed(correlationId, CONSUMER_GROUP)) {
      log.info("Skipping duplicate OrderCreated message - correlationId: {}", correlationId);
      acknowledgment.acknowledge();
      return;
    }

    // Parse the message
    OrderCreatedPayload payload = objectMapper.readValue(message, OrderCreatedPayload.class);

    // Process the event
    processingService.processOrderCreated(payload, correlationId);

    // Mark as processed after successful processing (within the same transaction)
    if (correlationId != null) {
      outboxFilter.markProcessed(correlationId, CONSUMER_GROUP);
    }

    // Acknowledge the message after successful processing
    acknowledgment.acknowledge();
    log.debug("Acknowledged OrderCreated message - correlationId: {}", correlationId);
  }

  /**
   * Listen to OrderStatusChanged events from Kafka.
   *
   * <p>Uses OutboxFilter to deduplicate messages based on correlation ID. Only processes each
   * unique correlation ID once per consumer group.
   *
   * <p>The deduplication check and business logic processing happen within the same transaction to
   * ensure atomicity. If processing fails, the transaction is rolled back and the deduplication
   * marker is not saved, allowing the message to be retried.
   *
   * <p>Error handling is managed by Spring Kafka's DefaultErrorHandler, which retries transient
   * errors and routes permanent errors to a Dead Letter Topic (DLT).
   *
   * @param message the JSON message payload
   * @param correlationId the correlation ID from the message header
   * @param acknowledgment manual acknowledgment for the message
   */
  @KafkaListener(
      topics = "OrderStatusChanged",
      groupId = CONSUMER_GROUP,
      containerFactory = "kafkaListenerContainerFactory")
  @Transactional
  public void handleOrderStatusChanged(
      @Payload String message,
      @Header(value = "correlationId", required = false) String correlationId,
      Acknowledgment acknowledgment)
      throws Exception {

    log.debug("Received OrderStatusChanged message - correlationId: {}", correlationId);

    // Check for deduplication using OutboxFilter (read-only check)
    if (correlationId != null && outboxFilter.isProcessed(correlationId, CONSUMER_GROUP)) {
      log.info("Skipping duplicate OrderStatusChanged message - correlationId: {}", correlationId);
      acknowledgment.acknowledge();
      return;
    }

    // Parse the message
    OrderStatusChangedPayload payload =
        objectMapper.readValue(message, OrderStatusChangedPayload.class);

    // Process the event
    processingService.processOrderStatusChanged(payload, correlationId);

    // Mark as processed after successful processing (within the same transaction)
    if (correlationId != null) {
      outboxFilter.markProcessed(correlationId, CONSUMER_GROUP);
    }

    // Acknowledge the message after successful processing
    acknowledgment.acknowledge();
    log.debug("Acknowledged OrderStatusChanged message - correlationId: {}", correlationId);
  }
}
