package com.example.routebox.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.example.routebox.client.metrics.RouteBoxClientMetricsService;
import com.example.routebox.common.repository.ProcessedMessageRepository;
import org.junit.jupiter.api.Test;

/** Tests for RouteBoxClientAutoConfiguration. */
class RouteBoxClientAutoConfigurationTest {

  @Test
  void createDefaultOutboxFilter_createsDatabaseImplementation() {
    // Given
    RouteBoxClientAutoConfiguration config = new RouteBoxClientAutoConfiguration();
    ProcessedMessageRepository mockRepository = mock(ProcessedMessageRepository.class);
    RouteBoxClientMetricsService mockMetricsService = mock(RouteBoxClientMetricsService.class);

    // When
    OutboxFilter filter = config.outboxFilter(mockRepository, mockMetricsService);

    // Then
    assertThat(filter).isNotNull();
    assertThat(filter).isInstanceOf(DatabaseOutboxFilter.class);
  }
}
