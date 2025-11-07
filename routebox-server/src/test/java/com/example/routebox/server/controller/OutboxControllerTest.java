package com.example.routebox.server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import com.example.routebox.test.listener.SharedTestcontainers;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Integration tests for OutboxController to verify REST API endpoints. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class OutboxControllerTest {

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private OutboxEventRepository outboxEventRepository;

  @BeforeEach
  void setup() {
    outboxEventRepository.deleteAll();
  }

  @Test
  void getAllOutboxEvents_ShouldReturnAllEvents() throws Exception {
    // Given
    outboxEventRepository.save(
        new OutboxEvent("Order", UUID.randomUUID().toString(), "OrderCreated", "{}"));
    outboxEventRepository.save(
        new OutboxEvent("Order", UUID.randomUUID().toString(), "OrderStatusChanged", "{}"));

    // When & Then
    mockMvc
        .perform(get("/api/outbox-events"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
        .andExpect(jsonPath("$[*].eventType", hasItems("OrderCreated", "OrderStatusChanged")));
  }

  @Test
  void getPendingOutboxEvents_ShouldReturnOnlyPending() throws Exception {
    // Given
    outboxEventRepository.save(
        new OutboxEvent("Order", UUID.randomUUID().toString(), "OrderCreated", "{}"));
    OutboxEvent sentEvent =
        new OutboxEvent("Order", UUID.randomUUID().toString(), "OrderStatusChanged", "{}");
    sentEvent.setSentAt(LocalDateTime.now());
    outboxEventRepository.save(sentEvent);

    // When & Then
    mockMvc
        .perform(get("/api/outbox-events/pending"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$[*].eventType", hasItem("OrderCreated")));
  }

  @Test
  void searchOutbox_WithFilters_ShouldReturnFilteredResults() throws Exception {
    // Given
    outboxEventRepository.save(
        new OutboxEvent("Order", UUID.randomUUID().toString(), "OrderCreated", "{}"));
    outboxEventRepository.save(
        new OutboxEvent("Order", UUID.randomUUID().toString(), "OrderStatusChanged", "{}"));
    outboxEventRepository.save(
        new OutboxEvent("Inventory", UUID.randomUUID().toString(), "InventoryAdjusted", "{}"));

    // When & Then
    mockMvc
        .perform(
            get("/api/outbox-events/search")
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
    mockMvc
        .perform(post("/api/outbox-events/" + savedEvent.getId() + "/mark-unsent"))
        .andExpect(status().isNoContent());

    // Verify event is marked as unsent
    OutboxEvent reloadedEvent = outboxEventRepository.findById(savedEvent.getId()).orElseThrow();
    assertThat(reloadedEvent.getSentAt()).isNull();
    assertThat(reloadedEvent.getInProgressUntil()).isNull();
  }
}
