package com.example.order.tracing;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests that verify the tracing profile can be enabled.
 *
 * <p>Note: Similar tests exist in routebox-server. This duplication is intentional to ensure each
 * module can verify its own tracing configuration independently.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
@ActiveProfiles("tracing")
class TracingProfileEnabledTest {

  @Autowired(required = false)
  private Tracer tracer;

  @Value("${management.tracing.sampling.probability:0.0}")
  private double samplingProbability;

  @Value("${management.otlp.tracing.endpoint:}")
  private String otlpEndpoint;

  @Test
  void tracerBeanShouldBeAvailableWithTracingProfile() {
    // Tracer should be available when tracing profile is active
    assertThat(tracer).isNotNull();
  }

  @Test
  void tracingConfigurationShouldBeSetWithTracingProfile() {
    // Tracing configuration should be set when tracing profile is active
    assertThat(samplingProbability).isEqualTo(0.1);
    assertThat(otlpEndpoint).isEqualTo("http://localhost:4318/v1/traces");
  }
}
