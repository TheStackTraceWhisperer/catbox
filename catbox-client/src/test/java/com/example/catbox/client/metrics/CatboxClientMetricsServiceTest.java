package com.example.catbox.client.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatboxClientMetricsServiceTest {

  private MeterRegistry meterRegistry;
  private CatboxClientMetricsService metricsService;

  @BeforeEach
  void setup() {
    meterRegistry = new SimpleMeterRegistry();
    metricsService = new CatboxClientMetricsService(meterRegistry);
    metricsService.initializeMetrics();
  }

  @Test
  void metricsAreRegistered() {
    // Verify all metrics are registered
    Counter outboxWriteSuccessCounter =
        meterRegistry.find("catbox.client.outbox.write.success").counter();
    assertThat(outboxWriteSuccessCounter).isNotNull();

    Counter outboxWriteFailureCounter =
        meterRegistry.find("catbox.client.outbox.write.failure").counter();
    assertThat(outboxWriteFailureCounter).isNotNull();

    Counter filterUniqueCounter = meterRegistry.find("catbox.client.filter.unique").counter();
    assertThat(filterUniqueCounter).isNotNull();

    Counter filterDedupedCounter = meterRegistry.find("catbox.client.filter.deduped").counter();
    assertThat(filterDedupedCounter).isNotNull();

    Counter filterConcurrentDuplicateCounter =
        meterRegistry.find("catbox.client.filter.concurrent.duplicate").counter();
    assertThat(filterConcurrentDuplicateCounter).isNotNull();

    Counter filterMarkProcessedCounter =
        meterRegistry.find("catbox.client.filter.mark.processed").counter();
    assertThat(filterMarkProcessedCounter).isNotNull();

    Counter filterMarkUnprocessedCounter =
        meterRegistry.find("catbox.client.filter.mark.unprocessed").counter();
    assertThat(filterMarkUnprocessedCounter).isNotNull();
  }

  @Test
  void recordOutboxWriteSuccess_incrementsCounter() {
    Counter counter = meterRegistry.find("catbox.client.outbox.write.success").counter();
    double initialCount = counter.count();

    metricsService.recordOutboxWriteSuccess();

    assertThat(counter.count()).isEqualTo(initialCount + 1);
  }

  @Test
  void recordOutboxWriteFailure_incrementsCounter() {
    Counter counter = meterRegistry.find("catbox.client.outbox.write.failure").counter();
    double initialCount = counter.count();

    metricsService.recordOutboxWriteFailure();

    assertThat(counter.count()).isEqualTo(initialCount + 1);
  }

  @Test
  void recordFilterUnique_incrementsCounter() {
    Counter counter = meterRegistry.find("catbox.client.filter.unique").counter();
    double initialCount = counter.count();

    metricsService.recordFilterUnique();

    assertThat(counter.count()).isEqualTo(initialCount + 1);
  }

  @Test
  void recordFilterDeduped_incrementsCounter() {
    Counter counter = meterRegistry.find("catbox.client.filter.deduped").counter();
    double initialCount = counter.count();

    metricsService.recordFilterDeduped();

    assertThat(counter.count()).isEqualTo(initialCount + 1);
  }

  @Test
  void recordFilterConcurrentDuplicate_incrementsCounter() {
    Counter counter = meterRegistry.find("catbox.client.filter.concurrent.duplicate").counter();
    double initialCount = counter.count();

    metricsService.recordFilterConcurrentDuplicate();

    assertThat(counter.count()).isEqualTo(initialCount + 1);
  }

  @Test
  void recordFilterMarkProcessed_incrementsCounter() {
    Counter counter = meterRegistry.find("catbox.client.filter.mark.processed").counter();
    double initialCount = counter.count();

    metricsService.recordFilterMarkProcessed();

    assertThat(counter.count()).isEqualTo(initialCount + 1);
  }

  @Test
  void recordFilterMarkUnprocessed_incrementsCounter() {
    Counter counter = meterRegistry.find("catbox.client.filter.mark.unprocessed").counter();
    double initialCount = counter.count();

    metricsService.recordFilterMarkUnprocessed();

    assertThat(counter.count()).isEqualTo(initialCount + 1);
  }

  @Test
  void multipleIncrements_accumulateCorrectly() {
    Counter outboxWriteSuccessCounter =
        meterRegistry.find("catbox.client.outbox.write.success").counter();
    Counter filterUniqueCounter = meterRegistry.find("catbox.client.filter.unique").counter();

    // Record multiple events
    metricsService.recordOutboxWriteSuccess();
    metricsService.recordOutboxWriteSuccess();
    metricsService.recordOutboxWriteSuccess();

    metricsService.recordFilterUnique();
    metricsService.recordFilterUnique();

    // Verify counts
    assertThat(outboxWriteSuccessCounter.count()).isEqualTo(3.0);
    assertThat(filterUniqueCounter.count()).isEqualTo(2.0);
  }
}
