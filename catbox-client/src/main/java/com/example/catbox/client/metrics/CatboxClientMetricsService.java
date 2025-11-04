package com.example.catbox.client.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that tracks custom metrics for client-side catbox operations. Provides insights into
 * client-side outbox writes and deduplication filter behavior.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CatboxClientMetricsService {

  private final MeterRegistry meterRegistry;

  // Counters for outbox write operations
  private Counter outboxWriteSuccessCounter;
  private Counter outboxWriteFailureCounter;

  // Counters for filter operations
  private Counter filterUniqueCounter;
  private Counter filterDedupedCounter;
  private Counter filterConcurrentDuplicateCounter;
  private Counter filterMarkProcessedCounter;
  private Counter filterMarkUnprocessedCounter;

  /** Initialize metrics on bean construction. */
  @PostConstruct
  public void initializeMetrics() {
    // Counter: Successful outbox writes
    outboxWriteSuccessCounter =
        Counter.builder("catbox.client.outbox.write.success")
            .description("Events successfully written to outbox table")
            .register(meterRegistry);

    // Counter: Failed outbox writes
    outboxWriteFailureCounter =
        Counter.builder("catbox.client.outbox.write.failure")
            .description("Events that failed to write (serialization errors)")
            .register(meterRegistry);

    // Counter: Unique messages processed
    filterUniqueCounter =
        Counter.builder("catbox.client.filter.unique")
            .description("Unique messages processed successfully")
            .register(meterRegistry);

    // Counter: Deduplicated messages
    filterDedupedCounter =
        Counter.builder("catbox.client.filter.deduped")
            .description("Messages filtered as duplicates")
            .register(meterRegistry);

    // Counter: Concurrent duplicate detections
    filterConcurrentDuplicateCounter =
        Counter.builder("catbox.client.filter.concurrent.duplicate")
            .description("Concurrent duplicate detections (race conditions)")
            .register(meterRegistry);

    // Counter: Messages marked as processed
    filterMarkProcessedCounter =
        Counter.builder("catbox.client.filter.mark.processed")
            .description("Messages explicitly marked as processed")
            .register(meterRegistry);

    // Counter: Messages marked as unprocessed
    filterMarkUnprocessedCounter =
        Counter.builder("catbox.client.filter.mark.unprocessed")
            .description("Messages explicitly marked as unprocessed")
            .register(meterRegistry);

    log.info("Catbox client metrics initialized");
  }

  /** Record a successful outbox write. */
  public void recordOutboxWriteSuccess() {
    outboxWriteSuccessCounter.increment();
  }

  /** Record a failed outbox write. */
  public void recordOutboxWriteFailure() {
    outboxWriteFailureCounter.increment();
  }

  /** Record a unique message processed through the filter. */
  public void recordFilterUnique() {
    filterUniqueCounter.increment();
  }

  /** Record a message filtered as a duplicate. */
  public void recordFilterDeduped() {
    filterDedupedCounter.increment();
  }

  /** Record a concurrent duplicate detection (race condition). */
  public void recordFilterConcurrentDuplicate() {
    filterConcurrentDuplicateCounter.increment();
  }

  /** Record a message explicitly marked as processed. */
  public void recordFilterMarkProcessed() {
    filterMarkProcessedCounter.increment();
  }

  /** Record a message explicitly marked as unprocessed. */
  public void recordFilterMarkUnprocessed() {
    filterMarkUnprocessedCounter.increment();
  }
}
