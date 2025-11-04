package com.example.routebox.server.service;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.server.config.OutboxProcessingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Polls and claims pending outbox events, then delegates to the publisher.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventPoller {

    private final OutboxEventClaimer claimer;
    private final OutboxEventPublisher publisher;
    private final OutboxProcessingConfig processingConfig;

    /**
     * Polls for pending events.
     */
    @Scheduled(
        fixedDelayString = "${outbox.processing.poll-fixed-delay:2s}",
        initialDelayString = "${outbox.processing.poll-initial-delay:10s}"
    )
    public void pollAndPublish() {
        List<OutboxEvent> claimedEvents = claimer.claimEvents();
        if (!claimedEvents.isEmpty()) {
            log.info("Claimed {} events for publishing", claimedEvents.size());

            // Publish each event in a virtual thread
            for (OutboxEvent event : claimedEvents) {
                Thread.ofVirtual().start(() -> {
                    try {
                        publisher.publishEvent(event);
                    } catch (Exception e) {
                        log.error("Unexpected error publishing event {}", event.getId(), e);
                    }
                });
            }
        }
    }
}

