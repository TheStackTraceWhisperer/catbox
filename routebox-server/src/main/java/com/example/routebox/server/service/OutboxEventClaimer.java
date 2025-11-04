package com.example.routebox.server.service;

import com.example.routebox.server.config.OutboxProcessingConfig;
import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Claims pending outbox events for processing by the poller/publisher pipeline.
 * Works with OutboxEventPoller which delegates each claimed event to OutboxEventPublisher.
 */
@Service
@RequiredArgsConstructor
public class OutboxEventClaimer {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxProcessingConfig processingConfig;

    /**
     * Claims events using SELECT TOP ... WITH (UPDLOCK, READPAST, ROWLOCK) in a new transaction.
     */
    @Observed(name = "outbox.event.claim", contextualName = "claim-outbox-events")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OutboxEvent> claimEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<OutboxEvent> events = outboxEventRepository.claimPendingEvents(now, processingConfig.getBatchSize());

        // Set claim lease using Duration
        LocalDateTime claimUntil = now.plus(processingConfig.getClaimTimeout());
        for (OutboxEvent event : events) {
            event.setInProgressUntil(claimUntil);
        }

        if (!events.isEmpty()) {
            outboxEventRepository.saveAll(events);
        }

        return events;
    }
}
