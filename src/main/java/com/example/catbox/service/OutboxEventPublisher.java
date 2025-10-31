package com.example.catbox.service;

import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * High-performance outbox poller that claims events using SELECT FOR UPDATE SKIP LOCKED.
 * Supports multi-node deployment with concurrent processing.
 */
@Service
public class OutboxEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventPublisher.class);
    private static final int BATCH_SIZE = 100;
    private static final int CLAIM_TIMEOUT_MINUTES = 5;

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventProcessor eventProcessor;
    private final ExecutorService executorService;

    public OutboxEventPublisher(OutboxEventRepository outboxEventRepository,
                               OutboxEventProcessor eventProcessor) {
        this.outboxEventRepository = outboxEventRepository;
        this.eventProcessor = eventProcessor;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Polls for pending events every 2 seconds.
     * Uses REQUIRES_NEW to claim events in a separate transaction with row-level locks.
     */
    @Scheduled(fixedDelay = 2000, initialDelay = 10000)
    public void pollAndProcessEvents() {
        // Claim events in a new transaction
        List<OutboxEvent> claimedEvents = claimEvents();
        
        if (!claimedEvents.isEmpty()) {
            logger.info("Claimed {} events for processing", claimedEvents.size());
            
            // Process each event in a separate thread with its own transaction
            for (OutboxEvent event : claimedEvents) {
                executorService.submit(() -> {
                    try {
                        eventProcessor.processEvent(event);
                    } catch (Exception e) {
                        logger.error("Unexpected error processing event {}", event.getId(), e);
                    }
                });
            }
        }
    }

    /**
     * Claims events using SELECT FOR UPDATE SKIP LOCKED.
     * This allows multiple nodes to process different events concurrently.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OutboxEvent> claimEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<OutboxEvent> events = outboxEventRepository.claimPendingEvents(now, BATCH_SIZE);
        
        // Update inProgressUntil to claim these events
        LocalDateTime claimUntil = now.plusMinutes(CLAIM_TIMEOUT_MINUTES);
        for (OutboxEvent event : events) {
            event.setInProgressUntil(claimUntil);
        }
        
        if (!events.isEmpty()) {
            outboxEventRepository.saveAll(events);
        }
        
        return events;
    }

    public List<OutboxEvent> getAllEvents() {
        return outboxEventRepository.findAllByOrderByCreatedAtAsc();
    }

    public List<OutboxEvent> getPendingEvents() {
        return outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
    }
}
