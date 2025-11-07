package com.example.routebox.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import com.example.routebox.server.config.OutboxProcessingConfig;
import com.example.routebox.server.entity.OutboxArchiveEvent;
import com.example.routebox.server.repository.OutboxArchiveEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.example.routebox.test.listener.SharedTestcontainers;

/** Tests for OutboxArchivalService. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class OutboxArchivalServiceTest {

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @Autowired OutboxEventRepository outboxEventRepository;

  @Autowired OutboxArchiveEventRepository archiveEventRepository;

  @Autowired OutboxArchivalService archivalService;

  @Autowired OutboxProcessingConfig processingConfig;

  @BeforeEach
  void setup() {
    archiveEventRepository.deleteAll();
    outboxEventRepository.deleteAll();
  }

  @Test
  void archiveOldEvents_movesOldSentEventsToArchive() {
    // Given - Create some old sent events
    OutboxEvent oldEvent1 = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    oldEvent1.setSentAt(LocalDateTime.now().minusDays(10));
    oldEvent1 = outboxEventRepository.save(oldEvent1);

    OutboxEvent oldEvent2 = new OutboxEvent("Order", "A2", "OrderCreated", "corr-123", "{}");
    oldEvent2.setSentAt(LocalDateTime.now().minusDays(8));
    oldEvent2 = outboxEventRepository.save(oldEvent2);

    // Create a recent sent event (should not be archived)
    OutboxEvent recentEvent = new OutboxEvent("Order", "A3", "OrderCreated", "{}");
    recentEvent.setSentAt(LocalDateTime.now().minusDays(3));
    recentEvent = outboxEventRepository.save(recentEvent);

    // Create a pending event (should not be archived)
    OutboxEvent pendingEvent = new OutboxEvent("Order", "A4", "OrderCreated", "{}");
    outboxEventRepository.save(pendingEvent);

    // When
    archivalService.archiveOldEvents();

    // Then
    List<OutboxEvent> remainingEvents = outboxEventRepository.findAll();
    assertThat(remainingEvents).hasSize(2); // recent and pending
    assertThat(remainingEvents)
        .extracting(OutboxEvent::getAggregateId)
        .containsExactlyInAnyOrder("A3", "A4");

    List<OutboxArchiveEvent> archivedEvents = archiveEventRepository.findAll();
    assertThat(archivedEvents).hasSize(2);

    // Verify archived events have correct data
    OutboxArchiveEvent archived1 =
        archivedEvents.stream()
            .filter(e -> e.getAggregateId().equals("A1"))
            .findFirst()
            .orElseThrow();
    assertThat(archived1.getOriginalEventId()).isEqualTo(oldEvent1.getId());
    assertThat(archived1.getEventType()).isEqualTo("OrderCreated");
    assertThat(archived1.getArchivedAt()).isNotNull();
    assertThat(archived1.getSentAt()).isNotNull();

    OutboxArchiveEvent archived2 =
        archivedEvents.stream()
            .filter(e -> e.getAggregateId().equals("A2"))
            .findFirst()
            .orElseThrow();
    assertThat(archived2.getCorrelationId()).isEqualTo("corr-123");
  }

  @Test
  void archiveOldEvents_doesNothingWhenNoOldEvents() {
    // Given - Only recent events
    OutboxEvent recentEvent = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    recentEvent.setSentAt(LocalDateTime.now().minusDays(1));
    outboxEventRepository.save(recentEvent);

    // When
    archivalService.archiveOldEvents();

    // Then
    assertThat(outboxEventRepository.count()).isEqualTo(1);
    assertThat(archiveEventRepository.count()).isEqualTo(0);
  }

  @Test
  void manualArchive_archivesWithCustomRetention() {
    // Given
    OutboxEvent event = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    event.setSentAt(LocalDateTime.now().minusDays(4));
    outboxEventRepository.save(event);

    // When - Archive with 3-day retention
    int archived = archivalService.manualArchive(3);

    // Then
    assertThat(archived).isEqualTo(1);
    assertThat(outboxEventRepository.count()).isEqualTo(0);
    assertThat(archiveEventRepository.count()).isEqualTo(1);
  }

  @Test
  void manualArchive_returnsZeroForInvalidRetention() {
    // Given
    OutboxEvent event = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    event.setSentAt(LocalDateTime.now().minusDays(10));
    outboxEventRepository.save(event);

    // When - Archive with invalid retention (0 days)
    int archived = archivalService.manualArchive(0);

    // Then
    assertThat(archived).isEqualTo(0);
    assertThat(outboxEventRepository.count()).isEqualTo(1); // Event not archived
  }

  @Test
  void archiveOldEvents_preservesAllEventData() {
    // Given
    OutboxEvent event =
        new OutboxEvent("Order", "A1", "OrderCreated", "corr-xyz", "{\"test\":\"data\"}");
    event.setSentAt(LocalDateTime.now().minusDays(10));
    LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(15);
    event.setCreatedAt(originalCreatedAt);
    event = outboxEventRepository.save(event);
    Long originalId = event.getId();

    // When
    archivalService.archiveOldEvents();

    // Then
    OutboxArchiveEvent archived = archiveEventRepository.findAll().get(0);
    assertThat(archived.getOriginalEventId()).isEqualTo(originalId);
    assertThat(archived.getAggregateType()).isEqualTo("Order");
    assertThat(archived.getAggregateId()).isEqualTo("A1");
    assertThat(archived.getEventType()).isEqualTo("OrderCreated");
    assertThat(archived.getCorrelationId()).isEqualTo("corr-xyz");
    assertThat(archived.getPayload()).isEqualTo("{\"test\":\"data\"}");
    assertThat(archived.getCreatedAt()).isNotNull();
    assertThat(archived.getSentAt()).isNotNull();
    assertThat(archived.getArchivedAt()).isNotNull();
  }

  @Test
  void archiveOldEvents_preservesKafkaMetadata() {
    // Given - Event with Kafka metadata
    OutboxEvent event = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    event.setSentAt(LocalDateTime.now().minusDays(10));
    event.setKafkaPartition(5);
    event.setKafkaOffset(123456L);
    event.setKafkaTimestamp(LocalDateTime.now().minusDays(10));
    event = outboxEventRepository.save(event);
    Long originalId = event.getId();

    // When
    archivalService.archiveOldEvents();

    // Then - Kafka metadata should be preserved in archive
    OutboxArchiveEvent archived = archiveEventRepository.findAll().get(0);
    assertThat(archived.getOriginalEventId()).isEqualTo(originalId);
    assertThat(archived.getKafkaPartition()).isEqualTo(5);
    assertThat(archived.getKafkaOffset()).isEqualTo(123456L);
    assertThat(archived.getKafkaTimestamp()).isNotNull();
  }

  @Test
  void manualArchive_returnsZeroWhenNoEventsToArchive() {
    // Given - No old events
    OutboxEvent event = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    event.setSentAt(LocalDateTime.now().minusDays(1));
    outboxEventRepository.save(event);

    // When - Archive with 3-day retention
    int archived = archivalService.manualArchive(3);

    // Then
    assertThat(archived).isEqualTo(0);
    assertThat(outboxEventRepository.count()).isEqualTo(1);
    assertThat(archiveEventRepository.count()).isEqualTo(0);
  }

  @Test
  void manualArchive_returnsZeroForNegativeRetention() {
    // Given
    OutboxEvent event = new OutboxEvent("Order", "A1", "OrderCreated", "{}");
    event.setSentAt(LocalDateTime.now().minusDays(10));
    outboxEventRepository.save(event);

    // When - Archive with invalid negative retention
    int archived = archivalService.manualArchive(-5);

    // Then
    assertThat(archived).isEqualTo(0);
    assertThat(outboxEventRepository.count()).isEqualTo(1); // Event not archived
  }
}
