package com.example.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import com.example.order.OrderServiceApplication;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.repository.OrderRepository;
import com.example.routebox.client.OutboxClient;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.example.routebox.test.listener.SharedTestcontainers;

/**
 * Tests the transactional rollback behavior when outbox event creation fails. This verifies the
 * core guarantee of the transactional outbox pattern: if event creation fails, the business
 * transaction must also fail.
 */
@SpringBootTest(classes = OrderServiceApplication.class)
@Testcontainers
class OrderServiceFailureTest {

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @Autowired private OrderService orderService;

  @Autowired private OrderRepository orderRepository;

  @MockitoBean private OutboxClient outboxClient;

  /**
   * Gap 2: Tests that order creation fails when outbox write fails. Verifies transactional rollback
   * - both order and event should not be saved.
   */
  @Test
  void testOrderCreationFailsWhenOutboxWriteFails() {
    // Arrange: Configure mock to throw on both 'write' method signatures
    doThrow(new RuntimeException("Simulated serialization failure"))
        .when(outboxClient)
        .write(anyString(), anyString(), anyString(), any(Object.class));

    doThrow(new RuntimeException("Simulated serialization failure"))
        .when(outboxClient)
        .write(anyString(), anyString(), anyString(), anyString(), any(Object.class));

    long initialCount = orderRepository.count();
    CreateOrderRequest request =
        new CreateOrderRequest("Alice Smith", "Laptop", new BigDecimal("1299.99"));

    // Act & Assert
    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Simulated serialization failure");

    // Assert
    long finalCount = orderRepository.count();
    assertThat(finalCount).isEqualTo(initialCount);
  }
}
