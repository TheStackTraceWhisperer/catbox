package com.example.routebox.server.tracing;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;

/** Tests that verify distributed tracing is properly configured. */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "management.tracing.sampling.probability=1.0"
    },
    classes = {
      // Exclude Kafka auto-configuration
    })
@AutoConfigureObservability
class TracingConfigurationTest {

  @Autowired(required = false)
  private Tracer tracer;

  @Test
  void tracerBeanShouldBeAvailable() {
    assertThat(tracer).isNotNull();
  }

  @Test
  void tracerShouldCreateSpans() {
    Span span = tracer.nextSpan().name("test-span").start();
    try {
      assertThat(span).isNotNull();
      assertThat(span.context()).isNotNull();
      assertThat(span.context().traceId()).isNotNull();
      assertThat(span.context().spanId()).isNotNull();
    } finally {
      span.end();
    }
  }

  @Test
  void tracerShouldPropagateContext() {
    Span parentSpan = tracer.nextSpan().name("parent-span").start();
    try (Tracer.SpanInScope ws = tracer.withSpan(parentSpan)) {
      Span currentSpan = tracer.currentSpan();
      assertThat(currentSpan).isNotNull();
      assertThat(currentSpan.context().traceId()).isEqualTo(parentSpan.context().traceId());
    } finally {
      parentSpan.end();
    }
  }
}
