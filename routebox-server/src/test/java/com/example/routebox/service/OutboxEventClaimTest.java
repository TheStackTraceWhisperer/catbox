package com.example.routebox.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = RouteBoxServerApplication.class)
@Transactional
@Testcontainers
class OutboxEventClaimTest {

  @Container
  static final MSSQLServerContainer<?> mssql =
      new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
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
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    registry.add("spring.threads.virtual.enabled", () -> "true");
  }

  @Autowired OutboxEventRepository outboxEventRepository;

  @BeforeEach
  void setup() {
    outboxEventRepository.deleteAll();
  }

  @Test
  void testClaimPendingEvents_claimsEventsSuccessfully() {
    // Given: Create some test events
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
    outboxEventRepository.save(new OutboxEvent("Order", "A3", "OrderCancelled", "{}"));

    // When: Claim events
    LocalDateTime now = LocalDateTime.now();
    List<OutboxEvent> claimed =
        outboxEventRepository.findPendingEventsForClaim(now, PageRequest.of(0, 10));

    // Then: Events should be claimed
    assertThat(claimed).hasSize(3);
    assertThat(claimed)
        .extracting(OutboxEvent::getAggregateId)
        .containsExactlyInAnyOrder("A1", "A2", "A3");
  }

  @Test
  void testClaimPendingEvents_respectsBatchSize() {
    // Given: Create more events than the batch size
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
    outboxEventRepository.save(new OutboxEvent("Order", "A3", "OrderCancelled", "{}"));

    // When: Claim events with batch size of 2
    LocalDateTime now = LocalDateTime.now();
    List<OutboxEvent> claimed =
        outboxEventRepository.findPendingEventsForClaim(now, PageRequest.of(0, 2));

    // Then: Only 2 events should be claimed
    assertThat(claimed).hasSize(2);
  }

  @Test
  void testClaimPendingEvents_skipsAlreadySentEvents() {
    // Given: Create events, one of which is already sent
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    OutboxEvent sentEvent =
        outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
    sentEvent.setSentAt(LocalDateTime.now());
    outboxEventRepository.save(sentEvent);
    outboxEventRepository.save(new OutboxEvent("Order", "A3", "OrderCancelled", "{}"));

    // When: Claim events
    LocalDateTime now = LocalDateTime.now();
    List<OutboxEvent> claimed =
        outboxEventRepository.findPendingEventsForClaim(now, PageRequest.of(0, 10));

    // Then: Only unsent events should be claimed
    assertThat(claimed).hasSize(2);
    assertThat(claimed)
        .extracting(OutboxEvent::getAggregateId)
        .containsExactlyInAnyOrder("A1", "A3");
  }

  @Test
  void testClaimPendingEvents_skipsEventsInProgress() {
    // Given: Create events, one of which is in progress
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    OutboxEvent inProgressEvent =
        outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
    inProgressEvent.setInProgressUntil(LocalDateTime.now().plusMinutes(5));
    outboxEventRepository.save(inProgressEvent);
    outboxEventRepository.save(new OutboxEvent("Order", "A3", "OrderCancelled", "{}"));

    // When: Claim events
    LocalDateTime now = LocalDateTime.now();
    List<OutboxEvent> claimed =
        outboxEventRepository.findPendingEventsForClaim(now, PageRequest.of(0, 10));

    // Then: Only events not in progress should be claimed
    assertThat(claimed).hasSize(2);
    assertThat(claimed)
        .extracting(OutboxEvent::getAggregateId)
        .containsExactlyInAnyOrder("A1", "A3");
  }

  @Test
  void testClaimPendingEvents_claimsExpiredInProgressEvents() {
    // Given: Create events, one of which has expired in-progress lease
    outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    OutboxEvent expiredEvent =
        outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
    expiredEvent.setInProgressUntil(LocalDateTime.now().minusMinutes(5)); // Expired
    outboxEventRepository.save(expiredEvent);

    // When: Claim events
    LocalDateTime now = LocalDateTime.now();
    List<OutboxEvent> claimed =
        outboxEventRepository.findPendingEventsForClaim(now, PageRequest.of(0, 10));

    // Then: Both events should be claimed, including the one with expired lease
    assertThat(claimed).hasSize(2);
    assertThat(claimed)
        .extracting(OutboxEvent::getAggregateId)
        .containsExactlyInAnyOrder("A1", "A2");
  }

  @Test
  void testClaimPendingEvents_ordersEventsByCreatedAt() {
    // Given: Create events with different timestamps
    LocalDateTime baseTime = LocalDateTime.now().minusHours(1);

    OutboxEvent event1 = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    event1.setCreatedAt(baseTime.plusMinutes(1));

    OutboxEvent event2 = new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}");
    event2.setCreatedAt(baseTime);

    OutboxEvent event3 = new OutboxEvent("Order", "A3", "OrderCancelled", "{}");
    event3.setCreatedAt(baseTime.plusMinutes(2));

    // Save in random order to ensure ordering is by created_at, not insertion order
    outboxEventRepository.save(event2);
    outboxEventRepository.save(event1);
    outboxEventRepository.save(event3);

    // When: Claim events
    LocalDateTime now = LocalDateTime.now();
    List<OutboxEvent> claimed =
        outboxEventRepository.findPendingEventsForClaim(now, PageRequest.of(0, 10));

    // Then: Events should be ordered by created_at (event2, then event1, then event3)
    assertThat(claimed).hasSize(3);
    assertThat(claimed.get(0).getAggregateId()).isEqualTo("A2");
    assertThat(claimed.get(1).getAggregateId()).isEqualTo("A1");
    assertThat(claimed.get(2).getAggregateId()).isEqualTo("A3");
  }
}
