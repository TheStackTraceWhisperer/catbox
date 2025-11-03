package com.example.order.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for Order entity to verify field access and lifecycle methods.
 */
class OrderTest {

    @Test
    void onCreate_shouldSetDefaultStatus() {
        // Given
        Order order = new Order("John Doe", "Laptop", new BigDecimal("999.99"));

        // When
        order.onCreate();

        // Then
        assertThat(order.getStatus()).isEqualTo("PENDING");
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void onCreate_shouldNotOverrideExistingStatus() {
        // Given
        Order order = new Order("Jane Smith", "Mouse", new BigDecimal("29.99"));
        order.setStatus("COMPLETED");

        // When
        order.onCreate();

        // Then
        assertThat(order.getStatus()).isEqualTo("COMPLETED");
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldCreateOrderWithConstructor() {
        // When
        Order order = new Order("Alice", "Keyboard", new BigDecimal("49.99"));

        // Then
        assertThat(order.getCustomerName()).isEqualTo("Alice");
        assertThat(order.getProductName()).isEqualTo("Keyboard");
        assertThat(order.getAmount()).isEqualByComparingTo(new BigDecimal("49.99"));
        assertThat(order.getStatus()).isNull(); // Status set by @PrePersist
    }

    @Test
    void shouldSupportSettersAndGetters() {
        // Given
        Order order = new Order();

        // When
        order.setCustomerName("Bob");
        order.setProductName("Monitor");
        order.setAmount(new BigDecimal("299.99"));
        order.setStatus("PENDING");

        // Then
        assertThat(order.getCustomerName()).isEqualTo("Bob");
        assertThat(order.getProductName()).isEqualTo("Monitor");
        assertThat(order.getAmount()).isEqualByComparingTo(new BigDecimal("299.99"));
        assertThat(order.getStatus()).isEqualTo("PENDING");
    }
}
