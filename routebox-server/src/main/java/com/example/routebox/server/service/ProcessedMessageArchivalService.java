package com.example.routebox.server.service;

import com.example.catbox.common.entity.ProcessedMessage;
import com.example.catbox.common.entity.ProcessedMessageArchive;
import com.example.catbox.common.repository.ProcessedMessageArchiveRepository;
import com.example.catbox.common.repository.ProcessedMessageRepository;
import com.example.catbox.server.config.OutboxProcessingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for archiving old processed messages to prevent
 * unbounded table growth. Messages that have been processed and are older
 * than the configured retention period are moved to the archive table.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedMessageArchivalService {

    private final ProcessedMessageRepository processedMessageRepository;
    private final ProcessedMessageArchiveRepository archiveRepository;
    private final OutboxProcessingConfig processingConfig;

    /**
     * Archives old processed messages. Runs daily at 3 AM by default.
     */
    @Scheduled(cron = "${outbox.processed-messages.archival.schedule:0 0 3 * * *}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void archiveOldMessages() {
        int retentionDays = processingConfig.getArchivalRetentionDays();
        if (retentionDays <= 0) {
            log.debug("Processed message archival is disabled "
                    + "(retention days: {})", retentionDays);
            return;
        }

        LocalDateTime cutoffTime = LocalDateTime.now()
                .minusDays(retentionDays);

        int archived = processedMessageRepository
                .deleteByProcessedAtBefore(cutoffTime);

        if (archived > 0) {
            log.info("Archived {} processed messages older than {}",
                    archived, cutoffTime);
        } else {
            log.debug("No processed messages to archive before {}",
                    cutoffTime);
        }
    }

    /**
     * Manually trigger archival (for testing or administrative purposes).
     *
     * @param retentionDays number of days to retain processed messages
     *                      before archiving
     * @return number of messages archived
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int manualArchive(final int retentionDays) {
        if (retentionDays <= 0) {
            log.warn("Invalid retention days: {}. Must be > 0",
                    retentionDays);
            return 0;
        }

        LocalDateTime cutoffTime = LocalDateTime.now()
                .minusDays(retentionDays);

        // Find messages to archive
        List<ProcessedMessage> messagesToArchive = processedMessageRepository
                .findAll()
                .stream()
                .filter(m -> m.getProcessedAt().isBefore(cutoffTime))
                .toList();

        if (messagesToArchive.isEmpty()) {
            return 0;
        }

        log.info("Manual archival: archiving {} processed messages "
                + "older than {}", messagesToArchive.size(), cutoffTime);

        // Move to archive
        List<ProcessedMessageArchive> archives = messagesToArchive.stream()
                .map(ProcessedMessageArchive::new)
                .toList();
        archiveRepository.saveAll(archives);

        // Delete from main table
        processedMessageRepository.deleteAll(messagesToArchive);

        log.info("Manual archival completed: {} messages archived",
                messagesToArchive.size());
        return messagesToArchive.size();
    }
}
