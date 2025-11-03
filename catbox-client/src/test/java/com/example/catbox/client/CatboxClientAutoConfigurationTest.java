package com.example.catbox.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CatboxClientAutoConfiguration.
 */
class CatboxClientAutoConfigurationTest {

    @Test
    void createDefaultOutboxFilter_createsInMemoryImplementation() {
        // Given
        CatboxClientAutoConfiguration config = new CatboxClientAutoConfiguration();

        // When
        OutboxFilter filter = config.outboxFilter();

        // Then
        assertThat(filter).isNotNull();
        assertThat(filter).isInstanceOf(InMemoryOutboxFilter.class);
    }

    @Test
    void defaultOutboxFilter_worksCorrectly() {
        // Given
        CatboxClientAutoConfiguration config = new CatboxClientAutoConfiguration();
        OutboxFilter filter = config.outboxFilter();

        // When/Then
        assertThat(filter.deduped("test-id")).isFalse();
        assertThat(filter.deduped("test-id")).isTrue();
    }
}
