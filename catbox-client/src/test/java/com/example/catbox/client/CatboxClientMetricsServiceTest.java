package com.example.catbox.client;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for CatboxClientMetricsService. */
class CatboxClientMetricsServiceTest {

  private CatboxClientMetricsService metricsService;
  private MeterRegistry meterRegistry;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    metricsService = new CatboxClientMetricsService(meterRegistry);
    metricsService.initializeMetrics();
  }

  @Test
  void initializeMetrics_createsAllCounters() {
    // Then - All counters should be registered
    assertThat(meterRegistry.find("catbox.client.outbox.write.success").counter()).isNotNull();
    assertThat(meterRegistry.find("catbox.client.outbox.write.failure").counter()).isNotNull();
    assertThat(meterRegistry.find("catbox.client.filter.deduped").counter()).isNotNull();
    assertThat(meterRegistry.find("catbox.client.filter.unique").counter()).isNotNull();
    assertThat(meterRegistry.find("catbox.client.filter.concurrent.duplicate").counter())
        .isNotNull();
    assertThat(meterRegistry.find("catbox.client.filter.mark.processed").counter()).isNotNull();
    assertThat(meterRegistry.find("catbox.client.filter.mark.unprocessed").counter()).isNotNull();
  }

  @Test
  void recordOutboxWriteSuccess_incrementsCounter() {
    // When
    metricsService.recordOutboxWriteSuccess();
    metricsService.recordOutboxWriteSuccess();

    // Then
    assertThat(meterRegistry.find("catbox.client.outbox.write.success").counter().count())
        .isEqualTo(2.0);
  }

  @Test
  void recordOutboxWriteFailure_incrementsCounter() {
    // When
    metricsService.recordOutboxWriteFailure();

    // Then
    assertThat(meterRegistry.find("catbox.client.outbox.write.failure").counter().count())
        .isEqualTo(1.0);
  }

  @Test
  void recordFilterDeduped_incrementsCounter() {
    // When
    metricsService.recordFilterDeduped();
    metricsService.recordFilterDeduped();
    metricsService.recordFilterDeduped();

    // Then
    assertThat(meterRegistry.find("catbox.client.filter.deduped").counter().count()).isEqualTo(3.0);
  }

  @Test
  void recordFilterUnique_incrementsCounter() {
    // When
    metricsService.recordFilterUnique();

    // Then
    assertThat(meterRegistry.find("catbox.client.filter.unique").counter().count()).isEqualTo(1.0);
  }

  @Test
  void recordFilterConcurrentDuplicate_incrementsCounter() {
    // When
    metricsService.recordFilterConcurrentDuplicate();

    // Then
    assertThat(meterRegistry.find("catbox.client.filter.concurrent.duplicate").counter().count())
        .isEqualTo(1.0);
  }

  @Test
  void recordFilterMarkProcessed_incrementsCounter() {
    // When
    metricsService.recordFilterMarkProcessed();
    metricsService.recordFilterMarkProcessed();

    // Then
    assertThat(meterRegistry.find("catbox.client.filter.mark.processed").counter().count())
        .isEqualTo(2.0);
  }

  @Test
  void recordFilterMarkUnprocessed_incrementsCounter() {
    // When
    metricsService.recordFilterMarkUnprocessed();

    // Then
    assertThat(meterRegistry.find("catbox.client.filter.mark.unprocessed").counter().count())
        .isEqualTo(1.0);
  }
}
