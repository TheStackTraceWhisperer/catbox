package com.example.catbox.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for InMemoryOutboxFilter implementation.
 */
class InMemoryOutboxFilterTest {

    private static final String GROUP_A = "consumer-group-a";
    private static final String GROUP_B = "consumer-group-b";

    private InMemoryOutboxFilter filter;

    @BeforeEach
    void setUp() {
        filter = new InMemoryOutboxFilter();
    }

    @Test
    void deduped_firstCall_returnsFalse() {
        // When
        boolean isDuplicate = filter.deduped("correlation-id-1", GROUP_A);

        // Then
        assertThat(isDuplicate).isFalse();
    }

    @Test
    void deduped_secondCall_returnsTrue() {
        // Given
        filter.deduped("correlation-id-1", GROUP_A);

        // When
        boolean isDuplicate = filter.deduped("correlation-id-1", GROUP_A);

        // Then
        assertThat(isDuplicate).isTrue();
    }

    @Test
    void deduped_differentGroups_returnsFalseForEach() {
        // When
        boolean isDuplicateA = filter.deduped("correlation-id-1", GROUP_A);
        boolean isDuplicateB = filter.deduped("correlation-id-1", GROUP_B);

        // Then - Same correlation ID but different groups
        assertThat(isDuplicateA).isFalse();
        assertThat(isDuplicateB).isFalse();
    }

    @Test
    void deduped_differentIds_returnsFalseForEach() {
        // When
        boolean isDuplicate1 = filter.deduped("correlation-id-1", GROUP_A);
        boolean isDuplicate2 = filter.deduped("correlation-id-2", GROUP_A);
        boolean isDuplicate3 = filter.deduped("correlation-id-3", GROUP_A);

        // Then
        assertThat(isDuplicate1).isFalse();
        assertThat(isDuplicate2).isFalse();
        assertThat(isDuplicate3).isFalse();
    }

    @Test
    void deduped_nullCorrelationId_returnsFalse() {
        // When
        boolean isDuplicate = filter.deduped(null, GROUP_A);

        // Then
        assertThat(isDuplicate).isFalse();
    }

    @Test
    void deduped_emptyCorrelationId_returnsFalse() {
        // When
        boolean isDuplicate = filter.deduped("", GROUP_A);

        // Then
        assertThat(isDuplicate).isFalse();
    }

    @Test
    void deduped_nullConsumerGroup_returnsFalse() {
        // When
        boolean isDuplicate = filter.deduped("correlation-id-1", null);

        // Then
        assertThat(isDuplicate).isFalse();
    }

    @Test
    void deduped_multipleCalls_tracksCorrectly() {
        // When/Then
        assertThat(filter.deduped("id-1", GROUP_A)).isFalse();  // First time
        assertThat(filter.deduped("id-2", GROUP_A)).isFalse();  // First time
        assertThat(filter.deduped("id-1", GROUP_A)).isTrue();   // Duplicate
        assertThat(filter.deduped("id-3", GROUP_A)).isFalse();  // First time
        assertThat(filter.deduped("id-2", GROUP_A)).isTrue();   // Duplicate
        assertThat(filter.deduped("id-1", GROUP_A)).isTrue();   // Duplicate
    }

    @Test
    void markProcessed_marksIdAsProcessed() {
        // When
        filter.markProcessed("correlation-id-1", GROUP_A);

        // Then
        assertThat(filter.deduped("correlation-id-1", GROUP_A)).isTrue();
    }

    @Test
    void markProcessed_nullId_handlesGracefully() {
        // When/Then - Should not throw
        filter.markProcessed(null, GROUP_A);
        assertThat(filter.size()).isEqualTo(0);
    }

    @Test
    void markProcessed_emptyId_handlesGracefully() {
        // When/Then - Should not throw
        filter.markProcessed("", GROUP_A);
        assertThat(filter.size()).isEqualTo(0);
    }

    @Test
    void isProcessed_returnsTrueForProcessedId() {
        // Given
        filter.deduped("correlation-id-1", GROUP_A);

        // When
        boolean isProcessed = filter.isProcessed("correlation-id-1", GROUP_A);

        // Then
        assertThat(isProcessed).isTrue();
    }

    @Test
    void isProcessed_returnsFalseForUnprocessedId() {
        // When
        boolean isProcessed = filter.isProcessed("correlation-id-1", GROUP_A);

        // Then
        assertThat(isProcessed).isFalse();
    }

    @Test
    void isProcessed_doesNotMarkAsProcessed() {
        // When
        filter.isProcessed("correlation-id-1", GROUP_A);

        // Then
        assertThat(filter.deduped("correlation-id-1", GROUP_A)).isFalse();
    }

    @Test
    void isProcessed_nullId_returnsFalse() {
        // When
        boolean isProcessed = filter.isProcessed(null, GROUP_A);

        // Then
        assertThat(isProcessed).isFalse();
    }

    @Test
    void isProcessed_emptyId_returnsFalse() {
        // When
        boolean isProcessed = filter.isProcessed("", GROUP_A);

        // Then
        assertThat(isProcessed).isFalse();
    }

    @Test
    void markUnprocessed_removesProcessedId() {
        // Given
        filter.deduped("correlation-id-1", GROUP_A);
        assertThat(filter.isProcessed("correlation-id-1", GROUP_A)).isTrue();

        // When
        filter.markUnprocessed("correlation-id-1", GROUP_A);

        // Then
        assertThat(filter.isProcessed("correlation-id-1", GROUP_A)).isFalse();
        assertThat(filter.deduped("correlation-id-1", GROUP_A)).isFalse();
    }

    @Test
    void clear_removesAllProcessedIds() {
        // Given
        filter.deduped("correlation-id-1", GROUP_A);
        filter.deduped("correlation-id-2", GROUP_A);
        filter.deduped("correlation-id-3", GROUP_B);
        assertThat(filter.size()).isEqualTo(3);

        // When
        filter.clear();

        // Then
        assertThat(filter.size()).isEqualTo(0);
        assertThat(filter.deduped("correlation-id-1", GROUP_A)).isFalse();
        assertThat(filter.deduped("correlation-id-2", GROUP_A)).isFalse();
    }

    @Test
    void clearGroup_removesOnlyGroupProcessedIds() {
        // Given
        filter.deduped("correlation-id-1", GROUP_A);
        filter.deduped("correlation-id-2", GROUP_A);
        filter.deduped("correlation-id-3", GROUP_B);
        assertThat(filter.size()).isEqualTo(3);

        // When
        filter.clear(GROUP_A);

        // Then
        assertThat(filter.size()).isEqualTo(1);
        assertThat(filter.deduped("correlation-id-1", GROUP_A)).isFalse();
        assertThat(filter.isProcessed("correlation-id-3", GROUP_B)).isTrue();
    }

    @Test
    void size_returnsCorrectCount() {
        // When/Then
        assertThat(filter.size()).isEqualTo(0);

        filter.deduped("id-1", GROUP_A);
        assertThat(filter.size()).isEqualTo(1);

        filter.deduped("id-2", GROUP_A);
        assertThat(filter.size()).isEqualTo(2);

        filter.deduped("id-1", GROUP_A); // Duplicate, should not increase
        assertThat(filter.size()).isEqualTo(2);

        filter.markProcessed("id-3", GROUP_A);
        assertThat(filter.size()).isEqualTo(3);

        filter.deduped("id-1", GROUP_B); // Same ID, different group
        assertThat(filter.size()).isEqualTo(4);
    }

    @Test
    void sizeByGroup_returnsCorrectCount() {
        // When/Then
        filter.deduped("id-1", GROUP_A);
        filter.deduped("id-2", GROUP_A);
        filter.deduped("id-3", GROUP_B);

        assertThat(filter.size(GROUP_A)).isEqualTo(2);
        assertThat(filter.size(GROUP_B)).isEqualTo(1);
        assertThat(filter.size("non-existent")).isEqualTo(0);
    }

    @Test
    void concurrentAccess_handlesSafely() throws InterruptedException {
        // Given
        int threadCount = 10;
        int iterationsPerThread = 100;
        String correlationId = "concurrent-test-id";

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger duplicateCount = new AtomicInteger(0);

        // When - Multiple threads try to process the same correlation ID
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        if (filter.deduped(correlationId, GROUP_A)) {
                            duplicateCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - Only one thread should have gotten false
        int totalCalls = threadCount * iterationsPerThread;
        assertThat(duplicateCount.get()).isEqualTo(totalCalls - 1);
        assertThat(filter.size(GROUP_A)).isEqualTo(1);
    }

    @Test
    void concurrentAccess_differentIds_allProcessedOnce()
            throws InterruptedException {
        // Given
        int threadCount = 10;
        int uniqueIdsPerThread = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger duplicateCount = new AtomicInteger(0);

        // When - Multiple threads process different correlation IDs
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < uniqueIdsPerThread; j++) {
                        String id = "thread-" + threadId + "-id-" + j;
                        if (filter.deduped(id, GROUP_A)) {
                            duplicateCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all threads to complete
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Then - No duplicates should be found since all IDs are unique
        assertThat(duplicateCount.get()).isEqualTo(0);
        assertThat(filter.size(GROUP_A))
                .isEqualTo(threadCount * uniqueIdsPerThread);
    }
}
