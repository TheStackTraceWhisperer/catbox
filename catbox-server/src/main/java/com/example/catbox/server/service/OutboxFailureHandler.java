package com.example.catbox.server.service;

import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.catbox.server.config.OutboxProcessingConfig;
import com.example.catbox.server.entity.OutboxDeadLetterEvent;
import com.example.catbox.server.metrics.OutboxMetricsService;
import com.example.catbox.server.repository.OutboxDeadLetterEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles permanent and transient failures for outbox events. Operates in REQUIRES_NEW transaction
 * to ensure failure recording happens independently of the publishing transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxFailureHandler {

  private final OutboxEventRepository outboxEventRepository;
  private final OutboxDeadLetterEventRepository deadLetterRepository;
  private final OutboxProcessingConfig processingConfig;
  private final OutboxMetricsService metricsService;

  /**
   * Records a permanent failure for an event. If the event has exceeded max retries, moves it to
   * the dead-letter queue.
   *
   * @param eventId The ID of the event that failed
   * @param errorMessage The error message from the failure
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void recordPermanentFailure(Long eventId, String errorMessage) {
    OutboxEvent event =
        outboxEventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

    // Increment failure count
    Integer currentCount = event.getPermanentFailureCount();
    if (currentCount == null) {
      currentCount = 0;
    }
    currentCount++;
    event.setPermanentFailureCount(currentCount);
    event.setLastError(errorMessage);

    log.warn(
        "Recording permanent failure #{} for event: {}. Error: {}",
        currentCount,
        eventId,
        errorMessage);

    // Record the retry metric
    metricsService.recordPermanentFailureRetry();

    if (currentCount >= processingConfig.getMaxPermanentRetries()) {
      // Move to dead-letter queue
      log.error(
          "Event {} exceeded max permanent retries ({}). Moving to dead-letter queue.",
          eventId,
          processingConfig.getMaxPermanentRetries());

      OutboxDeadLetterEvent deadLetter = new OutboxDeadLetterEvent(event, errorMessage);
      deadLetterRepository.save(deadLetter);

      // Delete from outbox
      outboxEventRepository.delete(event);

      // Record metrics
      metricsService.recordDeadLetter();

      log.info("Event {} moved to dead-letter queue with ID: {}", eventId, deadLetter.getId());
    } else {
      // Save the updated failure count and clear the claim for retry
      event.setInProgressUntil(null);
      outboxEventRepository.save(event);

      log.info(
          "Event {} will be retried. Failures: {}/{}",
          eventId,
          currentCount,
          processingConfig.getMaxPermanentRetries());
    }
  }

  /**
   * Resets the failure count for an event after successful publication.
   *
   * @param event The event that was successfully published
   */
  @Transactional(propagation = Propagation.MANDATORY)
  public void resetFailureCount(OutboxEvent event) {
    if (event.getPermanentFailureCount() != null && event.getPermanentFailureCount() > 0) {
      log.debug("Resetting failure count for event: {}", event.getId());
      event.setPermanentFailureCount(0);
      event.setLastError(null);
    }
  }
}
