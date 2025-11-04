package com.example.routebox.client;

import com.example.routebox.common.repository.ProcessedMessageRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for RouteBoxClientAutoConfiguration.
 */
class RouteBoxClientAutoConfigurationTest {

    @Test
    void createDefaultOutboxFilter_createsDatabaseImplementation() {
        // Given
        RouteBoxClientAutoConfiguration config =
                new RouteBoxClientAutoConfiguration();
        ProcessedMessageRepository mockRepository =
                mock(ProcessedMessageRepository.class);

        // When
        OutboxFilter filter = config.outboxFilter(mockRepository);

        // Then
        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(DatabaseOutboxFilter.class);
    }
}
