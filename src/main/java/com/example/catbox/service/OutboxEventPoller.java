package com.example.catbox.service;

import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.config.OutboxProcessingConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Polls and claims pending outbox events, then delegates to the publisher.
 */
@Service
@RequiredArgsConstructor
public class OutboxEventPoller {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventPoller.class);

    private final OutboxEventClaimer claimer;
    private final OutboxEventPublisher publisher;
    private final OutboxProcessingConfig processingConfig;

    /**
     * Polls for pending events.
     */
    @Scheduled(
        fixedDelayString = "${outbox.processing.poll-fixed-delay-ms:2000}",
        initialDelayString = "${outbox.processing.poll-initial-delay-ms:10000}"
    )
    public void pollAndPublish() {
        List<OutboxEvent> claimedEvents = claimer.claimEvents();
        if (!claimedEvents.isEmpty()) {
            logger.info("Claimed {} events for publishing", claimedEvents.size());

            // Publish each event in a virtual thread
            for (OutboxEvent event : claimedEvents) {
                Thread.ofVirtual().start(() -> {
                    try {
                        publisher.publishEvent(event);
                    } catch (Exception e) {
                        logger.error("Unexpected error publishing event {}", event.getId(), e);
                    }
                });
            }
        }
    }
}

