package com.example.routebox.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.server.RouteBoxServerApplication;
import com.example.routebox.server.config.DynamicKafkaTemplateFactory;
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

/** Tests for multi-cluster publishing scenarios in OutboxEventPublisher. */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class OutboxEventPublisherMultiClusterTest {

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

    // Configure multi-cluster routing
    // Test event with all-must-succeed strategy
    registry.add("outbox.routing.rules.OrderCreated.clusters[0]", () -> "cluster-a");
    registry.add("outbox.routing.rules.OrderCreated.clusters[1]", () -> "cluster-b");
    registry.add("outbox.routing.rules.OrderCreated.strategy", () -> "all-must-succeed");

    // Test event with at-least-one strategy
    registry.add("outbox.routing.rules.OrderStatusChanged.clusters[0]", () -> "cluster-a");
    registry.add("outbox.routing.rules.OrderStatusChanged.clusters[1]", () -> "cluster-b");
    registry.add("outbox.routing.rules.OrderStatusChanged.strategy", () -> "at-least-one");

    // Test event with optional clusters
    registry.add("outbox.routing.rules.InventoryAdjusted.clusters[0]", () -> "cluster-a");
    registry.add("outbox.routing.rules.InventoryAdjusted.optional[0]", () -> "cluster-b");
    registry.add("outbox.routing.rules.InventoryAdjusted.strategy", () -> "all-must-succeed");
  }

  @Autowired OutboxEventRepository outboxEventRepository;

  @Autowired com.example.routebox.server.service.OutboxEventPublisher publisher;

  @MockitoBean DynamicKafkaTemplateFactory kafkaTemplateFactory;

  @SuppressWarnings("unchecked")
  private KafkaTemplate<String, String> mockTemplate;

  @BeforeEach
  void setup() {
    outboxEventRepository.deleteAll();
    mockTemplate = Mockito.mock(KafkaTemplate.class);
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
  void allMustSucceed_successWhenAllClustersSucceed() throws Exception {
    // Given - Event configured with all-must-succeed strategy
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));

    SendResult<String, String> mockSendResult = createMockSendResult("OrderCreated", 0, 12345L);
    CompletableFuture<SendResult<String, String>> future =
        CompletableFuture.completedFuture(mockSendResult);
    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-b"))).thenReturn(mockTemplate);
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(future);

    // When
    publisher.publishEvent(event);

    // Then
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNotNull();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(0);

    // Verify both clusters were called
    Mockito.verify(kafkaTemplateFactory).getTemplate("cluster-a");
    Mockito.verify(kafkaTemplateFactory).getTemplate("cluster-b");
  }

  @Test
  void allMustSucceed_failsWhenOneClusterFails() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));

    SendResult<String, String> mockSendResult = createMockSendResult("OrderCreated", 0, 12345L);
    CompletableFuture<SendResult<String, String>> successFuture =
        CompletableFuture.completedFuture(mockSendResult);
    CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
    failureFuture.completeExceptionally(new RuntimeException("Connection failed"));

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-b"))).thenReturn(mockTemplate);

    // First call succeeds, second fails
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(successFuture)
        .thenReturn(failureFuture);

    // When
    publisher.publishEvent(event);

    // Then - Should not be marked as sent due to one failure
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNull();
    assertThat(updated.getInProgressUntil()).isNull(); // Claim cleared for retry
  }

  @Test
  void atLeastOne_successWhenOnlyOneClusterSucceeds() throws Exception {
    // Given - Event configured with at-least-one strategy
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderStatusChanged", "{}"));

    SendResult<String, String> mockSendResult =
        createMockSendResult("OrderStatusChanged", 0, 12345L);
    CompletableFuture<SendResult<String, String>> successFuture =
        CompletableFuture.completedFuture(mockSendResult);
    CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
    failureFuture.completeExceptionally(new RuntimeException("Connection failed"));

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-b"))).thenReturn(mockTemplate);

    // First call succeeds, second fails
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(successFuture)
        .thenReturn(failureFuture);

    // When
    publisher.publishEvent(event);

    // Then - Should be marked as sent because at least one succeeded
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNotNull();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
  }

  @Test
  void atLeastOne_failsWhenAllClustersFail() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderStatusChanged", "{}"));

    CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
    failureFuture.completeExceptionally(new RuntimeException("Connection failed"));

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-b"))).thenReturn(mockTemplate);
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(failureFuture);

    // When
    publisher.publishEvent(event);

    // Then - Should not be marked as sent
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNull();
  }

  @Test
  void optionalClusters_successWhenRequiredSucceedsOptionalFails() throws Exception {
    // Given - Event with required cluster-a and optional cluster-b
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Inventory", "I1", "InventoryAdjusted", "{}"));

    SendResult<String, String> mockSendResult =
        createMockSendResult("InventoryAdjusted", 0, 12345L);
    CompletableFuture<SendResult<String, String>> successFuture =
        CompletableFuture.completedFuture(mockSendResult);
    CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
    failureFuture.completeExceptionally(new RuntimeException("Optional cluster failed"));

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-b"))).thenReturn(mockTemplate);

    // First call (required) succeeds, second (optional) fails
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(successFuture)
        .thenReturn(failureFuture);

    // When
    publisher.publishEvent(event);

    // Then - Should be marked as sent because required succeeded (optional failure ignored)
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNotNull();
    assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
  }

  @Test
  void optionalClusters_failsWhenRequiredClusterFails() throws Exception {
    // Given
    OutboxEvent event =
        outboxEventRepository.save(new OutboxEvent("Inventory", "I1", "InventoryAdjusted", "{}"));

    SendResult<String, String> mockSendResult =
        createMockSendResult("InventoryAdjusted", 0, 12345L);
    CompletableFuture<SendResult<String, String>> successFuture =
        CompletableFuture.completedFuture(mockSendResult);
    CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
    failureFuture.completeExceptionally(new RuntimeException("Required cluster failed"));

    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
    Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-b"))).thenReturn(mockTemplate);

    // First call (required) fails, second (optional) succeeds
    Mockito.when(mockTemplate.send(any(org.apache.kafka.clients.producer.ProducerRecord.class)))
        .thenReturn(failureFuture)
        .thenReturn(successFuture);

    // When
    publisher.publishEvent(event);

    // Then - Should not be marked as sent because required failed
    OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
    assertThat(updated.getSentAt()).isNull();
  }
}
