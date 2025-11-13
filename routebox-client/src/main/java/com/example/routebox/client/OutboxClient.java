package com.example.routebox.client;

import com.example.routebox.client.metrics.RouteBoxClientMetricsService;
import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.MANDATORY)
public class OutboxClient {

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;
  private final RouteBoxClientMetricsService metricsService;

  public OutboxClient(
      final OutboxEventRepository outboxEventRepository,
      final ObjectMapper objectMapper,
      @Autowired(required = false) final RouteBoxClientMetricsService metricsService) {
    this.outboxEventRepository = outboxEventRepository;
    this.objectMapper = objectMapper;
    this.metricsService = metricsService;
  }

  /**
   * Serializes the given payload and writes it to the outbox.
   *
   * @param aggregateType The "type" of the entity (e.g., "Order")
   * @param aggregateId The ID of the entity (e.g., "123")
   * @param eventType The event name (e.g., "OrderCreated")
   * @param payload The POJO/Record/Map to be serialized to JSON
   */
  public void write(
      final String aggregateType,
      final String aggregateId,
      final String eventType,
      final Object payload) {
    // Delegate to the method with correlationId, passing null
    this.write(aggregateType, aggregateId, eventType, null, payload);
  }

  /**
   * Serializes the given payload and writes it to the outbox with a specific correlation ID.
   *
   * @param aggregateType The "type" of the entity (e.g., "Order")
   * @param aggregateId The ID of the entity (e.g., "123")
   * @param eventType The event name (e.g., "OrderCreated")
   * @param correlationId A unique ID for tracing
   * @param payload The POJO/Record/Map to be serialized to JSON
   */
  public void write(
      final String aggregateType,
      final String aggregateId,
      final String eventType,
      final String correlationId,
      final Object payload) {
    try {
      // 1. Serialize the domain-agnostic object
      String jsonPayload = objectMapper.writeValueAsString(payload);

      // 2. Create and save the event
      OutboxEvent event =
          new OutboxEvent(aggregateType, aggregateId, eventType, correlationId, jsonPayload);
      outboxEventRepository.save(event);

      // 3. Record successful write
      recordOutboxWriteSuccess();
    } catch (JsonProcessingException e) {
      // Record failure
      recordOutboxWriteFailure();
      // Fatal serialization error - propagate as unchecked exception
      throw new RuntimeException("Failed to serialize outbox event payload for: " + eventType, e);
    }
  }

  private void recordOutboxWriteSuccess() {
    if (metricsService != null) {
      metricsService.recordOutboxWriteSuccess();
    }
  }

  private void recordOutboxWriteFailure() {
    if (metricsService != null) {
      metricsService.recordOutboxWriteFailure();
    }
  }
}
