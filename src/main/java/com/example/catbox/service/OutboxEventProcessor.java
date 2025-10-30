package com.example.catbox.service;

import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Processes individual outbox events using virtual threads.
 * Each event is processed in its own transaction (REQUIRES_NEW).
 */
@Service
public class OutboxEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventProcessor.class);

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventProcessor(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    /**
     * Process a single event in a new transaction.
     * This method is called from a virtual thread.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEvent(OutboxEvent event) {
        try {
            // Publish to Kafka/message broker
            publishToKafka(event);
            
            // Mark as sent in a new transaction
            event.setSentAt(LocalDateTime.now());
            event.setInProgressUntil(null); // Clear the claim
            outboxEventRepository.save(event);
            
            logger.info("Successfully published event: {} for aggregate: {}/{}",
                event.getEventType(), event.getAggregateType(), event.getAggregateId());
                
        } catch (Exception e) {
            logger.error("Failed to publish event: {}. Will retry on next poll.", event.getId(), e);
            // Don't update sentAt - leave inProgressUntil to expire and retry
            // In case of Kafka send success but DB update failure, 
            // at-least-once delivery with idempotent consumers handles duplicates
        }
    }

    /**
     * Simulate publishing to Kafka.
     * In production, replace with actual Kafka producer call.
     */
    private void publishToKafka(OutboxEvent event) {
        // Example: kafkaTemplate.send(topic, event.getPayload()).get();
        logger.debug("Publishing to Kafka: type={}, aggregateId={}, payload={}",
            event.getEventType(), event.getAggregateId(), event.getPayload());
        
        // Simulate network call (for demo purposes only)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka send interrupted", e);
        }
    }
}
