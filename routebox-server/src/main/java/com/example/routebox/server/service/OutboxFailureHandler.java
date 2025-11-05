package com.example.routebox.server.service;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.config.OutboxProcessingConfig;
import com.example.routebox.server.entity.OutboxDeadLetterEvent;
import com.example.routebox.server.metrics.OutboxMetricsService;
import com.example.routebox.server.repository.OutboxDeadLetterEventRepository;
import java.util.Set;
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

  /**
   * Releases the claim for an event that failed with a transient error. This allows the event to be
   * retried on the next polling cycle instead of waiting for the full claim timeout.
   *
   * @param eventId The ID of the event to release
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void releaseClaimForTransientFailure(Long eventId) {
    OutboxEvent event =
        outboxEventRepository
            .findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

    log.warn(
        "Releasing claim for event {} due to transient failure. Event will be retried on next poll.",
        eventId);

    event.setInProgressUntil(null);
    outboxEventRepository.save(event);
  }

  /**
   * Handles a failure for an event by determining if it's permanent or transient and taking
   * appropriate action. This is the main entry point for failure handling from the worker.
   *
   * @param event The event that failed
   * @param exception The exception that occurred
   */
  public void handleFailure(OutboxEvent event, Exception exception) {
    // Record failure metric first to ensure it's tracked even if logging fails
    metricsService.recordPublishFailure();

    // Determine if this is a permanent or transient failure
    if (isPermanentFailure(exception)) {
      // PERMANENT: Call the failure handler
      log.error(
          "Permanent failure publishing event: {}. Recording failure. Error: {}",
          event.getId(),
          exception.getMessage());
      recordPermanentFailure(event.getId(), exception.getMessage());
    } else {
      // TRANSIENT: Release the claim so event can be retried immediately
      log.warn(
          "Transient failure publishing event: {}. Releasing claim for re-polling. Error: {}",
          event.getId(),
          exception.getMessage());
      releaseClaimForTransientFailure(event.getId());
    }
  }

  /**
   * Classifies exceptions based on the configurable list. This method recursively checks the
   * exception and its causes.
   */
  private boolean isPermanentFailure(Throwable e) {
    final Set<String> permanentErrors = processingConfig.getPermanentExceptionSet();

    Throwable current = e;
    while (current != null) {
      String exceptionName = current.getClass().getName();
      if (permanentErrors.contains(exceptionName)) {
        return true;
      }
      // Move to the cause, but stop if we hit a circular reference
      current = (current.getCause() == current) ? null : current.getCause();
    }

    // No match found in the exception chain
    return false;
  }
}
