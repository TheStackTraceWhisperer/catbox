package com.example.routebox.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import com.example.routebox.server.config.OutboxProcessingConfig;
import com.example.routebox.server.entity.OutboxDeadLetterEvent;
import com.example.routebox.server.repository.OutboxDeadLetterEventRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.example.routebox.test.listener.SharedTestcontainers;

/**
 * Tests for the OutboxFailureHandler service that manages permanent failures and dead-letter
 * events.
 * 
 * Note: This test is NOT @Transactional because the service methods use Propagation.REQUIRES_NEW,
 * which would conflict with a test-level transaction and cause deadlocks. The @BeforeEach method
 * explicitly cleans up data instead.
 */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class OutboxFailureHandlerTest {

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @Autowired OutboxEventRepository outboxEventRepository;

  @Autowired OutboxDeadLetterEventRepository deadLetterRepository;

  @Autowired OutboxFailureHandler failureHandler;

  @Autowired OutboxProcessingConfig processingConfig;

  @Autowired EntityManager entityManager;

  @BeforeEach
  void setup() {
    deadLetterRepository.deleteAll();
    outboxEventRepository.deleteAll();
  }

  @Test
  void recordPermanentFailure_incrementsFailureCount() {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    assertThat(event.getPermanentFailureCount()).isEqualTo(0);

    // When
    failureHandler.recordPermanentFailure(event.getId(), "Test error");

    // Clear the persistence context to force fresh reads from database after REQUIRES_NEW transactions
    entityManager.clear();

    // Then
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(1);
    assertThat(updated.getLastError()).isEqualTo("Test error");
    assertThat(updated.getInProgressUntil()).isNull(); // Claim cleared for retry
  }

  @Test
  void recordPermanentFailure_movesToDeadLetterAfterMaxRetries() {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    Long eventId = event.getId();

    // When - Record failures up to max retries
    int maxRetries = processingConfig.getMaxPermanentRetries();
    for (int i = 0; i < maxRetries; i++) {
      failureHandler.recordPermanentFailure(eventId, "Error attempt " + (i + 1));

      // Clear persistence context to see fresh data for the assertion below
      entityManager.clear();

      if (i < maxRetries - 1) {
        // Event should still exist before last failure
        assertThat(outboxEventRepository.findById(eventId)).isPresent();
      }
    }

    // Clear persistence context again to see that event was deleted and moved to dead letter
    entityManager.clear();

    // Then - Event should be moved to dead letter queue
    Optional<OutboxEvent> deletedEvent = outboxEventRepository.findById(eventId);
    assertThat(deletedEvent).isEmpty();

    List<OutboxDeadLetterEvent> deadLetters = deadLetterRepository.findAll();
    assertThat(deadLetters).hasSize(1);

    OutboxDeadLetterEvent deadLetter = deadLetters.get(0);
    assertThat(deadLetter.getOriginalEventId()).isEqualTo(eventId);
    assertThat(deadLetter.getAggregateType()).isEqualTo("Order");
    assertThat(deadLetter.getAggregateId()).isEqualTo("A1");
    assertThat(deadLetter.getEventType()).isEqualTo("OrderCreated");
    assertThat(deadLetter.getPayload()).isEqualTo("{}");
    assertThat(deadLetter.getFinalError()).contains("Error attempt");
    assertThat(deadLetter.getFailedAt()).isNotNull();
  }

  @Test
  @Transactional // Required because resetFailureCount uses Propagation.MANDATORY
  void resetFailureCount_clearsFailureData() {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    event.setPermanentFailureCount(2);
    event.setLastError("Previous error");
    outboxEventRepository.save(event);

    // When
    failureHandler.resetFailureCount(event);
    outboxEventRepository.save(event);

    // Then
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
    assertThat(updated.getLastError()).isNull();
  }

  @Test
  @Transactional // Required because resetFailureCount uses Propagation.MANDATORY
  void resetFailureCount_doesNothingWhenCountIsZero() {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    assertThat(event.getPermanentFailureCount()).isEqualTo(0);

    // When
    failureHandler.resetFailureCount(event);
    outboxEventRepository.save(event);

    // Then
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
  }

  @Test
  void recordPermanentFailure_handlesMultipleEvents() {
    // Given
    OutboxEvent event1 =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    OutboxEvent event2 =
        outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderCreated", "{}"));

    // When
    failureHandler.recordPermanentFailure(event1.getId(), "Error 1");
    failureHandler.recordPermanentFailure(event2.getId(), "Error 2");
    failureHandler.recordPermanentFailure(event1.getId(), "Error 1 again");

    // Clear the persistence context to force fresh reads from database after REQUIRES_NEW transactions
    entityManager.clear();

    // Then
    OutboxEvent updated1 = outboxEventRepository.findById(event1.getId()).orElseThrow();
    assertThat(updated1.getPermanentFailureCount()).isEqualTo(2);
    assertThat(updated1.getLastError()).isEqualTo("Error 1 again");

    OutboxEvent updated2 = outboxEventRepository.findById(event2.getId()).orElseThrow();
    assertThat(updated2.getPermanentFailureCount()).isEqualTo(1);
    assertThat(updated2.getLastError()).isEqualTo("Error 2");
  }

  @Test
  void recordPermanentFailure_throwsExceptionWhenEventNotFound() {
    // When & Then
    assertThatThrownBy(() -> failureHandler.recordPermanentFailure(99999L, "Error"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event not found");
  }

  @Test
  void releaseClaimForTransientFailure_releasesEventClaim() {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    // Simulate that the event was claimed
    event.setInProgressUntil(java.time.LocalDateTime.now().plusMinutes(5));
    outboxEventRepository.save(event);

    // When
    failureHandler.releaseClaimForTransientFailure(event.getId());

    // Clear the persistence context to force fresh reads from database after REQUIRES_NEW transactions
    entityManager.clear();

    // Then
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getInProgressUntil()).isNull();
  }

  @Test
  void releaseClaimForTransientFailure_throwsExceptionWhenEventNotFound() {
    // When & Then
    assertThatThrownBy(() -> failureHandler.releaseClaimForTransientFailure(99999L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Event not found");
  }
}
