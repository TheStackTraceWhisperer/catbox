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
   * @param message the JSON message payload
   * @param correlationId the correlation ID from the message header
   * @param acknowledgment manual acknowledgment for the message
   */
  @KafkaListener(
      topics = "OrderCreated",
      groupId = CONSUMER_GROUP,
      containerFactory = "kafkaListenerContainerFactory")
  public void handleOrderCreated(
      @Payload String message,
      @Header(value = "correlationId", required = false) String correlationId,
      Acknowledgment acknowledgment) {

    log.debug("Received OrderCreated message - correlationId: {}", correlationId);

    // Check for deduplication using OutboxFilter
    if (correlationId != null && outboxFilter.deduped(correlationId, CONSUMER_GROUP)) {
      log.info("Skipping duplicate OrderCreated message - correlationId: {}", correlationId);
      acknowledgment.acknowledge();
      return;
    }

    try {
      // Parse the message
      OrderCreatedPayload payload = objectMapper.readValue(message, OrderCreatedPayload.class);

      // Process the event
      processingService.processOrderCreated(payload, correlationId);

      // Acknowledge the message after successful processing
      acknowledgment.acknowledge();
      log.debug("Acknowledged OrderCreated message - correlationId: {}", correlationId);

    } catch (OrderEventProcessingService.ProcessingException e) {
      // For intermittent failures, don't acknowledge - message will be retried
      log.error(
          "Processing failed for OrderCreated - correlationId: {}. Message will be retried.",
          correlationId,
          e);
      // Note: Not acknowledging will cause Kafka to redeliver the message

    } catch (Exception e) {
      // For unexpected errors, log and acknowledge to avoid blocking the queue
      log.error(
          "Unexpected error processing OrderCreated - correlationId: {}. Acknowledging to continue.",
          correlationId,
          e);
      acknowledgment.acknowledge();
    }
  }

  /**
   * Listen to OrderStatusChanged events from Kafka.
   *
   * <p>Uses OutboxFilter to deduplicate messages based on correlation ID. Only processes each
   * unique correlation ID once per consumer group.
   *
   * @param message the JSON message payload
   * @param correlationId the correlation ID from the message header
   * @param acknowledgment manual acknowledgment for the message
   */
  @KafkaListener(
      topics = "OrderStatusChanged",
      groupId = CONSUMER_GROUP,
      containerFactory = "kafkaListenerContainerFactory")
  public void handleOrderStatusChanged(
      @Payload String message,
      @Header(value = "correlationId", required = false) String correlationId,
      Acknowledgment acknowledgment) {

    log.debug("Received OrderStatusChanged message - correlationId: {}", correlationId);

    // Check for deduplication using OutboxFilter
    if (correlationId != null && outboxFilter.deduped(correlationId, CONSUMER_GROUP)) {
      log.info("Skipping duplicate OrderStatusChanged message - correlationId: {}", correlationId);
      acknowledgment.acknowledge();
      return;
    }

    try {
      // Parse the message
      OrderStatusChangedPayload payload =
          objectMapper.readValue(message, OrderStatusChangedPayload.class);

      // Process the event
      processingService.processOrderStatusChanged(payload, correlationId);

      // Acknowledge the message after successful processing
      acknowledgment.acknowledge();
      log.debug("Acknowledged OrderStatusChanged message - correlationId: {}", correlationId);

    } catch (OrderEventProcessingService.ProcessingException e) {
      // For intermittent failures, don't acknowledge - message will be retried
      log.error(
          "Processing failed for OrderStatusChanged - correlationId: {}. Message will be retried.",
          correlationId,
          e);
      // Note: Not acknowledging will cause Kafka to redeliver the message

    } catch (Exception e) {
      // For unexpected errors, log and acknowledge to avoid blocking the queue
      log.error(
          "Unexpected error processing OrderStatusChanged - correlationId: {}. Acknowledging to continue.",
          correlationId,
          e);
      acknowledgment.acknowledge();
    }
  }
}
