package com.example.order.controller;

import com.example.catbox.client.CatboxClientAutoConfiguration;
import com.example.order.OrderServiceApplication;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.UpdateStatusRequest;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController to verify REST API endpoints.
 */
@SpringBootTest(classes = {OrderServiceApplication.class, CatboxClientAutoConfiguration.class})
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class OrderControllerTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withReuse(true);

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        // Given
        CreateOrderRequest request = new CreateOrderRequest("John Doe", "Laptop", new BigDecimal("999.99"));

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.amount").value(999.99))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() throws Exception {
        // Given
        Order order1 = new Order("Alice", "Keyboard", new BigDecimal("49.99"));
        Order order2 = new Order("Bob", "Monitor", new BigDecimal("299.99"));
        orderRepository.save(order1);
        orderRepository.save(order2);

        // When & Then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].customerName", hasItem("Alice")))
                .andExpect(jsonPath("$[*].customerName", hasItem("Bob")));
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        // Given
        Order order = new Order("Charlie", "Mouse", new BigDecimal("19.99"));
        Order savedOrder = orderRepository.save(order);

        // When & Then
        mockMvc.perform(get("/api/orders/" + savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.customerName").value("Charlie"))
                .andExpect(jsonPath("$.productName").value("Mouse"))
                .andExpect(jsonPath("$.amount").value(19.99));
    }

    @Test
    void updateOrderStatus_ShouldUpdateAndReturnOrder() throws Exception {
        // Given
        Order order = new Order("Diana", "Headphones", new BigDecimal("79.99"));
        Order savedOrder = orderRepository.save(order);
        UpdateStatusRequest updateRequest = new UpdateStatusRequest("COMPLETED");

        // When & Then
        mockMvc.perform(patch("/api/orders/" + savedOrder.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updateOrderStatus_WithNullStatus_ShouldReturnBadRequest() throws Exception {
        // Given
        Order order = new Order("Eve", "Tablet", new BigDecimal("399.99"));
        Order savedOrder = orderRepository.save(order);
        UpdateStatusRequest updateRequest = new UpdateStatusRequest(null);

        // When & Then
        mockMvc.perform(patch("/api/orders/" + savedOrder.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_WithBlankStatus_ShouldReturnBadRequest() throws Exception {
        // Given
        Order order = new Order("Frank", "Camera", new BigDecimal("599.99"));
        Order savedOrder = orderRepository.save(order);
        UpdateStatusRequest updateRequest = new UpdateStatusRequest("  ");

        // When & Then
        mockMvc.perform(patch("/api/orders/" + savedOrder.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}
