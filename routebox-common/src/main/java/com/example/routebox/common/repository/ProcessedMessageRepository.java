package com.example.routebox.common.repository;

import com.example.routebox.common.entity.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for managing processed message records.
 */
@Repository
public interface ProcessedMessageRepository
        extends JpaRepository<ProcessedMessage, Long> {

    /**
     * Check if a message has been processed for a specific consumer group.
     *
     * @param correlationId the correlation ID
     * @param consumerGroup the consumer group name
     * @return true if the message has been processed
     */
    boolean existsByCorrelationIdAndConsumerGroup(String correlationId,
                                                   String consumerGroup);

    /**
     * Find a processed message by correlation ID and consumer group.
     *
     * @param correlationId the correlation ID
     * @param consumerGroup the consumer group name
     * @return the processed message if found
     */
    Optional<ProcessedMessage> findByCorrelationIdAndConsumerGroup(
            String correlationId,
            String consumerGroup);

    /**
     * Delete a processed message record (for marking as unprocessed).
     *
     * @param correlationId the correlation ID
     * @param consumerGroup the consumer group name
     */
    @Modifying
    @Query("DELETE FROM ProcessedMessage p WHERE p.correlationId = :correlationId "
         + "AND p.consumerGroup = :consumerGroup")
    void deleteByCorrelationIdAndConsumerGroup(
            @Param("correlationId") String correlationId,
            @Param("consumerGroup") String consumerGroup);

    /**
     * Delete processed messages older than the specified date.
     * Used for archival cleanup.
     *
     * @param before the date threshold
     * @return the number of deleted records
     */
    @Modifying
    @Query("DELETE FROM ProcessedMessage p WHERE p.processedAt < :before")
    int deleteByProcessedAtBefore(@Param("before") LocalDateTime before);

    /**
     * Count total processed messages for a consumer group.
     *
     * @param consumerGroup the consumer group name
     * @return the count of processed messages
     */
    long countByConsumerGroup(String consumerGroup);
}
