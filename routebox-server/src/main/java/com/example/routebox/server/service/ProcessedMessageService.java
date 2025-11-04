package com.example.routebox.server.service;

import com.example.catbox.common.entity.ProcessedMessage;
import com.example.catbox.common.repository.ProcessedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for managing processed messages through the admin UI.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedMessageService {

    private final ProcessedMessageRepository repository;

    /**
     * Find processed messages with pagination and filtering.
     *
     * @param page the page number
     * @param size the page size
     * @param consumerGroup optional consumer group filter
     * @param correlationId optional correlation ID filter
     * @param sortBy the field to sort by
     * @param direction the sort direction
     * @return page of processed messages
     */
    @Transactional(readOnly = true)
    public Page<ProcessedMessage> findPaged(final Integer page,
                                            final Integer size,
                                            final String consumerGroup,
                                            final String correlationId,
                                            final String sortBy,
                                            final Sort.Direction direction) {
        Sort sort = Sort.by(direction, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // For now, return all messages
        // In a real implementation, you would apply filters
        return repository.findAll(pageRequest);
    }

    /**
     * Mark a message as unprocessed by removing it from the database.
     *
     * @param correlationId the correlation ID
     * @param consumerGroup the consumer group
     */
    @Transactional
    public void markUnprocessed(final String correlationId,
                               final String consumerGroup) {
        repository.deleteByCorrelationIdAndConsumerGroup(
                correlationId, consumerGroup);
        log.info("Marked message as unprocessed: correlationId={}, "
                + "consumerGroup={}", correlationId, consumerGroup);
    }

    /**
     * Get total count of processed messages for a consumer group.
     *
     * @param consumerGroup the consumer group
     * @return the count
     */
    @Transactional(readOnly = true)
    public long countByConsumerGroup(final String consumerGroup) {
        return repository.countByConsumerGroup(consumerGroup);
    }

    /**
     * DTO for processed message summary.
     */
    public record ProcessedMessageSummary(
            Long id,
            String correlationId,
            String consumerGroup,
            LocalDateTime processedAt,
            String eventType,
            String aggregateType,
            String aggregateId
    ) {
        /**
         * Create from entity.
         *
         * @param message the processed message entity
         * @return the summary DTO
         */
        public static ProcessedMessageSummary from(
                final ProcessedMessage message) {
            return new ProcessedMessageSummary(
                    message.getId(),
                    message.getCorrelationId(),
                    message.getConsumerGroup(),
                    message.getProcessedAt(),
                    message.getEventType(),
                    message.getAggregateType(),
                    message.getAggregateId()
            );
        }
    }
}
