package com.example.catbox.server.service;

import com.example.catbox.server.entity.OutboxArchiveEvent;
import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.server.repository.OutboxArchiveEventRepository;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.catbox.server.config.OutboxProcessingConfig;
import com.example.catbox.server.metrics.OutboxMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for archiving old sent outbox events to prevent unbounded table growth.
 * Events that have been successfully sent and are older than the configured retention period
 * are moved to the archive table.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxArchivalService {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxArchiveEventRepository archiveEventRepository;
    private final OutboxProcessingConfig processingConfig;
    private final OutboxMetricsService metricsService;

    /**
     * Archives old sent events. Runs daily at 2 AM by default.
     */
    @Scheduled(cron = "${outbox.archival.schedule:0 0 2 * * *}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void archiveOldEvents() {
        int retentionDays = processingConfig.getArchivalRetentionDays();
        if (retentionDays <= 0) {
            log.debug("Archival is disabled (retention days: {})", retentionDays);
            return;
        }

        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
        List<OutboxEvent> eventsToArchive = outboxEventRepository.findSentEventsBefore(cutoffTime);

        if (eventsToArchive.isEmpty()) {
            log.debug("No events to archive before {}", cutoffTime);
            return;
        }

        log.info("Archiving {} events sent before {}", eventsToArchive.size(), cutoffTime);

        // Move events to archive table in batch
        List<OutboxArchiveEvent> archiveEvents = eventsToArchive.stream()
                .map(OutboxArchiveEvent::new)
                .toList();
        archiveEventRepository.saveAll(archiveEvents);

        // Delete archived events from the main table
        outboxEventRepository.deleteAll(eventsToArchive);

        // Record metrics
        metricsService.recordArchival(eventsToArchive.size());

        log.info("Successfully archived {} events", eventsToArchive.size());
    }

    /**
     * Manually trigger archival (for testing or administrative purposes).
     * @param retentionDays Number of days to retain sent events before archiving
     * @return Number of events archived
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int manualArchive(int retentionDays) {
        if (retentionDays <= 0) {
            log.warn("Invalid retention days: {}. Must be > 0", retentionDays);
            return 0;
        }

        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
        List<OutboxEvent> eventsToArchive = outboxEventRepository.findSentEventsBefore(cutoffTime);

        if (eventsToArchive.isEmpty()) {
            return 0;
        }

        log.info("Manual archival: archiving {} events sent before {}", eventsToArchive.size(), cutoffTime);

        // Move events to archive table in batch
        List<OutboxArchiveEvent> archiveEvents = eventsToArchive.stream()
                .map(OutboxArchiveEvent::new)
                .toList();
        archiveEventRepository.saveAll(archiveEvents);

        outboxEventRepository.deleteAll(eventsToArchive);

        // Record metrics
        metricsService.recordArchival(eventsToArchive.size());

        log.info("Manual archival completed: {} events archived", eventsToArchive.size());
        return eventsToArchive.size();
    }
}
