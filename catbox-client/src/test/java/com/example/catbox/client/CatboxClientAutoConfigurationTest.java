package com.example.catbox.client;

import com.example.catbox.common.repository.ProcessedMessageRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for CatboxClientAutoConfiguration.
 */
class CatboxClientAutoConfigurationTest {

    @Test
    void createDefaultOutboxFilter_createsDatabaseImplementation() {
        // Given
        CatboxClientAutoConfiguration config =
                new CatboxClientAutoConfiguration();
        ProcessedMessageRepository mockRepository =
                mock(ProcessedMessageRepository.class);

        // When
        OutboxFilter filter = config.outboxFilter(mockRepository);

        // Then
        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(DatabaseOutboxFilter.class);
    }
}
