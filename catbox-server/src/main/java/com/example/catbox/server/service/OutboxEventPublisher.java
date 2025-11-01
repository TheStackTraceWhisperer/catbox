package com.example.catbox.server.service;

import com.example.catbox.server.config.DynamicKafkaTemplateFactory;
import com.example.catbox.server.config.OutboxRoutingConfig;
import com.example.catbox.server.config.OutboxProcessingConfig;
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
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
    private final OutboxFailureHandler failureHandler;

    /**
     * Publish a single event in a new transaction.
     * This method is called from a virtual thread.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishEvent(OutboxEvent event) {
        try {
            // Publish to Kafka using our dynamic, routing factory
            publishToKafka(event);

            // Mark as sent
            event.setSentAt(LocalDateTime.now());
            event.setInProgressUntil(null); // Clear the claim
            failureHandler.resetFailureCount(event);
            outboxEventRepository.save(event);

            logger.info("Successfully published event: {} for aggregate: {}/{}",
                event.getEventType(), event.getAggregateType(), event.getAggregateId());

        } catch (Exception e) {
            // FAILURE: Differentiate error type
            if (isPermanentFailure(e)) {
                // PERMANENT: Call the failure handler
                logger.error(
                    "Permanent failure publishing event: {}. Recording failure. Error: {}",
                    event.getId(), e.getMessage()
                );
                failureHandler.recordPermanentFailure(event.getId(), e.getMessage());
            } else {
                // TRANSIENT: Log and let transaction roll back for later retry
                logger.warn(
                    "Transient failure publishing event: {}. Will retry after ~{} ms. Error: {}",
                    event.getId(), processingConfig.getClaimTimeoutMs(), e.getMessage()
                );
            }
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (ExecutionException e) {
            // Unwrap and throw the real, actionable Kafka exception
            throw (Exception) e.getCause();
        }
    }

    /**
     * Classifies exceptions based on the configurable list.
     * This method recursively checks the exception and its causes.
     */
    private boolean isPermanentFailure(Throwable e) {
        final Set<String> permanentErrors = processingConfig.getPermanentExceptionSet();
        
        Throwable current = e;
        while (current != null) {
            String exceptionName = current.getClass().getName();
            if (permanentErrors.contains(exceptionName)) {
                return true;
            }
            // Move to the cause, but stop if we hit a circular reference
            current = (current.getCause() == current) ? null : current.getCause();
        }
        
        // No match found in the exception chain
        return false;
    }
}
