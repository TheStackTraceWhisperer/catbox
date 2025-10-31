package com.example.catbox.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.catbox.entity.Order;
import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OrderRepository;
import com.example.catbox.repository.OutboxEventRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderServiceTest {

  @Autowired private OrderService orderService;

  @Autowired private OrderRepository orderRepository;

  @Autowired private OutboxEventRepository outboxEventRepository;

  @Test
  void testCreateOrder_CreatesOrderAndOutboxEvent() {
    // Given
    Order order = new Order("John Doe", "Laptop", new BigDecimal("999.99"));

    // When
    Order createdOrder = orderService.createOrder(order);

    // Then
    assertThat(createdOrder.getId()).isNotNull();
    assertThat(createdOrder.getCustomerName()).isEqualTo("John Doe");
    assertThat(createdOrder.getProductName()).isEqualTo("Laptop");
    assertThat(createdOrder.getAmount()).isEqualByComparingTo(new BigDecimal("999.99"));
    assertThat(createdOrder.getStatus()).isEqualTo("PENDING");
    assertThat(createdOrder.getCreatedAt()).isNotNull();

    // Verify outbox event was created
    List<OutboxEvent> outboxEvents = outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
    assertThat(outboxEvents).isNotEmpty();

    OutboxEvent event = outboxEvents.get(0);
    assertThat(event.getAggregateType()).isEqualTo("Order");
    assertThat(event.getAggregateId()).isEqualTo(createdOrder.getId().toString());
    assertThat(event.getEventType()).isEqualTo("OrderCreated");
    assertThat(event.getSentAt()).isNull();
    assertThat(event.getPayload()).contains("John Doe");
  }

  @Test
  void testUpdateOrderStatus_CreatesOutboxEvent() {
    // Given
    Order order = new Order("Jane Smith", "Mouse", new BigDecimal("29.99"));
    Order createdOrder = orderService.createOrder(order);

    // When
    Order updatedOrder = orderService.updateOrderStatus(createdOrder.getId(), "COMPLETED");

    // Then
    assertThat(updatedOrder.getStatus()).isEqualTo("COMPLETED");

    // Verify outbox events (one for creation, one for status change)
    List<OutboxEvent> outboxEvents = outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
    assertThat(outboxEvents).hasSizeGreaterThanOrEqualTo(2);

    OutboxEvent statusChangeEvent =
        outboxEvents.stream()
            .filter(e -> "OrderStatusChanged".equals(e.getEventType()))
            .findFirst()
            .orElseThrow();

    assertThat(statusChangeEvent.getAggregateId()).isEqualTo(createdOrder.getId().toString());
    assertThat(statusChangeEvent.getPayload()).contains("COMPLETED");
  }

  @Test
  void testGetAllOrders() {
    // Given
    orderService.createOrder(new Order("Alice", "Keyboard", new BigDecimal("49.99")));
    orderService.createOrder(new Order("Bob", "Monitor", new BigDecimal("299.99")));

    // When
    List<Order> orders = orderService.getAllOrders();

    // Then
    assertThat(orders).hasSizeGreaterThanOrEqualTo(2);
  }
}
