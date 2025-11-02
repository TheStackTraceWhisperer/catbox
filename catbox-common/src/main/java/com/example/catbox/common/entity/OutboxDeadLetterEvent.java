package com.example.catbox.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing events that have been moved to the dead-letter queue
 * after exceeding the maximum number of permanent failure retries.
 */
@Entity
@Table(name = "outbox_dead_letter_events")
@Getter
@Setter
@NoArgsConstructor
public class OutboxDeadLetterEvent {

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime originalCreatedAt;

    @Column(nullable = false)
    private LocalDateTime failedAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String finalError;

    @PrePersist
    protected void onFail() {
        failedAt = LocalDateTime.now();
    }

    public OutboxDeadLetterEvent(OutboxEvent event, String finalError) {
        this.originalEventId = event.getId();
        this.aggregateType = event.getAggregateType();
        this.aggregateId = event.getAggregateId();
        this.eventType = event.getEventType();
        this.payload = event.getPayload();
        this.originalCreatedAt = event.getCreatedAt();
        this.finalError = finalError;
    }
}
