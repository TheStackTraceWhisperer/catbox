package com.example.routebox.client;

import com.example.catbox.client.metrics.RouteBoxClientMetricsService;
import com.example.catbox.common.entity.ProcessedMessage;
import com.example.catbox.common.repository.ProcessedMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Database-backed implementation of {@link OutboxFilter} that tracks processed correlation IDs per
 * consumer group using a database table.
 *
 * <p>This implementation stores processed messages in the database, which means:
 *
 * <ul>
 *   <li>State survives application restarts
 *   <li>Works correctly in multi-instance deployments
 *   <li>Supports per-consumer-group tracking
 *   <li>Can be archived for long-term auditing
 * </ul>
 *
 * <p>Thread-safe and designed for concurrent use across multiple consumer instances.
 */
@Slf4j
public class DatabaseOutboxFilter implements OutboxFilter {

  private final ProcessedMessageRepository repository;
  private final RouteBoxClientMetricsService metricsService;

  public DatabaseOutboxFilter(
      final ProcessedMessageRepository repository,
      final RouteBoxClientMetricsService metricsService) {
    this.repository = repository;
    this.metricsService = metricsService;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean deduped(final String correlationId, final String consumerGroup) {
    if (correlationId == null || correlationId.isEmpty()) {
      log.warn("Received null or empty correlationId, " + "treating as not processed");
      return false;
    }

    if (consumerGroup == null || consumerGroup.isEmpty()) {
      log.warn("Received null or empty consumerGroup, " + "treating as not processed");
      return false;
    }

    // Check if already processed
    if (repository.existsByCorrelationIdAndConsumerGroup(correlationId, consumerGroup)) {
      log.debug(
          "Duplicate detected for correlationId: {} " + "in consumerGroup: {}",
          correlationId,
          consumerGroup);
      recordFilterDeduped();
      return true;
    }

    // Try to mark as processed
    try {
      ProcessedMessage message = new ProcessedMessage(correlationId, consumerGroup);
      repository.save(message);
      log.trace(
          "First time processing correlationId: {} " + "in consumerGroup: {}",
          correlationId,
          consumerGroup);
      recordFilterUnique();
      return false;
    } catch (DataIntegrityViolationException e) {
      // Race condition: another instance processed it concurrently
      log.debug(
          "Concurrent duplicate detected for correlationId: {} " + "in consumerGroup: {}",
          correlationId,
          consumerGroup);
      recordFilterConcurrentDuplicate();
      return true;
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markProcessed(final String correlationId, final String consumerGroup) {
    if (correlationId == null || correlationId.isEmpty()) {
      log.warn("Attempted to mark null or empty correlationId " + "as processed");
      return;
    }

    if (consumerGroup == null || consumerGroup.isEmpty()) {
      log.warn("Attempted to mark with null or empty consumerGroup");
      return;
    }

    try {
      ProcessedMessage message = new ProcessedMessage(correlationId, consumerGroup);
      repository.save(message);
      log.trace(
          "Marked correlationId: {} as processed " + "in consumerGroup: {}",
          correlationId,
          consumerGroup);
    } catch (DataIntegrityViolationException e) {
      log.debug(
          "CorrelationId: {} already marked as processed " + "in consumerGroup: {}",
          correlationId,
          consumerGroup);
    }
    recordFilterMarkProcessed();
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isProcessed(final String correlationId, final String consumerGroup) {
    if (correlationId == null || correlationId.isEmpty()) {
      return false;
    }

    if (consumerGroup == null || consumerGroup.isEmpty()) {
      return false;
    }

    return repository.existsByCorrelationIdAndConsumerGroup(correlationId, consumerGroup);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markUnprocessed(final String correlationId, final String consumerGroup) {
    if (correlationId == null || correlationId.isEmpty()) {
      log.warn("Attempted to mark null or empty correlationId " + "as unprocessed");
      return;
    }

    if (consumerGroup == null || consumerGroup.isEmpty()) {
      log.warn("Attempted to unmark with null or empty consumerGroup");
      return;
    }

    repository.deleteByCorrelationIdAndConsumerGroup(correlationId, consumerGroup);
    log.info(
        "Marked correlationId: {} as unprocessed " + "in consumerGroup: {}",
        correlationId,
        consumerGroup);
    recordFilterMarkUnprocessed();
  }

  private void recordFilterDeduped() {
    if (metricsService != null) {
      metricsService.recordFilterDeduped();
    }
  }

  private void recordFilterUnique() {
    if (metricsService != null) {
      metricsService.recordFilterUnique();
    }
  }

  private void recordFilterConcurrentDuplicate() {
    if (metricsService != null) {
      metricsService.recordFilterConcurrentDuplicate();
    }
  }

  private void recordFilterMarkProcessed() {
    if (metricsService != null) {
      metricsService.recordFilterMarkProcessed();
    }
  }

  private void recordFilterMarkUnprocessed() {
    if (metricsService != null) {
      metricsService.recordFilterMarkUnprocessed();
    }
  }
}
