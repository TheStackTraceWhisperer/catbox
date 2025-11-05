package com.example.routebox.server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Integration tests for AdminController to verify admin page rendering. */
@ActiveProfiles("azuresql")
@SpringBootTest(classes = RouteBoxServerApplication.class)
@AutoConfigureMockMvc
@Transactional
@Testcontainers
class AdminControllerTest {

  @Container
  static MSSQLServerContainer<?> mssql =
      new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
          .acceptLicense()
          .withReuse(true);

  @DynamicPropertySource
  static void sqlProps(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
    registry.add("spring.datasource.username", mssql::getUsername);
    registry.add("spring.datasource.password", mssql::getPassword);
    registry.add(
        "spring.datasource.driver-class-name",
        () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    registry.add(
        "spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private OutboxEventRepository outboxEventRepository;

  @BeforeEach
  void setup() {
    outboxEventRepository.deleteAll();
  }

  @Test
  void adminPage_ShouldReturnAdminView() throws Exception {
    // Given
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));

    // When & Then
    mockMvc
        .perform(get("/admin"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin"))
        .andExpect(model().attributeExists("events"))
        .andExpect(model().attributeExists("currentPage"))
        .andExpect(model().attributeExists("totalPages"))
        .andExpect(model().attributeExists("totalElements"));
  }

  @Test
  void adminPage_WithPagination_ShouldReturnPagedResults() throws Exception {
    // Given
    for (int i = 0; i < 25; i++) {
      outboxEventRepository.save(new OutboxEvent("Order", "A" + i, "OrderCreated", "{}"));
    }

    // When & Then
    mockMvc
        .perform(get("/admin").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin"))
        .andExpect(model().attribute("currentPage", 0));
  }

  @Test
  void adminPage_WithFilters_ShouldReturnFilteredResults() throws Exception {
    // Given
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
    outboxEventRepository.save(new OutboxEvent("Inventory", "I1", "InventoryAdjusted", "{}"));

    // When & Then
    mockMvc
        .perform(get("/admin").param("eventType", "OrderCreated").param("aggregateType", "Order"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin"))
        .andExpect(model().attributeExists("events"));
  }

  @Test
  void adminPage_WithSorting_ShouldReturnSortedResults() throws Exception {
    // Given
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));

    // When & Then
    mockMvc
        .perform(get("/admin").param("sortBy", "createdAt").param("direction", "DESC"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin"))
        .andExpect(model().attribute("sortBy", "createdAt"))
        .andExpect(model().attribute("direction", "DESC"));
  }

  @Test
  void adminPage_WithPendingOnlyFilter_ShouldReturnOnlyPending() throws Exception {
    // Given
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));

    // When & Then
    mockMvc
        .perform(get("/admin").param("pendingOnly", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin"))
        .andExpect(model().attribute("pendingOnly", true));
  }

  @Test
  void adminPage_WithDefaultParameters_ShouldUseDefaults() throws Exception {
    // When & Then
    mockMvc
        .perform(get("/admin"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin"))
        .andExpect(model().attribute("currentPage", 0))
        .andExpect(model().attribute("sortBy", "createdAt"))
        .andExpect(model().attribute("direction", "DESC"))
        .andExpect(model().attribute("eventType", ""))
        .andExpect(model().attribute("aggregateType", ""))
        .andExpect(model().attribute("aggregateId", ""))
        .andExpect(model().attribute("pendingOnly", false));
  }
}
