package com.example.catbox.server.service;

import com.example.catbox.server.config.DynamicKafkaTemplateFactory;
import com.example.catbox.server.config.OutboxRoutingConfig;
import com.example.catbox.server.config.OutboxProcessingConfig;
import com.example.catbox.server.metrics.OutboxMetricsService;
import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Publishes individual outbox events to Kafka using virtual threads.
 * Each event is published in its own transaction (REQUIRES_NEW).
 */
@Service
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final DynamicKafkaTemplateFactory kafkaTemplateFactory;
    private final OutboxRoutingConfig routingConfig;
    private final OutboxProcessingConfig processingConfig;
    private final OutboxMetricsService metricsService;

    /**
     * Publish a single event in a new transaction.
     * This method is called from a virtual thread.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishEvent(OutboxEvent event) {
        LocalDateTime claimTime = calculateEventClaimTime(event);
        
        try {
            // Publish to Kafka using our dynamic, routing factory
            publishToKafka(event);

            // Mark as sent
            event.setSentAt(LocalDateTime.now());
            event.setInProgressUntil(null); // Clear the claim
            outboxEventRepository.save(event);

            logger.info("Successfully published event: {} for aggregate: {}/{}",
                event.getEventType(), event.getAggregateType(), event.getAggregateId());

            // Record metrics
            metricsService.recordPublishSuccess();
            metricsService.recordProcessingDuration(claimTime);

        } catch (Exception e) {
            // Record failure metric first to ensure it's tracked even if logging fails
            metricsService.recordPublishFailure();
            
            logger.error(
                "Failed to publish event: {}. Will retry after ~{} ms when claim expires.",
                event.getId(), processingConfig.getClaimTimeoutMs(), e
            );
            
            // On failure, the transaction rolls back.
            // The 'inProgressUntil' lease remains, and the poller will retry when the claim timeout elapses.
        }
    }

    /**
     * Calculates when the event was originally claimed for processing.
     * Uses the inProgressUntil timestamp minus the claim timeout to determine the claim time.
     */
    private LocalDateTime calculateEventClaimTime(OutboxEvent event) {
        if (event.getInProgressUntil() != null) {
            return event.getInProgressUntil().minusNanos(processingConfig.getClaimTimeoutMs() * 1_000_000L);
        }
        return LocalDateTime.now();
    }

    /**
     * Publishes the event to the correct Kafka cluster based on routing rules.
     */
    private void publishToKafka(OutboxEvent event) {
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

        logger.debug("Publishing to cluster '{}' , topic '{}' , key '{}'", clusterKey, topic, key);

        // 3. Send the message
        try {
            template.send(topic, key, payload).get(); // .get() will throw if the send fails
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}
