package com.example.routebox.server.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.routebox.common.entity.ProcessedMessage;
import com.example.routebox.common.repository.ProcessedMessageRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Tests for ProcessedMessageService. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Transactional
@Testcontainers
class ProcessedMessageServiceTest {
  @Container
  static final MSSQLServerContainer<?> mssql =
      new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
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
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    registry.add("spring.threads.virtual.enabled", () -> "true");
  }

  @Autowired ProcessedMessageRepository processedMessageRepository;

  @Autowired ProcessedMessageService processedMessageService;

  @BeforeEach
  void setup() {
    processedMessageRepository.deleteAll();
  }

  @Test
  void findPaged_returnsAllMessages() {
    // Given
    processedMessageRepository.save(
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1"));
    processedMessageRepository.save(
        new ProcessedMessage("corr-2", "consumer-group-1", "OrderUpdated", "Order", "A2"));
    processedMessageRepository.save(
        new ProcessedMessage("corr-3", "consumer-group-2", "OrderCreated", "Order", "A3"));

    // When
    Page<ProcessedMessage> page =
        processedMessageService.findPaged(0, 10, null, null, "processedAt", Sort.Direction.ASC);

    // Then
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getContent()).hasSize(3);
  }

  @Test
  void findPaged_withPagination() {
    // Given - Create 25 messages
    for (int i = 0; i < 25; i++) {
      processedMessageRepository.save(
          new ProcessedMessage("corr-" + i, "consumer-group-1", "OrderCreated", "Order", "A" + i));
    }

    // When - Request page 1 with size 10
    Page<ProcessedMessage> page =
        processedMessageService.findPaged(1, 10, null, null, "processedAt", Sort.Direction.ASC);

    // Then
    assertThat(page.getTotalElements()).isEqualTo(25);
    assertThat(page.getContent()).hasSize(10);
    assertThat(page.getNumber()).isEqualTo(1);
    assertThat(page.getTotalPages()).isEqualTo(3);
  }

  @Test
  void findPaged_sortsByField() {
    // Given - Use fixed timestamps for reliable ordering
    ProcessedMessage msg1 =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    msg1.setProcessedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
    processedMessageRepository.save(msg1);

    ProcessedMessage msg2 =
        new ProcessedMessage("corr-2", "consumer-group-1", "OrderCreated", "Order", "A2");
    msg2.setProcessedAt(LocalDateTime.of(2024, 1, 2, 10, 0));
    processedMessageRepository.save(msg2);

    ProcessedMessage msg3 =
        new ProcessedMessage("corr-3", "consumer-group-1", "OrderCreated", "Order", "A3");
    msg3.setProcessedAt(LocalDateTime.of(2024, 1, 3, 10, 0));
    processedMessageRepository.save(msg3);

    // When - Sort by processedAt DESC
    Page<ProcessedMessage> page =
        processedMessageService.findPaged(0, 10, null, null, "processedAt", Sort.Direction.DESC);

    // Then
    assertThat(page.getContent()).hasSize(3);
    assertThat(page.getContent().get(0).getCorrelationId()).isEqualTo("corr-3");
    assertThat(page.getContent().get(1).getCorrelationId()).isEqualTo("corr-2");
    assertThat(page.getContent().get(2).getCorrelationId()).isEqualTo("corr-1");
  }

  @Test
  void markUnprocessed_removesMessageFromDatabase() {
    // Given
    ProcessedMessage message =
        processedMessageRepository.save(
            new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1"));
    Long messageId = message.getId();
    assertThat(processedMessageRepository.count()).isEqualTo(1);

    // When
    processedMessageService.markUnprocessed("corr-1", "consumer-group-1");

    // Then - flush to ensure delete is committed
    processedMessageRepository.flush();
    assertThat(
            processedMessageRepository.existsByCorrelationIdAndConsumerGroup(
                "corr-1", "consumer-group-1"))
        .isFalse();
  }

  @Test
  void markUnprocessed_doesNothingWhenMessageNotFound() {
    // Given - No messages in database
    assertThat(processedMessageRepository.count()).isEqualTo(0);

    // When - Try to mark non-existent message as unprocessed
    processedMessageService.markUnprocessed("non-existent", "consumer-group-1");

    // Then - No exception thrown, operation completes successfully
    assertThat(processedMessageRepository.count()).isEqualTo(0);
  }

  @Test
  void countByConsumerGroup_returnsCorrectCount() {
    // Given
    processedMessageRepository.save(
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1"));
    processedMessageRepository.save(
        new ProcessedMessage("corr-2", "consumer-group-1", "OrderCreated", "Order", "A2"));
    processedMessageRepository.save(
        new ProcessedMessage("corr-3", "consumer-group-2", "OrderCreated", "Order", "A3"));

    // When
    long count1 = processedMessageService.countByConsumerGroup("consumer-group-1");
    long count2 = processedMessageService.countByConsumerGroup("consumer-group-2");
    long count3 = processedMessageService.countByConsumerGroup("non-existent");

    // Then
    assertThat(count1).isEqualTo(2);
    assertThat(count2).isEqualTo(1);
    assertThat(count3).isEqualTo(0);
  }

  @Test
  void processedMessageSummary_fromEntity() {
    // Given
    ProcessedMessage message =
        new ProcessedMessage("corr-1", "consumer-group-1", "OrderCreated", "Order", "A1");
    message.setId(123L);
    message.setProcessedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

    // When
    ProcessedMessageService.ProcessedMessageSummary summary =
        ProcessedMessageService.ProcessedMessageSummary.from(message);

    // Then
    assertThat(summary.id()).isEqualTo(123L);
    assertThat(summary.correlationId()).isEqualTo("corr-1");
    assertThat(summary.consumerGroup()).isEqualTo("consumer-group-1");
    assertThat(summary.eventType()).isEqualTo("OrderCreated");
    assertThat(summary.aggregateType()).isEqualTo("Order");
    assertThat(summary.aggregateId()).isEqualTo("A1");
    assertThat(summary.processedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
  }

  @Test
  void processedMessageSummary_fromEntityWithNullFields() {
    // Given - Message with minimal fields
    ProcessedMessage message = new ProcessedMessage("corr-1", "consumer-group-1");
    message.setId(456L);
    message.setProcessedAt(LocalDateTime.now());

    // When
    ProcessedMessageService.ProcessedMessageSummary summary =
        ProcessedMessageService.ProcessedMessageSummary.from(message);

    // Then
    assertThat(summary.id()).isEqualTo(456L);
    assertThat(summary.correlationId()).isEqualTo("corr-1");
    assertThat(summary.consumerGroup()).isEqualTo("consumer-group-1");
    assertThat(summary.eventType()).isNull();
    assertThat(summary.aggregateType()).isNull();
    assertThat(summary.aggregateId()).isNull();
  }

  @Test
  void findPaged_sortsByCorrelationId() {
    // Given
    processedMessageRepository.save(
        new ProcessedMessage("corr-c", "consumer-group-1", "OrderCreated", "Order", "A1"));
    processedMessageRepository.save(
        new ProcessedMessage("corr-a", "consumer-group-1", "OrderCreated", "Order", "A2"));
    processedMessageRepository.save(
        new ProcessedMessage("corr-b", "consumer-group-1", "OrderCreated", "Order", "A3"));

    // When - Sort by correlationId ASC
    Page<ProcessedMessage> page =
        processedMessageService.findPaged(0, 10, null, null, "correlationId", Sort.Direction.ASC);

    // Then
    assertThat(page.getContent()).hasSize(3);
    assertThat(page.getContent().get(0).getCorrelationId()).isEqualTo("corr-a");
    assertThat(page.getContent().get(1).getCorrelationId()).isEqualTo("corr-b");
    assertThat(page.getContent().get(2).getCorrelationId()).isEqualTo("corr-c");
  }
}
