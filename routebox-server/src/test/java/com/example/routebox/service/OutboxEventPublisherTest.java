package com.example.routebox.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import com.example.routebox.server.config.DynamicKafkaTemplateFactory;
import com.example.routebox.server.config.OutboxProcessingConfig;
import com.example.routebox.server.config.OutboxRoutingConfig;
import com.example.routebox.server.entity.OutboxDeadLetterEvent;
import com.example.routebox.server.repository.OutboxDeadLetterEventRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Tests for OutboxEventPublisher focusing on permanent vs transient failure classification. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class OutboxEventPublisherTest {

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
    // Configure permanent failure exceptions
    registry.add("outbox.processing.max-permanent-retries", () -> "3");
    registry.add(
        "outbox.processing.permanent-failure-exceptions[0]",
        () -> "java.lang.IllegalStateException");
    registry.add(
        "outbox.processing.permanent-failure-exceptions[1]",
        () -> "org.apache.kafka.common.errors.InvalidTopicException");
    // Add routing configuration
    registry.add("outbox.routing.rules.OrderCreated", () -> "cluster-a");
  }

  @Autowired OutboxEventRepository outboxEventRepository;

  @Autowired OutboxDeadLetterEventRepository deadLetterRepository;

  @Autowired OutboxEventPublisher publisher;

  @Autowired OutboxProcessingConfig processingConfig;

  @Autowired OutboxRoutingConfig routingConfig;

  @MockitoBean DynamicKafkaTemplateFactory kafkaTemplateFactory;

  @BeforeEach
  void setup() {
    deadLetterRepository.deleteAll();
    outboxEventRepository.deleteAll();
  }

  /** Helper method to create a mock SendResult with RecordMetadata */
  private SendResult<String, String> createMockSendResult(
      String topic, int partition, long offset) {
    TopicPartition topicPartition = new TopicPartition(topic, partition);
    RecordMetadata recordMetadata =
        new RecordMetadata(
            topicPartition,
            offset,
            0, // batch index
            System.currentTimeMillis(),
            0, // serialized key size
            0 // serialized value size
            );
    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, "key", "value");
    return new SendResult<>(producerRecord, recordMetadata);
  }

  @Test
  void publishEvent_successfullySendsAndMarksSent() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));

    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
    SendResult<String, String> mockSendResult = createMockSendResult("OrderCreated", 0, 12345L);
    CompletableFuture<SendResult<String, String>> future =
        CompletableFuture.completedFuture(mockSendResult);

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(future);

    // When
    publisher.publishEvent(event);

    // Then
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNotNull();
    assertThat(updated.getInProgressUntil()).isNull();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
    assertThat(updated.getLastError()).isNull();
  }

  @Test
  void publishEvent_handlesPermanentFailure_noRoute() throws Exception {
    // Given - Event with no configured route
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "UnknownEventType", "{}"));

    // When/Then - Publisher should throw RuntimeException (Worker will handle failure)
    assertThatThrownBy(() -> publisher.publishEvent(event))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to publish event")
        .hasCauseInstanceOf(IllegalStateException.class);
  }

  @Test
  void publishEvent_handlesPermanentFailure_kafkaException() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));

    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
    CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
    future.completeExceptionally(new IllegalStateException("Some permanent Kafka error"));

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(future);

    // When/Then - Publisher should throw RuntimeException (Worker will handle failure)
    assertThatThrownBy(() -> publisher.publishEvent(event))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to publish event");
  }

  @Test
  void publishEvent_movesToDeadLetterAfterMaxPermanentRetries() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "UnknownEventType", "{}"));

    // When/Then - Publisher should throw RuntimeException (Worker will handle failure)
    // This test no longer applies as the Publisher doesn't handle retries - the Worker does
    assertThatThrownBy(() -> publisher.publishEvent(event))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to publish event");
  }

  @Test
  void publishEvent_resetsFailureCountOnSuccess() throws Exception {
    // Given - Event with previous failures
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    event.setPermanentFailureCount(2);
    event.setLastError("Previous error");
    event = outboxEventRepository.save(event);

    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
    SendResult<String, String> mockSendResult = createMockSendResult("OrderCreated", 0, 12345L);
    CompletableFuture<SendResult<String, String>> future =
        CompletableFuture.completedFuture(mockSendResult);

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(future);

    // When
    publisher.publishEvent(event);

    // Then
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNotNull();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
    assertThat(updated.getLastError()).isNull();
  }

  @Test
  void publishEvent_capturesKafkaMetadata() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));

    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);

    // Create a mock SendResult with specific metadata
    int expectedPartition = 3;
    long expectedOffset = 98765L;
    SendResult<String, String> mockSendResult =
        createMockSendResult("OrderCreated", expectedPartition, expectedOffset);
    CompletableFuture<SendResult<String, String>> future =
        CompletableFuture.completedFuture(mockSendResult);

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(mockTemplate.send(any(ProducerRecord.class))).thenReturn(future);

    // When
    publisher.publishEvent(event);

    // Then - Verify Kafka metadata was captured
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNotNull();
    assertThat(updated.getKafkaPartition()).isEqualTo(expectedPartition);
    assertThat(updated.getKafkaOffset()).isEqualTo(expectedOffset);
    assertThat(updated.getKafkaTimestamp()).isNotNull();
  }

  @Test
  void publishEvent_handlesTransientFailure_releaseClaim() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
    // Simulate the event was claimed
    event.setInProgressUntil(java.time.LocalDateTime.now().plusMinutes(5));
    final OutboxEvent savedEvent = outboxEventRepository.save(event);

    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
    // Use a transient exception (not in permanent-failure-exceptions list)
    CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
    future.completeExceptionally(
        new org.apache.kafka.common.errors.TimeoutException("Transient network error"));

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(future);

    // When/Then - Publisher should throw RuntimeException (Worker will handle failure)
    assertThatThrownBy(() -> publisher.publishEvent(savedEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to publish event");
  }
}
