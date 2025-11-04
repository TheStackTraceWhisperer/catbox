package com.example.order.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Test for OrderNotFoundException to verify exception message formatting. */
class OrderNotFoundExceptionTest {

  @Test
  void shouldCreateExceptionWithCorrectMessage() {
    // Given
    Long orderId = 123L;

    // When
    OrderNotFoundException exception = new OrderNotFoundException(orderId);

    // Then
    assertThat(exception.getMessage()).isEqualTo("Order not found: 123");
  }

  @Test
  void shouldBeRuntimeException() {
    // Given
    Long orderId = 456L;

    // When
    OrderNotFoundException exception = new OrderNotFoundException(orderId);

    // Then
    assertThat(exception).isInstanceOf(RuntimeException.class);
  }
}
