package com.example.order.service;

import com.example.catbox.client.OutboxClient;
import com.example.order.OrderServiceApplication;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * Tests the transactional rollback behavior when outbox event creation fails.
 * This verifies the core guarantee of the transactional outbox pattern:
 * if event creation fails, the business transaction must also fail.
 */
@SpringBootTest(classes = OrderServiceApplication.class)
@Testcontainers
class OrderServiceFailureTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withPassword("YourStrong@Passw0rd");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
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

    @MockitoBean
    private OutboxClient outboxClient;

    /**
     * Gap 2: Tests that order creation fails when outbox write fails.
     * Verifies transactional rollback - both order and event should not be saved.
     */
    @Test
    void testOrderCreationFailsWhenOutboxWriteFails() {
        // Arrange: Configure mock to throw on the new 'write' method
        doThrow(new RuntimeException("Simulated serialization failure"))
                .when(outboxClient).write(
                    anyString(), 
                    anyString(), 
                    anyString(), 
                    any(Object.class)
                );

        long initialCount = orderRepository.count();
        CreateOrderRequest request = new CreateOrderRequest("Alice Smith", "Laptop", new BigDecimal("1299.99"));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated serialization failure");

        // Assert
        long finalCount = orderRepository.count();
        assertThat(finalCount).isEqualTo(initialCount);
    }
}
