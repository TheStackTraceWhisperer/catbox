package com.example.order.service;

import com.example.catbox.client.CatboxClientAutoConfiguration;
import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.order.OrderServiceApplication;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {OrderServiceApplication.class, CatboxClientAutoConfiguration.class})
@Transactional
@Testcontainers
class OrderServiceTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense();

    @DynamicPropertySource
    static void sqlProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
        registry.add("spring.datasource.username", mssql::getUsername);
        registry.add("spring.datasource.password", mssql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Test
    void testCreateOrder_CreatesOrderAndOutboxEvent() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("John Doe", "Laptop", new BigDecimal("999.99"));

        // When
        Order createdOrder = orderService.createOrder(request);

        // Then
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getCustomerName()).isEqualTo("John Doe");
        assertThat(createdOrder.getProductName()).isEqualTo("Laptop");
        assertThat(createdOrder.getAmount()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(createdOrder.getStatus()).isEqualTo("PENDING");
        assertThat(createdOrder.getCreatedAt()).isNotNull();

        // Verify an outbox event for THIS order was created (robust to pre-existing events)
        List<OutboxEvent> outboxEvents = outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
        assertThat(outboxEvents).isNotEmpty();

        String expectedAggregateId = createdOrder.getId().toString();
        OutboxEvent event = outboxEvents.stream()
                .filter(e -> "OrderCreated".equals(e.getEventType()))
                .filter(e -> expectedAggregateId.equals(e.getAggregateId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("OrderCreated event for aggregateId=" + expectedAggregateId + " not found"));

        assertThat(event.getAggregateType()).isEqualTo("Order");
        assertThat(event.getAggregateId()).isEqualTo(expectedAggregateId);
        assertThat(event.getEventType()).isEqualTo("OrderCreated");
        assertThat(event.getSentAt()).isNull();
        assertThat(event.getPayload()).contains("John Doe");
    }

    @Test
    void testUpdateOrderStatus_CreatesOutboxEvent() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("Jane Smith", "Mouse", new BigDecimal("29.99"));
        Order createdOrder = orderService.createOrder(request);

        // When
        Order updatedOrder = orderService.updateOrderStatus(createdOrder.getId(), "COMPLETED");

        // Then
        assertThat(updatedOrder.getStatus()).isEqualTo("COMPLETED");

        // Verify outbox events contain a status change for THIS order (robust to pre-existing events)
        List<OutboxEvent> outboxEvents = outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
        assertThat(outboxEvents).isNotEmpty();

        String expectedAggregateId = createdOrder.getId().toString();
        OutboxEvent statusChangeEvent = outboxEvents.stream()
                .filter(e -> "OrderStatusChanged".equals(e.getEventType()))
                .filter(e -> expectedAggregateId.equals(e.getAggregateId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("OrderStatusChanged event for aggregateId=" + expectedAggregateId + " not found"));

        assertThat(statusChangeEvent.getAggregateId()).isEqualTo(expectedAggregateId);
        assertThat(statusChangeEvent.getPayload()).contains("COMPLETED");
    }

    @Test
    void testGetAllOrders() {
        // Given
        orderService.createOrder(new CreateOrderRequest("Alice", "Keyboard", new BigDecimal("49.99")));
        orderService.createOrder(new CreateOrderRequest("Bob", "Monitor", new BigDecimal("299.99")));

        // When
        List<Order> orders = orderService.getAllOrders();

        // Then
        assertThat(orders).hasSizeGreaterThanOrEqualTo(2);
    }
}
