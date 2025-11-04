package com.example.routebox.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing archived processed messages. Messages are moved here after a retention period
 * to prevent unbounded table growth.
 */
@Entity
@Table(
    name = "processed_messages_archive",
    indexes = {
      @Index(name = "idx_archive_correlation_consumer", columnList = "correlationId,consumerGroup"),
      @Index(name = "idx_archive_archived_at", columnList = "archivedAt")
    })
@Getter
@Setter
@NoArgsConstructor
public class ProcessedMessageArchive {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long originalId;

  @Column(nullable = false, length = 255)
  private String correlationId;

  @Column(nullable = false, length = 100)
  private String consumerGroup;

  @Column(nullable = false)
  private LocalDateTime processedAt;

  @Column(nullable = false)
  private LocalDateTime archivedAt;

  @Column(length = 100)
  private String eventType;

  @Column(length = 100)
  private String aggregateType;

  @Column(length = 100)
  private String aggregateId;

  @PrePersist
  protected void onArchive() {
    if (archivedAt == null) {
      archivedAt = LocalDateTime.now();
    }
  }

  /**
   * Constructor from ProcessedMessage entity.
   *
   * @param message the processed message to archive
   */
  public ProcessedMessageArchive(final ProcessedMessage message) {
    this.originalId = message.getId();
    this.correlationId = message.getCorrelationId();
    this.consumerGroup = message.getConsumerGroup();
    this.processedAt = message.getProcessedAt();
    this.eventType = message.getEventType();
    this.aggregateType = message.getAggregateType();
    this.aggregateId = message.getAggregateId();
  }
}
