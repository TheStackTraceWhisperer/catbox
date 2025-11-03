package com.example.orderprocessor.model;

/**
 * Payload for OrderStatusChanged events received from Kafka.
 */
public record OrderStatusChangedPayload(
    Long orderId,
    String oldStatus,
    String newStatus
) {}
