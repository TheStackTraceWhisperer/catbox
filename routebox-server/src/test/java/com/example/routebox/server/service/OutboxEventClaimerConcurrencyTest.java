package com.example.routebox.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.common.util.TimeBasedUuidGenerator;
import com.example.routebox.server.RouteBoxServerApplication;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Tests the concurrency safety of the OutboxEventClaimer. Verifies that multiple threads/nodes
 * claiming events simultaneously do not process the same event twice (using SKIP LOCKED/READPAST).
 */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class OutboxEventClaimerConcurrencyTest {
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

  @Autowired private OutboxEventClaimer claimer;

  @Autowired private OutboxEventRepository outboxEventRepository;

  @BeforeEach
  void setUp() {
    // Clean outbox event data from previous test runs in the same JVM session
    outboxEventRepository.deleteAll();
    outboxEventRepository.flush();
  }

  /**
   * Gap 3: Tests that concurrent claimers do not process the same events. Creates 100 events and
   * has two threads claim them simultaneously. Verifies no overlap between the claimed event sets.
   */
  @Test
  void testConcurrentClaimersDoNotProcessSameEvents() throws Exception {
    // Arrange: Create 100 pending events with unique IDs
    Set<Long> createdEventIds = new HashSet<>();
    for (int i = 1; i <= 100; i++) {
      String orderId = "order-" + TimeBasedUuidGenerator.generate().toString();
      OutboxEvent event =
          new OutboxEvent("Order", orderId, "OrderCreated", "{\"orderId\":\"" + orderId + "\"}");
      OutboxEvent saved = outboxEventRepository.save(event);
      createdEventIds.add(saved.getId());
    }

    // Ensure all events are committed to the database
    outboxEventRepository.flush();

    // Act: Use two threads to claim events simultaneously
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(2);

    CompletableFuture<List<OutboxEvent>> future1 =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                startLatch.await(); // Wait for signal to start
                List<OutboxEvent> claimed = claimer.claimEvents();
                doneLatch.countDown();
                return claimed;
              } catch (Exception e) {
                doneLatch.countDown();
                throw new RuntimeException(e);
              }
            });

    CompletableFuture<List<OutboxEvent>> future2 =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                startLatch.await(); // Wait for signal to start
                List<OutboxEvent> claimed = claimer.claimEvents();
                doneLatch.countDown();
                return claimed;
              } catch (Exception e) {
                doneLatch.countDown();
                throw new RuntimeException(e);
              }
            });

    // Start both threads simultaneously
    startLatch.countDown();

    // Wait for both to complete
    doneLatch.await();

    List<OutboxEvent> claimedByThread1 = future1.get();
    List<OutboxEvent> claimedByThread2 = future2.get();

    // Filter to only include events created by this test
    List<OutboxEvent> filteredThread1 = new ArrayList<>();
    for (OutboxEvent event : claimedByThread1) {
      if (createdEventIds.contains(event.getId())) {
        filteredThread1.add(event);
      }
    }

    List<OutboxEvent> filteredThread2 = new ArrayList<>();
    for (OutboxEvent event : claimedByThread2) {
      if (createdEventIds.contains(event.getId())) {
        filteredThread2.add(event);
      }
    }

    // Assert: Verify total count of our events is at least some were claimed
    // (May be less than 100 if other tests have leftover events that fill the batch)
    int totalClaimed = filteredThread1.size() + filteredThread2.size();
    assertThat(totalClaimed).isGreaterThan(0).as("At least some events should be claimed");

    // Assert: The main goal - verify no intersection (no event claimed by both threads)
    Set<Long> idsFromThread1 = new HashSet<>();
    for (OutboxEvent event : filteredThread1) {
      idsFromThread1.add(event.getId());
    }

    Set<Long> idsFromThread2 = new HashSet<>();
    for (OutboxEvent event : filteredThread2) {
      idsFromThread2.add(event.getId());
    }

    // Find intersection
    Set<Long> intersection = new HashSet<>(idsFromThread1);
    intersection.retainAll(idsFromThread2);

    assertThat(intersection)
        .as("No event should be claimed by both threads (SKIP LOCKED should prevent this)")
        .isEmpty();
  }
}
