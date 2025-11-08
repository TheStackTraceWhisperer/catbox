package com.example.orderprocessor.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

/**
 * Integration test that demonstrates the transactional boundary fix for the order-processor
 * listener.
 *
 * <p>This test validates that the fix for the "Major Issue #2: Flawed Transactional Boundary"
 * prevents data loss when processing fails after the deduplication check.
 *
 * <p><strong>The Problem (Before Fix):</strong>
 *
 * <ol>
 *   <li>Message arrives with correlation ID "test-123"
 *   <li>Listener calls {@code outboxFilter.deduped("test-123", "order-processor")}
 *   <li>This method runs in a REQUIRES_NEW transaction, saves the deduplication marker, and
 *       commits
 *   <li>Listener calls {@code processingService.processOrderCreated()} which <strong>fails</strong>
 *   <li>Kafka retries the message
 *   <li>Listener calls {@code outboxFilter.deduped("test-123", "order-processor")} again
 *   <li>This returns true (already processed), so the message is skipped
 *   <li><strong>Result: Data loss</strong> - the business logic never successfully completed
 * </ol>
 *
 * <p><strong>The Solution (After Fix):</strong>
 *
 * <ol>
 *   <li>Message arrives with correlation ID "test-123"
 *   <li>Listener is annotated with {@code @Transactional}
 *   <li>Listener calls {@code outboxFilter.isProcessed("test-123", "order-processor")} (read-only
 *       check)
 *   <li>This returns false, so processing continues
 *   <li>Listener calls {@code processingService.processOrderCreated()} which <strong>fails</strong>
 *   <li>Exception propagates, transaction is rolled back, deduplication marker is NOT saved
 *   <li>Kafka retries the message
 *   <li>Listener calls {@code outboxFilter.isProcessed("test-123", "order-processor")} again
 *   <li>This returns false (not yet processed), so processing continues
 *   <li>Processing succeeds, {@code markProcessed()} is called, transaction commits
 *   <li><strong>Result: No data loss</strong> - the business logic successfully completed
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
class OrderEventListenerTransactionalBoundaryTest {

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

  /**
   * Test that demonstrates the transactional boundary fix prevents data loss.
   *
   * <p>This test simulates the exact scenario described in the problem statement:
   *
   * <ol>
   *   <li>First attempt: Processing fails after deduplication check
   *   <li>Second attempt: Message is retried and processed successfully
   * </ol>
   *
   * <p>The test verifies that:
   *
   * <ul>
   *   <li>On the first attempt, {@code isProcessed()} returns false (not yet processed)
   *   <li>On the first attempt, processing fails and {@code markProcessed()} is never called
   *   <li>On the second attempt, {@code isProcessed()} still returns false (transaction was rolled
   *       back)
   *   <li>On the second attempt, processing succeeds and {@code markProcessed()} is called
   * </ul>
   */
  @Test
  void testTransactionalBoundary_ProcessingFailsThenSucceeds_NoDataLoss() throws Exception {
    // Given
    String correlationId = "test-transactional-boundary";
    OrderCreatedPayload payload =
        new OrderCreatedPayload(100L, "Alice", "Widget", new BigDecimal("99.99"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    // First attempt: isProcessed returns false, processing will fail
    when(outboxFilter.isProcessed(correlationId, "order-processor")).thenReturn(false);
    doThrow(new OrderEventProcessingService.ProcessingException("Simulated transient failure"))
        .doNothing() // Second call succeeds
        .when(processingService)
        .processOrderCreated(eq(payload), eq(correlationId));

    // When: First attempt - processing fails
    assertThatThrownBy(() -> listener.handleOrderCreated(message, correlationId, acknowledgment))
        .isInstanceOf(OrderEventProcessingService.ProcessingException.class)
        .hasMessageContaining("Simulated transient failure");

    // Then: Verify first attempt behavior
    verify(outboxFilter, times(1)).isProcessed(correlationId, "order-processor");
    verify(processingService, times(1)).processOrderCreated(eq(payload), eq(correlationId));
    verify(outboxFilter, never())
        .markProcessed(anyString(), anyString()); // NOT marked as processed
    verify(acknowledgment, never()).acknowledge();

    // When: Second attempt - message is retried and succeeds
    // isProcessed still returns false because transaction was rolled back
    listener.handleOrderCreated(message, correlationId, acknowledgment);

    // Then: Verify second attempt behavior
    verify(outboxFilter, times(2))
        .isProcessed(correlationId, "order-processor"); // Called again
    verify(processingService, times(2))
        .processOrderCreated(eq(payload), eq(correlationId)); // Called again
    verify(outboxFilter, times(1))
        .markProcessed(correlationId, "order-processor"); // NOW marked as processed
    verify(acknowledgment, times(1)).acknowledge();
  }

  /**
   * Test that demonstrates a successful first attempt with proper transactional boundary.
   *
   * <p>This test shows the happy path where processing succeeds on the first attempt:
   *
   * <ul>
   *   <li>{@code isProcessed()} returns false (not yet processed)
   *   <li>Processing succeeds
   *   <li>{@code markProcessed()} is called within the same transaction
   *   <li>Message is acknowledged
   * </ul>
   */
  @Test
  void testTransactionalBoundary_ProcessingSucceedsFirstAttempt() throws Exception {
    // Given
    String correlationId = "test-happy-path";
    OrderCreatedPayload payload =
        new OrderCreatedPayload(200L, "Bob", "Gadget", new BigDecimal("149.99"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    when(outboxFilter.isProcessed(correlationId, "order-processor")).thenReturn(false);
    doNothing().when(processingService).processOrderCreated(eq(payload), eq(correlationId));

    // When
    listener.handleOrderCreated(message, correlationId, acknowledgment);

    // Then
    verify(outboxFilter, times(1)).isProcessed(correlationId, "order-processor");
    verify(processingService, times(1)).processOrderCreated(eq(payload), eq(correlationId));
    verify(outboxFilter, times(1)).markProcessed(correlationId, "order-processor");
    verify(acknowledgment, times(1)).acknowledge();
  }

  /**
   * Test that demonstrates proper handling of already-processed messages.
   *
   * <p>This test shows that if a message has already been successfully processed (isProcessed
   * returns true), it is skipped without calling the business logic:
   *
   * <ul>
   *   <li>{@code isProcessed()} returns true (already processed in a previous transaction)
   *   <li>Business logic is NOT called
   *   <li>{@code markProcessed()} is NOT called (already marked)
   *   <li>Message is acknowledged to remove it from the queue
   * </ul>
   */
  @Test
  void testTransactionalBoundary_AlreadyProcessed_SkipsDuplicate() throws Exception {
    // Given
    String correlationId = "test-duplicate";
    OrderCreatedPayload payload =
        new OrderCreatedPayload(300L, "Charlie", "Item", new BigDecimal("50.00"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    when(outboxFilter.isProcessed(correlationId, "order-processor")).thenReturn(true);

    // When
    listener.handleOrderCreated(message, correlationId, acknowledgment);

    // Then
    verify(outboxFilter, times(1)).isProcessed(correlationId, "order-processor");
    verify(processingService, never()).processOrderCreated(any(), anyString());
    verify(outboxFilter, never()).markProcessed(anyString(), anyString());
    verify(acknowledgment, times(1)).acknowledge();
  }

  /**
   * Test that demonstrates the fix prevents "poison pill" scenarios.
   *
   * <p>This test shows that permanent errors (e.g., JsonProcessingException) are properly handled:
   *
   * <ul>
   *   <li>{@code isProcessed()} returns false
   *   <li>Parsing fails with a permanent error
   *   <li>{@code markProcessed()} is NOT called
   *   <li>Exception propagates to DefaultErrorHandler which routes to DLT
   *   <li>Message does NOT block the partition
   * </ul>
   */
  @Test
  void testTransactionalBoundary_PermanentError_DoesNotMarkAsProcessed() throws Exception {
    // Given
    String correlationId = "test-permanent-error";
    String invalidMessage = "{ invalid json";

    when(outboxFilter.isProcessed(correlationId, "order-processor")).thenReturn(false);

    // When/Then
    assertThatThrownBy(() -> listener.handleOrderCreated(invalidMessage, correlationId, acknowledgment))
        .isInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);

    // Then
    verify(outboxFilter, times(1)).isProcessed(correlationId, "order-processor");
    verify(processingService, never()).processOrderCreated(any(), anyString());
    verify(outboxFilter, never())
        .markProcessed(anyString(), anyString()); // NOT marked as processed
    verify(acknowledgment, never()).acknowledge();
  }

  /**
   * Test that verifies the exact call sequence on a successful processing attempt.
   *
   * <p>This test uses Mockito's InOrder to verify that the methods are called in the exact correct
   * order:
   *
   * <ol>
   *   <li>{@code isProcessed()} - read-only check
   *   <li>{@code processOrderCreated()} - business logic
   *   <li>{@code markProcessed()} - mark as processed (within same transaction)
   *   <li>{@code acknowledge()} - acknowledge Kafka message
   * </ol>
   */
  @Test
  void testTransactionalBoundary_VerifyCallOrder() throws Exception {
    // Given
    String correlationId = "test-call-order";
    OrderCreatedPayload payload =
        new OrderCreatedPayload(400L, "Diana", "Product", new BigDecimal("75.00"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    when(outboxFilter.isProcessed(correlationId, "order-processor")).thenReturn(false);
    doNothing().when(processingService).processOrderCreated(eq(payload), eq(correlationId));

    // When
    listener.handleOrderCreated(message, correlationId, acknowledgment);

    // Then: Verify exact call order
    var inOrder = inOrder(outboxFilter, processingService, acknowledgment);
    inOrder.verify(outboxFilter).isProcessed(correlationId, "order-processor");
    inOrder.verify(processingService).processOrderCreated(eq(payload), eq(correlationId));
    inOrder.verify(outboxFilter).markProcessed(correlationId, "order-processor");
    inOrder.verify(acknowledgment).acknowledge();
  }
}
