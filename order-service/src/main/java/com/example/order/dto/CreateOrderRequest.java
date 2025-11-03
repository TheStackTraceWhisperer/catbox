package com.example.order.dto;

import java.math.BigDecimal;

// Using a record for a simple, immutable DTO.
// Only fields a user is allowed to provide are included.
public record CreateOrderRequest(String customerName, String productName, BigDecimal amount) {
  // Add validation here in a real application
  // (e.g., @NotBlank, @NotNull, @Positive)
}
