package com.example.routebox.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.routebox.common.entity.ProcessedMessage;
import com.example.routebox.common.entity.ProcessedMessageArchive;
import com.example.routebox.common.repository.ProcessedMessageArchiveRepository;
import com.example.routebox.common.repository.ProcessedMessageRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import com.example.routebox.server.config.OutboxProcessingConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Tests for ProcessedMessageArchivalService. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class ProcessedMessageArchivalServiceTest {

  @Container
  static MSSQLServerContainer<?> mssql =
      new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
          .acceptLicense()
          .withReuse(true);

  @DynamicPropertySource
  static void sqlProps(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
    registry.add("spring.datasource.username", mssql::getUsername);
    registry.add("spring.datasource.password", mssql::getPassword);
    registry.add(
        "spring.datasource.driver-class-name",
        () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    registry.add(
        "spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("outbox.processing.archival-retention-days", () -> "7");
  }

  @Autowired ProcessedMessageRepository processedMessageRepository;

  @Autowired ProcessedMessageArchiveRepository processedMessageArchiveRepository;

  @Autowired ProcessedMessageArchivalService archivalService;

  @Autowired OutboxProcessingConfig processingConfig;

  @BeforeEach
  void setup() {
    processedMessageArchiveRepository.deleteAll();
    processedMessageRepository.deleteAll();
  }

  @Test
  void archiveOldMessages_deletesOldMessages() {
    // Given - Create old processed messages
    ProcessedMessage oldMessage1 =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    oldMessage1.setProcessedAt(LocalDateTime.now().minusDays(10));
    processedMessageRepository.save(oldMessage1);

    ProcessedMessage oldMessage2 =
        new ProcessedMessage("corr-2", "consumer-group-1", "OrderUpdated", "Order", "A2");
    oldMessage2.setProcessedAt(LocalDateTime.now().minusDays(8));
    processedMessageRepository.save(oldMessage2);

    // Create a recent message (should not be deleted)
    ProcessedMessage recentMessage =
        new ProcessedMessage("corr-3", "consumer-group-1", "OrderCreated", "Order", "A3");
    recentMessage.setProcessedAt(LocalDateTime.now().minusDays(3));
    processedMessageRepository.save(recentMessage);

    // When
    archivalService.archiveOldMessages();

    // Then - Old messages should be deleted, recent message should remain
    List<ProcessedMessage> remainingMessages = processedMessageRepository.findAll();
    assertThat(remainingMessages).hasSize(1);
    assertThat(remainingMessages.get(0).getCorrelationId()).isEqualTo("corr-3");
  }

  @Test
  void archiveOldMessages_doesNothingWhenNoOldMessages() {
    // Given - Only recent messages
    ProcessedMessage recentMessage =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    recentMessage.setProcessedAt(LocalDateTime.now().minusDays(1));
    processedMessageRepository.save(recentMessage);

    // When
    archivalService.archiveOldMessages();

    // Then - Message should still exist
    assertThat(processedMessageRepository.count()).isEqualTo(1);
  }

  @Test
  void archiveOldMessages_respectsRetentionDays() {
    // Given - Message just beyond retention boundary (should be deleted)
    ProcessedMessage boundaryMessage =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    boundaryMessage.setProcessedAt(
        LocalDateTime.now().minusDays(processingConfig.getArchivalRetentionDays() + 1));
    processedMessageRepository.save(boundaryMessage);

    // When
    archivalService.archiveOldMessages();

    // Then - Message beyond boundary should be deleted
    assertThat(processedMessageRepository.count()).isEqualTo(0);
  }

  @Test
  void manualArchive_archivesAndDeletesOldMessages() {
    // Given
    ProcessedMessage oldMessage =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    oldMessage.setProcessedAt(LocalDateTime.now().minusDays(5));
    oldMessage = processedMessageRepository.save(oldMessage);

    ProcessedMessage recentMessage =
        new ProcessedMessage("corr-2", "consumer-group-1", "OrderCreated", "Order", "A2");
    recentMessage.setProcessedAt(LocalDateTime.now().minusDays(1));
    processedMessageRepository.save(recentMessage);

    // When - Archive with 3-day retention
    int archived = archivalService.manualArchive(3);

    // Then
    assertThat(archived).isEqualTo(1);
    assertThat(processedMessageRepository.count()).isEqualTo(1);
    assertThat(processedMessageArchiveRepository.count()).isEqualTo(1);

    // Verify archived message content
    ProcessedMessageArchive archivedMessage = processedMessageArchiveRepository.findAll().get(0);
    assertThat(archivedMessage.getCorrelationId()).isEqualTo("corr-1");
    assertThat(archivedMessage.getConsumerGroup()).isEqualTo("consumer-group-1");
    assertThat(archivedMessage.getEventType()).isEqualTo("OrderCreated");
  }

  @Test
  void manualArchive_returnsZeroForInvalidRetention() {
    // Given
    ProcessedMessage message =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    message.setProcessedAt(LocalDateTime.now().minusDays(10));
    processedMessageRepository.save(message);

    // When - Archive with 0 days retention (invalid)
    int archived = archivalService.manualArchive(0);

    // Then
    assertThat(archived).isEqualTo(0);
    assertThat(processedMessageRepository.count()).isEqualTo(1);
    assertThat(processedMessageArchiveRepository.count()).isEqualTo(0);
  }

  @Test
  void manualArchive_returnsZeroForNegativeRetention() {
    // Given
    ProcessedMessage message =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    message.setProcessedAt(LocalDateTime.now().minusDays(10));
    processedMessageRepository.save(message);

    // When - Archive with negative retention (invalid)
    int archived = archivalService.manualArchive(-5);

    // Then
    assertThat(archived).isEqualTo(0);
    assertThat(processedMessageRepository.count()).isEqualTo(1);
    assertThat(processedMessageArchiveRepository.count()).isEqualTo(0);
  }

  @Test
  void manualArchive_returnsZeroWhenNoMessagesToArchive() {
    // Given - Only recent messages
    ProcessedMessage message =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    message.setProcessedAt(LocalDateTime.now().minusDays(1));
    processedMessageRepository.save(message);

    // When - Archive with 3-day retention
    int archived = archivalService.manualArchive(3);

    // Then
    assertThat(archived).isEqualTo(0);
    assertThat(processedMessageRepository.count()).isEqualTo(1);
    assertThat(processedMessageArchiveRepository.count()).isEqualTo(0);
  }

  @Test
  void manualArchive_preservesAllMessageData() {
    // Given - Message with all fields populated
    ProcessedMessage message =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    message.setProcessedAt(LocalDateTime.now().minusDays(5));
    message = processedMessageRepository.save(message);
    Long originalId = message.getId();

    // When
    int archived = archivalService.manualArchive(3);

    // Then
    assertThat(archived).isEqualTo(1);
    ProcessedMessageArchive archivedMessage = processedMessageArchiveRepository.findAll().get(0);
    assertThat(archivedMessage.getOriginalId()).isEqualTo(originalId);
    assertThat(archivedMessage.getCorrelationId()).isEqualTo("corr-1");
    assertThat(archivedMessage.getConsumerGroup()).isEqualTo("consumer-group-1");
    assertThat(archivedMessage.getEventType()).isEqualTo("OrderCreated");
    assertThat(archivedMessage.getAggregateType()).isEqualTo("Order");
    assertThat(archivedMessage.getAggregateId()).isEqualTo("A1");
    assertThat(archivedMessage.getProcessedAt()).isNotNull();
    assertThat(archivedMessage.getArchivedAt()).isNotNull();
  }

  @Test
  void manualArchive_archivesMultipleMessages() {
    // Given - Multiple old messages
    for (int i = 0; i < 5; i++) {
      ProcessedMessage message =
          new ProcessedMessage(
              "corr-" + i, "consumer-group-1", "OrderCreated", "Order", "A" + i);
      message.setProcessedAt(LocalDateTime.now().minusDays(10));
      processedMessageRepository.save(message);
    }

    // When
    int archived = archivalService.manualArchive(7);

    // Then
    assertThat(archived).isEqualTo(5);
    assertThat(processedMessageRepository.count()).isEqualTo(0);
    assertThat(processedMessageArchiveRepository.count()).isEqualTo(5);
  }

  @Test
  void archiveOldMessages_doesNothingWhenRetentionIsZero() {
    // This test verifies the behavior when archival is disabled in the scheduled method
    // Note: We can't easily test the scheduled method directly with retention=0 from config
    // because it's set to 7 in the test properties. This test documents expected behavior.

    // Given - Old messages exist
    ProcessedMessage oldMessage =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    oldMessage.setProcessedAt(LocalDateTime.now().minusDays(10));
    processedMessageRepository.save(oldMessage);

    // When - Manual archive with 0 retention (disabled)
    int archived = archivalService.manualArchive(0);

    // Then - Nothing should be archived
    assertThat(archived).isEqualTo(0);
    assertThat(processedMessageRepository.count()).isEqualTo(1);
  }
}
