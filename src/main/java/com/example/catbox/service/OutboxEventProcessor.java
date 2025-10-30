package com.example.catbox.service;

import com.example.catbox.config.DynamicKafkaTemplateFactory;
import com.example.catbox.config.OutboxRoutingConfig;
import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Processes individual outbox events using virtual threads.
 * Each event is processed in its own transaction (REQUIRES_NEW).
 */
@Service
@RequiredArgsConstructor
public class OutboxEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventProcessor.class);

    private final OutboxEventRepository outboxEventRepository;
    private final DynamicKafkaTemplateFactory kafkaTemplateFactory;
    private final OutboxRoutingConfig routingConfig;

    /**
     * Process a single event in a new transaction.
     * This method is called from a virtual thread.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEvent(OutboxEvent event) {
        try {
            // Publish to Kafka using our dynamic, routing factory
            publishToKafka(event);
            
            // Mark as sent
            event.setSentAt(LocalDateTime.now());
            event.setInProgressUntil(null); // Clear the claim
            outboxEventRepository.save(event);
            
            logger.info("Successfully published event: {} for aggregate: {}/{}",
                event.getEventType(), event.getAggregateType(), event.getAggregateId());
                
        } catch (Exception e) {
            logger.error("Failed to publish event: {}. Will retry on next poll.", event.getId(), e);
            // On failure, the transaction rolls back.
            // The 'inProgressUntil' lease remains, and the poller will retry
            // after the lease expires.
        }
    }

    /**
     * Publishes the event to the correct Kafka cluster based on routing rules.
     */
    private void publishToKafka(OutboxEvent event) throws Exception {
        // 1. Find the route for this event
        String clusterKey = routingConfig.getRules().get(event.getEventType());
        if (clusterKey == null) {
            // Fatal error: No route defined for this event type.
            // Throwing an exception will cause a retry, giving time to fix config.
            throw new IllegalStateException("No Kafka route found for eventType: " + event.getEventType());
        }

        // 2. Get the dynamic KafkaTemplate
        KafkaTemplate<String, String> template = kafkaTemplateFactory.getTemplate(clusterKey);

        String topic = event.getEventType();
        String key = event.getAggregateId(); // Guarantees ordering per aggregate
        String payload = event.getPayload();

        logger.debug("Publishing to cluster '{}', topic '{}', key '{}'", clusterKey, topic, key);

        // 3. Send the message
        // We use .get() to make the send synchronous and blocking.
        // This is ideal here because we are on a virtual thread and
        // we *want* to block until we get a success/failure response.
        template.send(topic, key, payload).get(); // .get() will throw if the send fails
    }
}
