package com.example.orderprocessor.service;

import com.example.orderprocessor.model.OrderCreatedPayload;
import com.example.orderprocessor.model.OrderStatusChangedPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service that processes order events with simulated business logic.
 * 
 * <p>This service demonstrates:
 * <ul>
 *   <li>Happy path processing (successful business logic execution)</li>
 *   <li>Duplicate message handling (via OutboxFilter in the listener)</li>
 *   <li>Intermittent failures (simulated for testing retry scenarios)</li>
 * </ul>
 */
@Slf4j
@Service
public class OrderEventProcessingService {

    private final AtomicInteger processedOrderCreatedCount = new AtomicInteger(0);
    private final AtomicInteger processedOrderStatusChangedCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final Random random = new Random();

    /**
     * Process an OrderCreated event.
     * 
     * <p>Simulates:
     * <ul>
     *   <li>10% chance of intermittent failure (for retry testing)</li>
     *   <li>Otherwise processes successfully with simulated business logic</li>
     * </ul>
     * 
     * @param payload the order created event payload
     * @param correlationId the correlation ID for tracking
     * @throws ProcessingException if processing fails (intermittent failure simulation)
     */
    public void processOrderCreated(OrderCreatedPayload payload, String correlationId) {
        log.info("Processing OrderCreated event - correlationId: {}, orderId: {}, customer: {}", 
            correlationId, payload.orderId(), payload.customerName());

        // Simulate intermittent failures (10% chance)
        if (shouldSimulateFailure()) {
            failureCount.incrementAndGet();
            log.warn("Simulated intermittent failure for OrderCreated - correlationId: {}", correlationId);
            throw new ProcessingException("Simulated intermittent failure for testing");
        }

        // Simulate business logic processing
        simulateProcessing(payload);
        
        processedOrderCreatedCount.incrementAndGet();
        log.info("Successfully processed OrderCreated - correlationId: {}, orderId: {}", 
            correlationId, payload.orderId());
    }

    /**
     * Process an OrderStatusChanged event.
     * 
     * <p>Simulates:
     * <ul>
     *   <li>10% chance of intermittent failure (for retry testing)</li>
     *   <li>Otherwise processes successfully with simulated business logic</li>
     * </ul>
     * 
     * @param payload the order status changed event payload
     * @param correlationId the correlation ID for tracking
     * @throws ProcessingException if processing fails (intermittent failure simulation)
     */
    public void processOrderStatusChanged(OrderStatusChangedPayload payload, String correlationId) {
        log.info("Processing OrderStatusChanged event - correlationId: {}, orderId: {}, {} -> {}", 
            correlationId, payload.orderId(), payload.oldStatus(), payload.newStatus());

        // Simulate intermittent failures (10% chance)
        if (shouldSimulateFailure()) {
            failureCount.incrementAndGet();
            log.warn("Simulated intermittent failure for OrderStatusChanged - correlationId: {}", correlationId);
            throw new ProcessingException("Simulated intermittent failure for testing");
        }

        // Simulate business logic processing
        simulateProcessing(payload);
        
        processedOrderStatusChangedCount.incrementAndGet();
        log.info("Successfully processed OrderStatusChanged - correlationId: {}, orderId: {}", 
            correlationId, payload.orderId());
    }

    /**
     * Simulate business logic processing with a small delay.
     */
    private void simulateProcessing(Object payload) {
        try {
            // Simulate some processing time (10-50ms)
            Thread.sleep(10 + random.nextInt(40));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessingException("Processing interrupted", e);
        }
    }

    /**
     * Determine if we should simulate a failure (10% chance).
     */
    private boolean shouldSimulateFailure() {
        return random.nextInt(10) == 0;
    }

    /**
     * Get the count of successfully processed OrderCreated events.
     */
    public int getProcessedOrderCreatedCount() {
        return processedOrderCreatedCount.get();
    }

    /**
     * Get the count of successfully processed OrderStatusChanged events.
     */
    public int getProcessedOrderStatusChangedCount() {
        return processedOrderStatusChangedCount.get();
    }

    /**
     * Get the count of simulated failures.
     */
    public int getFailureCount() {
        return failureCount.get();
    }

    /**
     * Reset all counters (useful for testing).
     */
    public void resetCounters() {
        processedOrderCreatedCount.set(0);
        processedOrderStatusChangedCount.set(0);
        failureCount.set(0);
    }

    /**
     * Exception thrown when processing fails.
     */
    public static class ProcessingException extends RuntimeException {
        public ProcessingException(String message) {
            super(message);
        }

        public ProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
