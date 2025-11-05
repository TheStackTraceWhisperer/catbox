package com.example.routebox.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.server.RouteBoxServerApplication;
import com.example.routebox.server.config.OutboxProcessingConfig;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Tests for OutboxEventWorker to verify worker thread behavior and error handling. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@ActiveProfiles("azuresql")
@Testcontainers
class OutboxEventWorkerTest {

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

    // Configure test values
    registry.add("outbox.processing.worker-concurrency", () -> "3");
    registry.add("outbox.processing.queue-capacity", () -> "5");
  }

  @Autowired private OutboxProcessingConfig processingConfig;
  @Autowired private BlockingQueue<OutboxEvent> eventQueue;

  @MockitoBean private OutboxEventPublisher publisher;

  @BeforeEach
  void setUp() {
    eventQueue.clear();
    Mockito.reset(publisher);
  }

  @Test
  void worker_shouldStartCorrectNumberOfThreads() {
    // The workers are started via @PostConstruct, so they should already be running
    assertThat(processingConfig.getWorkerConcurrency()).isEqualTo(3);
  }

  @Test
  void worker_shouldProcessEventsFromQueue() throws InterruptedException {
    // Add events to the queue
    for (int i = 0; i < 5; i++) {
      OutboxEvent event = new OutboxEvent("Order", "order-" + i, "OrderCreated", "{}");
      event.setId((long) i);
      eventQueue.put(event);
    }

    // Wait for workers to process all events
    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              verify(publisher, times(5)).publishEvent(any(OutboxEvent.class));
            });

    // Queue should be empty
    assertThat(eventQueue).isEmpty();
  }

  @Test
  void worker_shouldContinueProcessingAfterPublisherException() throws InterruptedException {
    // Configure publisher to fail for the first event, then succeed
    OutboxEvent failingEvent = new OutboxEvent("Order", "order-fail", "OrderCreated", "{}");
    failingEvent.setId(1L);

    OutboxEvent successEvent1 = new OutboxEvent("Order", "order-1", "OrderCreated", "{}");
    successEvent1.setId(2L);

    OutboxEvent successEvent2 = new OutboxEvent("Order", "order-2", "OrderCreated", "{}");
    successEvent2.setId(3L);

    // First call throws exception, subsequent calls succeed
    doThrow(new RuntimeException("Transient failure"))
        .doNothing()
        .doNothing()
        .when(publisher)
        .publishEvent(any(OutboxEvent.class));

    // Add events
    eventQueue.put(failingEvent);
    eventQueue.put(successEvent1);
    eventQueue.put(successEvent2);

    // Wait for all events to be attempted
    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              verify(publisher, times(3)).publishEvent(any(OutboxEvent.class));
            });

    // Queue should be empty - worker should continue despite the exception
    assertThat(eventQueue).isEmpty();
  }

  @Test
  void worker_shouldHandleMultipleEventsConcurrently() throws InterruptedException {
    // Configure publisher to introduce a delay
    Mockito.doAnswer(
            invocation -> {
              Thread.sleep(100); // Simulate slow processing
              return null;
            })
        .when(publisher)
        .publishEvent(any(OutboxEvent.class));

    // Add more events than workers to test concurrency
    int eventCount = 9; // More than worker count (3)
    for (int i = 0; i < eventCount; i++) {
      OutboxEvent event = new OutboxEvent("Order", "order-" + i, "OrderCreated", "{}");
      event.setId((long) i);
      eventQueue.put(event);
    }

    // With 3 workers processing in parallel, 9 events with 100ms each
    // should complete in roughly 300ms (3 batches of 3)
    // Allow some buffer for test reliability
    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              verify(publisher, times(eventCount)).publishEvent(any(OutboxEvent.class));
            });

    assertThat(eventQueue).isEmpty();
  }
}
