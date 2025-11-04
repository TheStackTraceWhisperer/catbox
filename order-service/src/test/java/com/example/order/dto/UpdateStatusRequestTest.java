package com.example.order.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Test for UpdateStatusRequest DTO to verify record functionality. */
class UpdateStatusRequestTest {

  @Test
  void shouldCreateRequestWithStatus() {
    // Given
    String status = "COMPLETED";

    // When
    UpdateStatusRequest request = new UpdateStatusRequest(status);

    // Then
    assertThat(request.status()).isEqualTo("COMPLETED");
  }

  @Test
  void shouldHandleNullStatus() {
    // When
    UpdateStatusRequest request = new UpdateStatusRequest(null);

    // Then
    assertThat(request.status()).isNull();
  }

  @Test
  void shouldSupportEquality() {
    // Given
    UpdateStatusRequest request1 = new UpdateStatusRequest("PENDING");
    UpdateStatusRequest request2 = new UpdateStatusRequest("PENDING");
    UpdateStatusRequest request3 = new UpdateStatusRequest("COMPLETED");

    // Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request1).isNotEqualTo(request3);
  }
}
