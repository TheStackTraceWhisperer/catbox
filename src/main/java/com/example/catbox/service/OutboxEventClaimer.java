package com.example.catbox.service;

import com.example.catbox.config.OutboxProcessingConfig;
import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OutboxEventRepository;
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
     * Claims events using SELECT FOR UPDATE SKIP LOCKED in a new transaction.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OutboxEvent> claimEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<OutboxEvent> events = outboxEventRepository.claimPendingEvents(now, processingConfig.getBatchSize());

        // Set claim lease using millisecond resolution
        LocalDateTime claimUntil = now.plusNanos(processingConfig.getClaimTimeoutMs() * 1_000_000L);
        for (OutboxEvent event : events) {
            event.setInProgressUntil(claimUntil);
        }

        if (!events.isEmpty()) {
            outboxEventRepository.saveAll(events);
        }

        return events;
    }
}
