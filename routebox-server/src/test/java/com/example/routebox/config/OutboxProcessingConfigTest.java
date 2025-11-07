package com.example.routebox.server.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.routebox.server.RouteBoxServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.example.routebox.test.listener.SharedTestcontainers;

/** Tests for OutboxProcessingConfig to verify configurable permanent failure settings. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class OutboxProcessingConfigTest {

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @Autowired OutboxProcessingConfig config;

  @Test
  void config_loadsDefaultPermanentFailureExceptions() {
    // Then - Defaults from application.yml should be loaded
    assertThat(config.getPermanentFailureExceptions()).isNotEmpty();
    assertThat(config.getPermanentExceptionSet()).isNotEmpty();

    // Should contain the configured exceptions
    assertThat(config.getPermanentExceptionSet())
        .contains(
            "java.lang.IllegalStateException",
            "org.apache.kafka.common.errors.InvalidTopicException",
            "org.apache.kafka.common.errors.RecordTooLargeException",
            "org.apache.kafka.common.errors.SerializationException",
            "org.apache.kafka.common.errors.AuthenticationException",
            "org.apache.kafka.common.errors.AuthorizationException");
  }

  @Test
  void config_loadsMaxPermanentRetries() {
    // Then - Should load the configured value
    assertThat(config.getMaxPermanentRetries()).isEqualTo(5);
  }

  @Test
  void config_hasExistingProcessingSettings() {
    // Then - Existing settings should still work
    assertThat(config.getClaimTimeout()).isEqualTo(java.time.Duration.ofMinutes(5));
    assertThat(config.getBatchSize()).isEqualTo(100);
    assertThat(config.getPollFixedDelay()).isEqualTo(java.time.Duration.ofSeconds(2));
    assertThat(config.getPollInitialDelay()).isEqualTo(java.time.Duration.ofSeconds(10));
  }

  @Test
  void config_hasBackpressureSettings() {
    // Then - Backpressure settings should have correct defaults
    assertThat(config.getWorkerConcurrency()).isEqualTo(50);
    assertThat(config.getQueueCapacity()).isEqualTo(200);
  }
}
