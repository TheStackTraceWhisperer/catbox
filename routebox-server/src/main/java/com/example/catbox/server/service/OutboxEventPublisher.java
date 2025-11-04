package com.example.catbox.server.service;

import com.example.catbox.server.config.ClusterPublishingStrategy;
import com.example.catbox.server.config.DynamicKafkaTemplateFactory;
import com.example.catbox.server.config.OutboxRoutingConfig;
import com.example.catbox.server.config.OutboxProcessingConfig;
import com.example.catbox.server.config.RoutingRule;
import com.example.catbox.server.metrics.OutboxMetricsService;
import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Publishes individual outbox events to Kafka using virtual threads.
 * Each event is published in its own transaction (REQUIRES_NEW).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final DynamicKafkaTemplateFactory kafkaTemplateFactory;
    private final OutboxRoutingConfig routingConfig;
    private final OutboxProcessingConfig processingConfig;
    private final OutboxFailureHandler failureHandler;
    private final OutboxMetricsService metricsService;
    private final Tracer tracer;

    /**
     * Publish a single event in a new transaction.
     * This method is called from a virtual thread.
     */
    @Observed(name = "outbox.event.publish", contextualName = "publish-outbox-event")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishEvent(OutboxEvent event) {
        // Add correlationId to span if present
        if (event.getCorrelationId() != null && tracer.currentSpan() != null) {
            tracer.currentSpan().tag("correlation.id", event.getCorrelationId());
            tracer.currentSpan().tag("event.type", event.getEventType());
            tracer.currentSpan().tag("aggregate.type", event.getAggregateType());
            tracer.currentSpan().tag("aggregate.id", event.getAggregateId());
        }
        
        LocalDateTime claimTime = calculateEventClaimTime(event);
        
        try {
            // Publish to Kafka using our dynamic, routing factory
            publishToKafka(event);

            // Mark as sent
            event.setSentAt(LocalDateTime.now());
            event.setInProgressUntil(null); // Clear the claim
            failureHandler.resetFailureCount(event);
            outboxEventRepository.save(event);

            log.info("Successfully published event: {} for aggregate: {}/{}",
                event.getEventType(), event.getAggregateType(), event.getAggregateId());

            // Record metrics
            metricsService.recordPublishSuccess();
            metricsService.recordProcessingDuration(claimTime);

        } catch (Exception e) {
            // Record failure metric first to ensure it's tracked even if logging fails
            metricsService.recordPublishFailure();
            
            // FAILURE: Differentiate error type
            if (isPermanentFailure(e)) {
                // PERMANENT: Call the failure handler
                log.error(
                    "Permanent failure publishing event: {}. Recording failure. Error: {}",
                    event.getId(), e.getMessage()
                );
                failureHandler.recordPermanentFailure(event.getId(), e.getMessage());
            } else {
                // TRANSIENT: Log and let transaction roll back for later retry
                log.warn(
                    "Transient failure publishing event: {}. Will retry after ~{} ms. Error: {}",
                    event.getId(), processingConfig.getClaimTimeout().toMillis(), e.getMessage()
                );
            }
        }
    }

    /**
     * Calculates when the event was originally claimed for processing.
     * Uses the inProgressUntil timestamp minus the claim timeout to determine the claim time.
     */
    private LocalDateTime calculateEventClaimTime(OutboxEvent event) {
        if (event.getInProgressUntil() != null) {
            return event.getInProgressUntil().minus(processingConfig.getClaimTimeout());
        }
        return LocalDateTime.now();
    }

    /**
     * Publishes the event to the correct Kafka cluster(s) based on routing rules.
     * Supports multi-cluster publishing with different strategies.
     */
    @Observed(name = "outbox.kafka.publish", contextualName = "publish-to-kafka")
    private void publishToKafka(OutboxEvent event) throws Exception {
        // 1. Find the routing rule for this event
        RoutingRule rule = routingConfig.getRoutingRule(event.getEventType());
        if (rule == null) {
            // Fatal error: No route defined for this event type.
            throw new IllegalStateException("No Kafka route found for eventType: " + event.getEventType());
        }

        String topic = event.getEventType();
        String key = event.getAggregateId(); // Guarantees ordering per aggregate
        String payload = event.getPayload();

        // 2. Publish to all required clusters
        List<String> requiredClusters = rule.getClusters();
        List<String> optionalClusters = rule.getOptional();
        ClusterPublishingStrategy strategy = rule.getStrategy();

        log.debug("Publishing to {} required cluster(s) and {} optional cluster(s) with strategy: {}", 
                  requiredClusters.size(), optionalClusters.size(), strategy);

        // Track successes and failures for required clusters only
        int requiredSuccessCount = 0;
        Map<String, Exception> requiredFailures = new HashMap<>();
        Map<String, SendResult<String, String>> successfulResults = new HashMap<>();

        // 3. Publish to required clusters
        for (String clusterKey : requiredClusters) {
            try {
                SendResult<String, String> result = publishToCluster(clusterKey, topic, key, payload, event.getCorrelationId());
                successfulResults.put(clusterKey, result);
                requiredSuccessCount++;
                log.debug("Successfully published to required cluster: {}", clusterKey);
            } catch (Exception e) {
                requiredFailures.put(clusterKey, e);
                log.warn("Failed to publish to required cluster '{}': {}", clusterKey, e.getMessage());
            }
        }

        // 4. Publish to optional clusters (failures are ignored)
        // Note: We don't capture SendResult for optional clusters as they don't affect
        // success determination and metadata is captured from required clusters only
        for (String clusterKey : optionalClusters) {
            try {
                publishToCluster(clusterKey, topic, key, payload, event.getCorrelationId());
                log.debug("Successfully published to optional cluster: {}", clusterKey);
            } catch (Exception e) {
                log.warn("Failed to publish to optional cluster '{}' (ignored): {}", clusterKey, e.getMessage());
                // Optional cluster failures are ignored
            }
        }

        // 5. Evaluate success based on strategy
        boolean isSuccess = evaluatePublishingSuccess(
            strategy, 
            requiredClusters.size(), 
            requiredSuccessCount, 
            requiredFailures
        );

        if (!isSuccess) {
            // Throw the first exception to trigger retry logic
            String errorMsg = String.format(
                "Publishing failed according to strategy %s. Required clusters: %d, Successful: %d, Failed: %s", 
                strategy, requiredClusters.size(), requiredSuccessCount, requiredFailures.keySet()
            );
            
            // Throw the first failure's exception with enhanced message
            Exception firstFailure = requiredFailures.values().iterator().next();
            throw new Exception(errorMsg, firstFailure);
        }

        // 6. If successful, record the receipt from the *first* successful publish
        if (isSuccess && !successfulResults.isEmpty()) {
            SendResult<String, String> firstResult = successfulResults.values().iterator().next();
            var recordMetadata = firstResult.getRecordMetadata();
            
            event.setKafkaPartition(recordMetadata.partition());
            event.setKafkaOffset(recordMetadata.offset());
            event.setKafkaTimestamp(
                LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(recordMetadata.timestamp()),
                    java.time.ZoneId.systemDefault()
                )
            );
        }
    }

    /**
     * Publishes a message to a single cluster.
     */
    private SendResult<String, String> publishToCluster(String clusterKey, String topic, String key, String payload, String correlationId) 
            throws Exception {
        KafkaTemplate<String, String> template = kafkaTemplateFactory.getTemplate(clusterKey);
        
        log.debug("Publishing to cluster '{}', topic '{}', key '{}'", clusterKey, topic, key);

        try {
            var producerRecord = new org.apache.kafka.clients.producer.ProducerRecord<>(topic, key, payload);
            if (correlationId != null) {
                producerRecord.headers().add("correlationId", correlationId.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
            return template.send(producerRecord).get(); // .get() will throw if the send fails
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (ExecutionException e) {
            // Unwrap and throw the real, actionable Kafka exception
            throw (Exception) e.getCause();
        }
    }

    /**
     * Evaluates whether publishing was successful based on the strategy.
     * 
     * @param strategy The publishing strategy
     * @param totalRequired Number of required clusters
     * @param successCount Number of successful publishes to required clusters
     * @param failures Map of failed required cluster keys to exceptions
     * @return true if publishing meets the success criteria
     */
    private boolean evaluatePublishingSuccess(
            ClusterPublishingStrategy strategy,
            int totalRequired,
            int successCount,
            Map<String, Exception> failures) {
        
        switch (strategy) {
            case AT_LEAST_ONE:
                // Success if at least one required cluster succeeded
                return successCount > 0;
                
            case ALL_MUST_SUCCEED:
                // Success if all required clusters succeeded (no failures)
                return failures.isEmpty();
                
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategy);
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
