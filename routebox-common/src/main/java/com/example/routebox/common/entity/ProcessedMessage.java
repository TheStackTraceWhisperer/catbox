package com.example.routebox.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entity representing processed messages tracked by consumer group.
 * Used for deduplication of Kafka messages to prevent duplicate processing.
 */
@Entity
@Table(name = "processed_messages",
        indexes = {
            @Index(name = "idx_correlation_consumer", 
                   columnList = "correlationId,consumerGroup", 
                   unique = true),
            @Index(name = "idx_processed_at", columnList = "processedAt")
        })
@Getter
@Setter
@NoArgsConstructor
public class ProcessedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String correlationId;

    @Column(nullable = false, length = 100)
    private String consumerGroup;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    @Column(length = 100)
    private String eventType;

    @Column(length = 100)
    private String aggregateType;

    @Column(length = 100)
    private String aggregateId;

    @PrePersist
    protected void onCreate() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }

    /**
     * Constructor for creating a processed message record.
     *
     * @param correlationId the correlation ID from the Kafka message
     * @param consumerGroup the consumer group name
     */
    public ProcessedMessage(final String correlationId,
                           final String consumerGroup) {
        this.correlationId = correlationId;
        this.consumerGroup = consumerGroup;
    }

    /**
     * Constructor with additional metadata.
     *
     * @param correlationId the correlation ID from the Kafka message
     * @param consumerGroup the consumer group name
     * @param eventType the event type being processed
     * @param aggregateType the aggregate type
     * @param aggregateId the aggregate ID
     */
    public ProcessedMessage(final String correlationId,
                           final String consumerGroup,
                           final String eventType,
                           final String aggregateType,
                           final String aggregateId) {
        this.correlationId = correlationId;
        this.consumerGroup = consumerGroup;
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }
}
