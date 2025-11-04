package com.example.catbox.client;

public interface OutboxClient {
  /**
   * Serializes the given payload and writes it to the outbox.
   *
   * @param aggregateType The "type" of the entity (e.g., "Order")
   * @param aggregateId The ID of the entity (e.g., "123")
   * @param eventType The event name (e.g., "OrderCreated")
   * @param payload The POJO/Record/Map to be serialized to JSON
   */
  void write(String aggregateType, String aggregateId, String eventType, Object payload);

  /**
   * Serializes the given payload and writes it to the outbox with a specific correlation ID.
   *
   * @param aggregateType The "type" of the entity (e.g., "Order")
   * @param aggregateId The ID of the entity (e.g., "123")
   * @param eventType The event name (e.g., "OrderCreated")
   * @param correlationId A unique ID for tracing
   * @param payload The POJO/Record/Map to be serialized to JSON
   */
  void write(
      String aggregateType,
      String aggregateId,
      String eventType,
      String correlationId,
      Object payload);
}
