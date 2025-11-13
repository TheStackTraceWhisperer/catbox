package com.example.routebox.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String aggregateType;

  @Column(nullable = false)
  private String aggregateId;

  @Column(nullable = false)
  private String eventType;

  @Column(unique = true)
  private String correlationId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column private LocalDateTime sentAt;

  @Column private LocalDateTime inProgressUntil;

  @Column private Integer permanentFailureCount = 0;

  @Column(columnDefinition = "TEXT")
  private String lastError;

  @Column private String kafkaClusterId;

  @Column private String kafkaTopicName;

  @Column private Integer kafkaPartition;

  @Column private Long kafkaOffset;

  @Column private LocalDateTime kafkaTimestamp;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (permanentFailureCount == null) {
      permanentFailureCount = 0;
    }
  }

  public OutboxEvent(String aggregateType, String aggregateId, String eventType, String payload) {
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
  }

  public OutboxEvent(
      String aggregateType,
      String aggregateId,
      String eventType,
      String correlationId,
      String payload) {
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.correlationId = correlationId;
    this.payload = payload;
  }

  // Helper method to increment permanent failure count
  public void incrementPermanentFailureCount() {
    if (this.permanentFailureCount == null) {
      this.permanentFailureCount = 0;
    }
    this.permanentFailureCount++;
  }
}
