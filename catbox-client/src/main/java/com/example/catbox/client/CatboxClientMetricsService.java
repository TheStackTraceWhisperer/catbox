package com.example.catbox.client;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that tracks custom metrics for the catbox client operations. Provides insights into
 * client-side outbox writes and filter operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CatboxClientMetricsService {

  private final MeterRegistry meterRegistry;

  // Counters for OutboxClient operations
  private Counter outboxWriteSuccessCounter;
  private Counter outboxWriteFailureCounter;

  // Counters for OutboxFilter operations
  private Counter filterDedupedCounter;
  private Counter filterUniqueCounter;
  private Counter filterConcurrentDuplicateCounter;
  private Counter filterMarkProcessedCounter;
  private Counter filterMarkUnprocessedCounter;

  /** Initialize metrics on bean construction. */
  @PostConstruct
  public void initializeMetrics() {
    // OutboxClient metrics
    outboxWriteSuccessCounter =
        Counter.builder("catbox.client.outbox.write.success")
            .description("Total number of events successfully written to the outbox table")
            .register(meterRegistry);

    outboxWriteFailureCounter =
        Counter.builder("catbox.client.outbox.write.failure")
            .description(
                "Total number of events that failed to write to the outbox table (e.g.,"
                    + " serialization errors)")
            .register(meterRegistry);

    // OutboxFilter metrics
    filterDedupedCounter =
        Counter.builder("catbox.client.filter.deduped")
            .description("Total number of messages filtered as duplicates")
            .register(meterRegistry);

    filterUniqueCounter =
        Counter.builder("catbox.client.filter.unique")
            .description("Total number of unique messages that completed successfully")
            .register(meterRegistry);

    filterConcurrentDuplicateCounter =
        Counter.builder("catbox.client.filter.concurrent.duplicate")
            .description(
                "Total number of concurrent duplicate detections (race condition in deduped check)")
            .register(meterRegistry);

    filterMarkProcessedCounter =
        Counter.builder("catbox.client.filter.mark.processed")
            .description("Total number of messages explicitly marked as processed")
            .register(meterRegistry);

    filterMarkUnprocessedCounter =
        Counter.builder("catbox.client.filter.mark.unprocessed")
            .description("Total number of messages explicitly marked as unprocessed")
            .register(meterRegistry);

    log.info("Catbox client metrics initialized");
  }

  /** Record a successful write to the outbox table. */
  public void recordOutboxWriteSuccess() {
    outboxWriteSuccessCounter.increment();
  }

  /** Record a failed write to the outbox table. */
  public void recordOutboxWriteFailure() {
    outboxWriteFailureCounter.increment();
  }

  /** Record a message that was filtered as a duplicate. */
  public void recordFilterDeduped() {
    filterDedupedCounter.increment();
  }

  /** Record a unique message that completed successfully. */
  public void recordFilterUnique() {
    filterUniqueCounter.increment();
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
