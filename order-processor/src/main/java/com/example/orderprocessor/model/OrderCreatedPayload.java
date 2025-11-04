package com.example.orderprocessor.model;

import java.math.BigDecimal;

/** Payload for OrderCreated events received from Kafka. */
public record OrderCreatedPayload(
    Long orderId, String customerName, String productName, BigDecimal amount, String status) {}
