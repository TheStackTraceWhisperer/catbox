package com.example.routebox.server.controller;

import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.catbox.server.RouteBoxServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OutboxController to verify REST API endpoints.
 */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class OutboxControllerTest {

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
    private OutboxEventRepository outboxEventRepository;

    @BeforeEach
    void setup() {
        outboxEventRepository.deleteAll();
    }

    @Test
    void getAllOutboxEvents_ShouldReturnAllEvents() throws Exception {
        // Given
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
        outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));

        // When & Then
        mockMvc.perform(get("/api/outbox-events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].eventType", hasItems("OrderCreated", "OrderStatusChanged")));
    }

    @Test
    void getPendingOutboxEvents_ShouldReturnOnlyPending() throws Exception {
        // Given
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
        OutboxEvent sentEvent = new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}");
        sentEvent.setSentAt(LocalDateTime.now());
        outboxEventRepository.save(sentEvent);

        // When & Then
        mockMvc.perform(get("/api/outbox-events/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].eventType", hasItem("OrderCreated")));
    }

    @Test
    void searchOutbox_WithFilters_ShouldReturnFilteredResults() throws Exception {
        // Given
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
        outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
        outboxEventRepository.save(new OutboxEvent("Inventory", "I1", "InventoryAdjusted", "{}"));

        // When & Then
        mockMvc.perform(get("/api/outbox-events/search")
                        .param("eventType", "OrderCreated")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].eventType").value("OrderCreated"));
    }

    @Test
    void markOutboxUnsent_ShouldMarkEventAsUnsent() throws Exception {
        // Given
        OutboxEvent event = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
        event.setSentAt(LocalDateTime.now());
        event.setInProgressUntil(LocalDateTime.now());
        OutboxEvent savedEvent = outboxEventRepository.save(event);

        // When & Then
        mockMvc.perform(post("/api/outbox-events/" + savedEvent.getId() + "/mark-unsent"))
                .andExpect(status().isNoContent());

        // Verify event is marked as unsent
        OutboxEvent reloadedEvent = outboxEventRepository.findById(savedEvent.getId()).orElseThrow();
        assertThat(reloadedEvent.getSentAt()).isNull();
        assertThat(reloadedEvent.getInProgressUntil()).isNull();
    }
}
