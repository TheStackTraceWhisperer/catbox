package com.example.order.tracing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/** Tests that verify tracing configuration is minimal by default. */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
class TracingDisabledByDefaultTest {

  @Value("${management.otlp.tracing.endpoint:}")
  private String otlpEndpoint;

  @Test
  void otlpEndpointShouldNotBeConfiguredByDefault() {
    // OTLP endpoint should not be configured when tracing profile is not active
    assertThat(otlpEndpoint).isEmpty();
  }
}
