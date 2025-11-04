package com.example.catbox.server.entity;

import com.example.catbox.common.entity.OutboxEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entity representing archived outbox events that have been successfully sent.
 * Events are moved here after a retention period to prevent unbounded table growth.
 */
@Entity
@Table(name = "outbox_archive_events")
@Getter
@Setter
@NoArgsConstructor
public class OutboxArchiveEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long originalEventId;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column
    private String correlationId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime archivedAt;

    @Column
    private Integer kafkaPartition;

    @Column
    private Long kafkaOffset;

    @Column
    private LocalDateTime kafkaTimestamp;

    @PrePersist
    protected void onArchive() {
        archivedAt = LocalDateTime.now();
    }

    public OutboxArchiveEvent(OutboxEvent event) {
        this.originalEventId = event.getId();
        this.aggregateType = event.getAggregateType();
        this.aggregateId = event.getAggregateId();
        this.eventType = event.getEventType();
        this.correlationId = event.getCorrelationId();
        this.payload = event.getPayload();
        this.createdAt = event.getCreatedAt();
        this.sentAt = event.getSentAt();
        this.kafkaPartition = event.getKafkaPartition();
        this.kafkaOffset = event.getKafkaOffset();
        this.kafkaTimestamp = event.getKafkaTimestamp();
    }
}
