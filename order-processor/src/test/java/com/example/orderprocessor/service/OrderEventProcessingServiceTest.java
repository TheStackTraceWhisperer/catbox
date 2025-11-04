package com.example.orderprocessor.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.orderprocessor.model.OrderCreatedPayload;
import com.example.orderprocessor.model.OrderStatusChangedPayload;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for OrderEventProcessingService. */
class OrderEventProcessingServiceTest {

  private OrderEventProcessingService service;

  @BeforeEach
  void setUp() {
    service = new OrderEventProcessingService();
    service.resetCounters();
  }

  @Test
  void testProcessOrderCreated_Success() {
    // Given
    OrderCreatedPayload payload =
        new OrderCreatedPayload(1L, "Alice", "Widget", new BigDecimal("99.99"), "PENDING");
    String correlationId = "test-corr-1";

    // When - try multiple times to account for random failures
    int maxAttempts = 20;
    int successCount = 0;

    for (int i = 0; i < maxAttempts; i++) {
      service.resetCounters();
      try {
        service.processOrderCreated(payload, correlationId);
        successCount++;
      } catch (OrderEventProcessingService.ProcessingException e) {
        // Expected intermittent failure
      }
    }

    // Then - should have at least some successes (not all failures)
    assertThat(successCount).as("Should have at least some successful processing").isGreaterThan(0);
  }

  @Test
  void testProcessOrderStatusChanged_Success() {
    // Given
    OrderStatusChangedPayload payload = new OrderStatusChangedPayload(1L, "PENDING", "SHIPPED");
    String correlationId = "test-corr-2";

    // When - try multiple times to account for random failures
    int maxAttempts = 20;
    int successCount = 0;

    for (int i = 0; i < maxAttempts; i++) {
      service.resetCounters();
      try {
        service.processOrderStatusChanged(payload, correlationId);
        successCount++;
      } catch (OrderEventProcessingService.ProcessingException e) {
        // Expected intermittent failure
      }
    }

    // Then - should have at least some successes (not all failures)
    assertThat(successCount).as("Should have at least some successful processing").isGreaterThan(0);
  }

  @Test
  void testCounters_TrackProcessedEvents() {
    // Given
    OrderCreatedPayload payload1 =
        new OrderCreatedPayload(1L, "Alice", "Widget", new BigDecimal("99.99"), "PENDING");
    OrderStatusChangedPayload payload2 = new OrderStatusChangedPayload(1L, "PENDING", "SHIPPED");

    // When - process multiple times until we get some successes
    int attempts = 0;
    int maxAttempts = 100;

    while (service.getProcessedOrderCreatedCount() < 2 && attempts < maxAttempts) {
      try {
        service.processOrderCreated(payload1, "corr-" + attempts);
      } catch (OrderEventProcessingService.ProcessingException e) {
        // Retry on intermittent failure
      }
      attempts++;
    }

    attempts = 0;
    while (service.getProcessedOrderStatusChangedCount() < 2 && attempts < maxAttempts) {
      try {
        service.processOrderStatusChanged(payload2, "corr-" + attempts);
      } catch (OrderEventProcessingService.ProcessingException e) {
        // Retry on intermittent failure
      }
      attempts++;
    }

    // Then
    assertThat(service.getProcessedOrderCreatedCount())
        .as("Should have processed at least 2 OrderCreated events")
        .isGreaterThanOrEqualTo(2);
    assertThat(service.getProcessedOrderStatusChangedCount())
        .as("Should have processed at least 2 OrderStatusChanged events")
        .isGreaterThanOrEqualTo(2);
  }

  @Test
  void testResetCounters() {
    // Given - some processing has occurred
    OrderCreatedPayload payload =
        new OrderCreatedPayload(1L, "Alice", "Widget", new BigDecimal("99.99"), "PENDING");

    // Process until we get at least one success
    int attempts = 0;
    while (service.getProcessedOrderCreatedCount() == 0 && attempts < 50) {
      try {
        service.processOrderCreated(payload, "corr-" + attempts);
      } catch (OrderEventProcessingService.ProcessingException e) {
        // Retry
      }
      attempts++;
    }

    int countBefore = service.getProcessedOrderCreatedCount();
    assertThat(countBefore).isGreaterThan(0);

    // When
    service.resetCounters();

    // Then
    assertThat(service.getProcessedOrderCreatedCount()).isEqualTo(0);
    assertThat(service.getProcessedOrderStatusChangedCount()).isEqualTo(0);
    assertThat(service.getFailureCount()).isEqualTo(0);
  }
}
