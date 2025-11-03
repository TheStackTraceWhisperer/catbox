package com.example.catbox.client;

import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link OutboxFilter} that tracks processed correlation IDs
 * using a thread-safe concurrent set.
 * 
 * <p>This implementation stores correlation IDs in memory, which means:
 * <ul>
 *   <li>Fast lookups with O(1) complexity</li>
 *   <li>State is lost on application restart</li>
 *   <li>Memory usage grows with the number of unique correlation IDs</li>
 * </ul>
 * 
 * <p>For production use cases where state must survive restarts, consider using
 * a persistent implementation backed by a database or distributed cache.
 */
@Slf4j
public class InMemoryOutboxFilter implements OutboxFilter {
    
    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();
    
    @Override
    public boolean deduped(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            log.warn("Received null or empty correlationId, treating as not processed");
            return false;
        }
        
        // add() returns false if element already existed
        boolean isNew = processedIds.add(correlationId);
        
        if (!isNew) {
            log.debug("Duplicate detected for correlationId: {}", correlationId);
        } else {
            log.trace("First time processing correlationId: {}", correlationId);
        }
        
        // Return true if it was already processed (i.e., not new)
        return !isNew;
    }
    
    @Override
    public void markProcessed(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            log.warn("Attempted to mark null or empty correlationId as processed");
            return;
        }
        
        processedIds.add(correlationId);
        log.trace("Marked correlationId as processed: {}", correlationId);
    }
    
    @Override
    public boolean isProcessed(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            return false;
        }
        
        return processedIds.contains(correlationId);
    }
    
    /**
     * Clears all tracked correlation IDs. Useful for testing or when implementing
     * a time-based cleanup strategy.
     */
    public void clear() {
        processedIds.clear();
        log.debug("Cleared all processed correlation IDs");
    }
    
    /**
     * Returns the number of correlation IDs currently being tracked.
     * 
     * @return the count of processed correlation IDs
     */
    public int size() {
        return processedIds.size();
    }
}
