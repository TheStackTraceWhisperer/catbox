package com.example.catbox.client;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link OutboxFilter} that tracks processed
 * correlation IDs per consumer group using thread-safe concurrent sets.
 *
 * <p>This implementation stores correlation IDs in memory, which means:
 * <ul>
 *   <li>Fast lookups with O(1) complexity</li>
 *   <li>State is lost on application restart</li>
 *   <li>Memory usage grows with the number of unique correlation IDs</li>
 *   <li>Not suitable for multi-instance deployments</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This implementation is primarily for testing
 * and development. For production use, consider using
 * {@link DatabaseOutboxFilter} which provides persistence and multi-instance
 * support.
 *
 * @deprecated Use {@link DatabaseOutboxFilter} for production deployments
 */
@Slf4j
@Deprecated
public final class InMemoryOutboxFilter implements OutboxFilter {

    /**
     * Map of consumer groups to their processed correlation IDs.
     */
    private final Map<String, Set<String>> processedIdsByGroup =
            new ConcurrentHashMap<>();

    @Override
    public boolean deduped(final String correlationId,
                          final String consumerGroup) {
        if (correlationId == null || correlationId.isEmpty()) {
            log.warn("Received null or empty correlationId, "
                    + "treating as not processed");
            return false;
        }

        if (consumerGroup == null || consumerGroup.isEmpty()) {
            log.warn("Received null or empty consumerGroup, "
                    + "treating as not processed");
            return false;
        }

        Set<String> processedIds = processedIdsByGroup.computeIfAbsent(
                consumerGroup, k -> ConcurrentHashMap.newKeySet());

        // add() returns false if element already existed
        boolean isNew = processedIds.add(correlationId);

        if (!isNew) {
            log.debug("Duplicate detected for correlationId: {} "
                    + "in consumerGroup: {}", correlationId, consumerGroup);
        } else {
            log.trace("First time processing correlationId: {} "
                    + "in consumerGroup: {}", correlationId, consumerGroup);
        }

        // Return true if it was already processed (i.e., not new)
        return !isNew;
    }

    @Override
    public void markProcessed(final String correlationId,
                             final String consumerGroup) {
        if (correlationId == null || correlationId.isEmpty()) {
            log.warn("Attempted to mark null or empty correlationId "
                    + "as processed");
            return;
        }

        if (consumerGroup == null || consumerGroup.isEmpty()) {
            log.warn("Attempted to mark with null or empty consumerGroup");
            return;
        }

        Set<String> processedIds = processedIdsByGroup.computeIfAbsent(
                consumerGroup, k -> ConcurrentHashMap.newKeySet());

        processedIds.add(correlationId);
        log.trace("Marked correlationId: {} as processed "
                + "in consumerGroup: {}", correlationId, consumerGroup);
    }

    @Override
    public boolean isProcessed(final String correlationId,
                              final String consumerGroup) {
        if (correlationId == null || correlationId.isEmpty()) {
            return false;
        }

        if (consumerGroup == null || consumerGroup.isEmpty()) {
            return false;
        }

        Set<String> processedIds = processedIdsByGroup.get(consumerGroup);
        return processedIds != null && processedIds.contains(correlationId);
    }

    @Override
    public void markUnprocessed(final String correlationId,
                               final String consumerGroup) {
        if (correlationId == null || correlationId.isEmpty()) {
            log.warn("Attempted to mark null or empty correlationId "
                    + "as unprocessed");
            return;
        }

        if (consumerGroup == null || consumerGroup.isEmpty()) {
            log.warn("Attempted to unmark with null or empty consumerGroup");
            return;
        }

        Set<String> processedIds = processedIdsByGroup.get(consumerGroup);
        if (processedIds != null) {
            processedIds.remove(correlationId);
            log.trace("Marked correlationId: {} as unprocessed "
                    + "in consumerGroup: {}", correlationId, consumerGroup);
        }
    }

    /**
     * Clears all tracked correlation IDs for all consumer groups.
     * Useful for testing.
     */
    public void clear() {
        processedIdsByGroup.clear();
        log.debug("Cleared all processed correlation IDs");
    }

    /**
     * Clears tracked correlation IDs for a specific consumer group.
     * Useful for testing.
     *
     * @param consumerGroup the consumer group to clear
     */
    public void clear(final String consumerGroup) {
        processedIdsByGroup.remove(consumerGroup);
        log.debug("Cleared processed correlation IDs for consumerGroup: {}",
                consumerGroup);
    }

    /**
     * Returns the total number of correlation IDs tracked across all
     * consumer groups.
     *
     * @return the total count of processed correlation IDs
     */
    public int size() {
        return processedIdsByGroup.values().stream()
                .mapToInt(Set::size)
                .sum();
    }

    /**
     * Returns the number of correlation IDs tracked for a specific
     * consumer group.
     *
     * @param consumerGroup the consumer group
     * @return the count of processed correlation IDs for the group
     */
    public int size(final String consumerGroup) {
        Set<String> processedIds = processedIdsByGroup.get(consumerGroup);
        return processedIds != null ? processedIds.size() : 0;
    }
}
