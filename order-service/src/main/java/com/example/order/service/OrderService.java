package com.example.order.service;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.entity.Order;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderRepository;
import com.example.routebox.client.OutboxClient;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final OutboxClient outboxClient;
  private final Tracer tracer;

  // Time-based UUID generator (UUIDv7) for correlation IDs
  private static final TimeBasedEpochGenerator UUID_GENERATOR =
      Generators.timeBasedEpochGenerator();

  // Payload record definitions for outbox events
  private record OrderCreatedPayload(
      Long orderId, String customerName, String productName, BigDecimal amount, String status) {}

  private record OrderStatusChangedPayload(Long orderId, String oldStatus, String newStatus) {}

  @Observed(name = "order.create", contextualName = "create-order")
  @Transactional
  public Order createOrder(CreateOrderRequest request) {
    // Get or create correlation ID from trace context
    String correlationId = getOrCreateCorrelationId();

    // Mapping from DTO to Entity happens here
    Order order = new Order(request.customerName(), request.productName(), request.amount());

    Order savedOrder = orderRepository.save(order);

    // 1. Create a type-safe payload object (no more HashMap)
    var payload =
        new OrderCreatedPayload(
            savedOrder.getId(),
            savedOrder.getCustomerName(),
            savedOrder.getProductName(),
            savedOrder.getAmount(),
            savedOrder.getStatus());

    // 2. Pass metadata, correlationId, and the payload *object* to the client
    outboxClient.write(
        "Order", savedOrder.getId().toString(), "OrderCreated", correlationId, payload);

    // Add order ID to span
    if (tracer.currentSpan() != null) {
      tracer.currentSpan().tag("order.id", savedOrder.getId().toString());
      tracer.currentSpan().tag("correlation.id", correlationId);
    }

    return savedOrder;
  }

  @Observed(name = "order.update-status", contextualName = "update-order-status")
  @Transactional
  public Order updateOrderStatus(Long orderId, String newStatus) {
    // Get or create correlation ID from trace context
    String correlationId = getOrCreateCorrelationId();

    Order order =
        orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    String oldStatus = order.getStatus();
    order.setStatus(newStatus);
    Order updatedOrder = orderRepository.save(order);

    // 1. Create a type-safe payload object
    var payload = new OrderStatusChangedPayload(updatedOrder.getId(), oldStatus, newStatus);

    // 2. Pass metadata, correlationId, and the payload *object* to the client
    outboxClient.write(
        "Order", updatedOrder.getId().toString(), "OrderStatusChanged", correlationId, payload);

    // Add order ID to span
    if (tracer.currentSpan() != null) {
      tracer.currentSpan().tag("order.id", updatedOrder.getId().toString());
      tracer.currentSpan().tag("correlation.id", correlationId);
      tracer.currentSpan().tag("old.status", oldStatus);
      tracer.currentSpan().tag("new.status", newStatus);
    }

    return updatedOrder;
  }

  public List<Order> getAllOrders() {
    return orderRepository.findAll();
  }

  public Order getOrderById(Long id) {
    return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
  }

  /**
   * Gets the correlation ID from the current trace context, or creates a new one if not present.
   */
  private String getOrCreateCorrelationId() {
    if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
      // Use trace ID as correlation ID for distributed tracing
      return tracer.currentSpan().context().traceId();
    }
    // Fallback: create a new time-based UUID (UUIDv7) if no trace context exists
    return UUID_GENERATOR.generate().toString();
  }
}
