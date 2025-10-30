package com.example.catbox.service;

import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventPublisher(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    /**
     * Scheduled task to process pending outbox events.
     * Runs every 5 seconds to publish events to external systems.
     */
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc("PENDING");
        
        if (!pendingEvents.isEmpty()) {
            logger.info("Processing {} pending outbox events", pendingEvents.size());
        }
        
        for (OutboxEvent event : pendingEvents) {
            try {
                // Simulate publishing to external message broker (Kafka, RabbitMQ, etc.)
                publishEvent(event);
                
                // Mark as processed
                event.setStatus("PROCESSED");
                event.setProcessedAt(LocalDateTime.now());
                outboxEventRepository.save(event);
                
                logger.info("Successfully published event: {} for aggregate: {}/{}",
                    event.getEventType(), event.getAggregateType(), event.getAggregateId());
                
            } catch (Exception e) {
                logger.error("Failed to publish event: {}", event.getId(), e);
                event.setStatus("FAILED");
                outboxEventRepository.save(event);
            }
        }
    }

    /**
     * Simulate publishing event to external system.
     * In a real implementation, this would publish to Kafka, RabbitMQ, etc.
     */
    private void publishEvent(OutboxEvent event) {
        // This is where you would integrate with your message broker
        // For example: kafkaTemplate.send(topic, event.getPayload());
        logger.debug("Publishing event: type={}, aggregateId={}, payload={}",
            event.getEventType(), event.getAggregateId(), event.getPayload());
        
        // Simulate some processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public List<OutboxEvent> getAllEvents() {
        return outboxEventRepository.findAll();
    }

    public List<OutboxEvent> getPendingEvents() {
        return outboxEventRepository.findByStatusOrderByCreatedAtAsc("PENDING");
    }
}
