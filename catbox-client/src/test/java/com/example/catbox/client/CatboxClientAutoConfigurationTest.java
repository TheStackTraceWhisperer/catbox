package com.example.catbox.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.example.catbox.client.metrics.CatboxClientMetricsService;
import com.example.catbox.common.repository.ProcessedMessageRepository;
import org.junit.jupiter.api.Test;

/** Tests for CatboxClientAutoConfiguration. */
class CatboxClientAutoConfigurationTest {

  @Test
  void createDefaultOutboxFilter_createsDatabaseImplementation() {
    // Given
    CatboxClientAutoConfiguration config = new CatboxClientAutoConfiguration();
    ProcessedMessageRepository mockRepository = mock(ProcessedMessageRepository.class);
    CatboxClientMetricsService mockMetricsService = mock(CatboxClientMetricsService.class);

    // When
    OutboxFilter filter = config.outboxFilter(mockRepository, mockMetricsService);

    // Then
    assertThat(filter).isNotNull();
    assertThat(filter).isInstanceOf(DatabaseOutboxFilter.class);
  }
}
