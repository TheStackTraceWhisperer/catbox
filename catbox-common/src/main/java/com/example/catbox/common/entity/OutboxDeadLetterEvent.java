package com.example.catbox.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing events that have been moved to the dead-letter queue
 * after exceeding the maximum number of permanent failure retries.
 */
@Entity
@Table(name = "outbox_dead_letter_events")
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

    // Constructors
    public OutboxDeadLetterEvent() {
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalEventId() {
        return originalEventId;
    }

    public void setOriginalEventId(Long originalEventId) {
        this.originalEventId = originalEventId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getOriginalCreatedAt() {
        return originalCreatedAt;
    }

    public void setOriginalCreatedAt(LocalDateTime originalCreatedAt) {
        this.originalCreatedAt = originalCreatedAt;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }

    public String getFinalError() {
        return finalError;
    }

    public void setFinalError(String finalError) {
        this.finalError = finalError;
    }
}
