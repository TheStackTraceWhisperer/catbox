package com.example.orderprocessor.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.orderprocessor.model.OrderCreatedPayload;
import com.example.orderprocessor.service.OrderEventProcessingService;
import com.example.routebox.client.OutboxFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

/** Unit tests for OrderEventListener. */
@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

  @Mock private OutboxFilter outboxFilter;

  @Mock private OrderEventProcessingService processingService;

  @Mock private Acknowledgment acknowledgment;

  private OrderEventListener listener;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    listener = new OrderEventListener(outboxFilter, processingService, objectMapper);
  }

  @Test
  void testHandleOrderCreated_NewMessage_ProcessedAndAcknowledged() throws Exception {
    // Given
    String correlationId = "test-corr-1";
    OrderCreatedPayload payload =
        new OrderCreatedPayload(1L, "Alice", "Widget", new BigDecimal("99.99"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    // Mock: not a duplicate
    when(outboxFilter.deduped(correlationId, "order-processor")).thenReturn(false);

    // When
    listener.handleOrderCreated(message, correlationId, acknowledgment);

    // Then
    verify(outboxFilter).deduped(correlationId, "order-processor");
    verify(processingService).processOrderCreated(eq(payload), eq(correlationId));
    verify(acknowledgment).acknowledge();
  }

  @Test
  void testHandleOrderCreated_DuplicateMessage_SkippedAndAcknowledged() throws Exception {
    // Given
    String correlationId = "test-corr-duplicate";
    OrderCreatedPayload payload =
        new OrderCreatedPayload(2L, "Bob", "Gadget", new BigDecimal("149.99"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    // Mock: is a duplicate
    when(outboxFilter.deduped(correlationId, "order-processor")).thenReturn(true);

    // When
    listener.handleOrderCreated(message, correlationId, acknowledgment);

    // Then
    verify(outboxFilter).deduped(correlationId, "order-processor");
    verify(processingService, never()).processOrderCreated(any(), anyString());
    verify(acknowledgment).acknowledge();
  }

  @Test
  void testHandleOrderCreated_ProcessingException_Thrown() throws Exception {
    // Given
    String correlationId = "test-corr-fail";
    OrderCreatedPayload payload =
        new OrderCreatedPayload(3L, "Charlie", "Item", new BigDecimal("50.00"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    when(outboxFilter.deduped(correlationId, "order-processor")).thenReturn(false);
    doThrow(new OrderEventProcessingService.ProcessingException("Simulated failure"))
        .when(processingService)
        .processOrderCreated(eq(payload), eq(correlationId));

    // When/Then - Exception should propagate to be handled by DefaultErrorHandler
    try {
      listener.handleOrderCreated(message, correlationId, acknowledgment);
      throw new AssertionError("Expected ProcessingException to be thrown");
    } catch (OrderEventProcessingService.ProcessingException e) {
      // Expected - error handler will retry this
      verify(outboxFilter).deduped(correlationId, "order-processor");
      verify(processingService).processOrderCreated(eq(payload), eq(correlationId));
      verify(acknowledgment, never()).acknowledge();
    }
  }

  @Test
  void testHandleOrderCreated_JsonProcessingException_Thrown() throws Exception {
    // Given
    String correlationId = "test-corr-unexpected";
    String invalidMessage = "{ invalid json";

    when(outboxFilter.deduped(correlationId, "order-processor")).thenReturn(false);

    // When/Then - Exception should propagate to be handled by DefaultErrorHandler
    try {
      listener.handleOrderCreated(invalidMessage, correlationId, acknowledgment);
      throw new AssertionError("Expected JsonProcessingException to be thrown");
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      // Expected - error handler will route to DLT (non-retryable)
      verify(outboxFilter).deduped(correlationId, "order-processor");
      verify(processingService, never()).processOrderCreated(any(), anyString());
      verify(acknowledgment, never()).acknowledge();
    }
  }

  @Test
  void testHandleOrderCreated_NullCorrelationId_ProcessedAndAcknowledged() throws Exception {
    // Given
    OrderCreatedPayload payload =
        new OrderCreatedPayload(4L, "Diana", "Product", new BigDecimal("75.00"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    // When
    listener.handleOrderCreated(message, null, acknowledgment);

    // Then - should skip deduplication check and process normally
    verify(outboxFilter, never()).deduped(anyString(), anyString());
    verify(processingService).processOrderCreated(eq(payload), isNull());
    verify(acknowledgment).acknowledge();
  }

  @Test
  void testHandleOrderStatusChanged_NewMessage_ProcessedAndAcknowledged() throws Exception {
    // Given
    String correlationId = "test-status-1";
    var payload =
        new com.example.orderprocessor.model.OrderStatusChangedPayload(1L, "PENDING", "SHIPPED");
    String message = objectMapper.writeValueAsString(payload);

    when(outboxFilter.deduped(correlationId, "order-processor")).thenReturn(false);

    // When
    listener.handleOrderStatusChanged(message, correlationId, acknowledgment);

    // Then
    verify(outboxFilter).deduped(correlationId, "order-processor");
    verify(processingService).processOrderStatusChanged(eq(payload), eq(correlationId));
    verify(acknowledgment).acknowledge();
  }

  @Test
  void testHandleOrderStatusChanged_DuplicateMessage_SkippedAndAcknowledged() throws Exception {
    // Given
    String correlationId = "test-status-duplicate";
    var payload =
        new com.example.orderprocessor.model.OrderStatusChangedPayload(2L, "PENDING", "DELIVERED");
    String message = objectMapper.writeValueAsString(payload);

    when(outboxFilter.deduped(correlationId, "order-processor")).thenReturn(true);

    // When
    listener.handleOrderStatusChanged(message, correlationId, acknowledgment);

    // Then
    verify(outboxFilter).deduped(correlationId, "order-processor");
    verify(processingService, never()).processOrderStatusChanged(any(), anyString());
    verify(acknowledgment).acknowledge();
  }
}
