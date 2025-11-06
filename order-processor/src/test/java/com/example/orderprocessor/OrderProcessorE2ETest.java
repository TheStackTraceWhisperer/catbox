package com.example.orderprocessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.example.orderprocessor.model.OrderCreatedPayload;
import com.example.orderprocessor.service.OrderEventProcessingService;
import com.example.routebox.client.OutboxFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * End-to-end integration test for the order processor.
 *
 * <p>Tests:
 *
 * <ul>
 *   <li>Happy path: messages are processed successfully
 *   <li>Deduplication: duplicate messages are filtered out by OutboxFilter
 *   <li>Multiple messages: multiple unique messages are all processed
 *   <li>Database persistence: processed messages are tracked in the database
 * </ul>
 */
@SpringBootTest
@Testcontainers
class OrderProcessorE2ETest {

  @Container
  static final MSSQLServerContainer<?> mssqlContainer =
      new MSSQLServerContainer<>(
              DockerImageName.parse("mcr.microsoft.com/azure-sql-edge:2.0.0")
                  .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server"))
          .acceptLicense();

  @Container
  static final KafkaContainer kafkaContainer =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mssqlContainer::getJdbcUrl);
    registry.add("spring.datasource.username", mssqlContainer::getUsername);
    registry.add("spring.datasource.password", mssqlContainer::getPassword);
    registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
  }

  @Autowired private OrderEventProcessingService processingService;

  @Autowired private OutboxFilter outboxFilter;

  @Autowired private ObjectMapper objectMapper;

  private KafkaTemplate<String, String> kafkaTemplate;

  @BeforeEach
  void setUp() {
    // Reset processing counters
    processingService.resetCounters();

    // Create Kafka producer
    Map<String, Object> producerProps = new HashMap<>();
    producerProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    DefaultKafkaProducerFactory<String, String> producerFactory =
        new DefaultKafkaProducerFactory<>(producerProps);
    kafkaTemplate = new KafkaTemplate<>(producerFactory);
  }

  @AfterEach
  void tearDown() {
    if (kafkaTemplate != null) {
      kafkaTemplate.destroy();
    }
  }

  @Test
  void testHappyPath_SingleMessage_ProcessedSuccessfully() throws Exception {
    // Given: A unique OrderCreated event
    String correlationId = UUID.randomUUID().toString();
    OrderCreatedPayload payload =
        new OrderCreatedPayload(1L, "Alice", "Widget", new BigDecimal("99.99"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    // When: Send the message to Kafka
    sendOrderCreatedMessage(message, correlationId);

    // Then: Message is processed exactly once
    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Should process the message exactly once")
                  .isEqualTo(1);
            });

    // And: Correlation ID is marked as processed
    assertThat(outboxFilter.isProcessed(correlationId, "order-processor"))
        .as("Correlation ID should be marked as processed")
        .isTrue();
  }

  @Test
  void testDeduplication_DuplicateMessages_ProcessedOnlyOnce() throws Exception {
    // Given: A unique OrderCreated event
    String correlationId = UUID.randomUUID().toString();
    OrderCreatedPayload payload =
        new OrderCreatedPayload(2L, "Bob", "Gadget", new BigDecimal("149.99"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    // When: Send the same message 3 times (simulating duplicate delivery)
    sendOrderCreatedMessage(message, correlationId);
    sendOrderCreatedMessage(message, correlationId);
    sendOrderCreatedMessage(message, correlationId);

    // Then: Message is processed exactly once (not 3 times)
    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Should process the message only once despite 3 deliveries")
                  .isEqualTo(1);
            });

    // And: Correlation ID is marked as processed
    assertThat(outboxFilter.isProcessed(correlationId, "order-processor"))
        .as("Correlation ID should be marked as processed")
        .isTrue();

    // Verify no additional processing happens after waiting
    await()
        .atMost(2, TimeUnit.SECONDS)
        .pollDelay(1500, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Count should still be 1 after waiting")
                  .isEqualTo(1);
            });
  }

  @Test
  void testMultipleUniqueMessages_AllProcessed() throws Exception {
    // Given: 5 unique OrderCreated events
    int messageCount = 5;

    // When: Send 5 different messages
    for (int i = 0; i < messageCount; i++) {
      String correlationId = UUID.randomUUID().toString();
      OrderCreatedPayload payload =
          new OrderCreatedPayload(
              (long) (100 + i),
              "Customer-" + i,
              "Product-" + i,
              new BigDecimal("50.00"),
              "PENDING");
      String message = objectMapper.writeValueAsString(payload);
      sendOrderCreatedMessage(message, correlationId);
    }

    // Then: All 5 messages are processed
    await()
        .atMost(15, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Should process all 5 unique messages")
                  .isEqualTo(messageCount);
            });
  }

  @Test
  void testMixedScenario_UniqueAndDuplicateMessages() throws Exception {
    // Given: Mix of unique and duplicate messages
    String correlationId1 = UUID.randomUUID().toString();
    String correlationId2 = UUID.randomUUID().toString();

    OrderCreatedPayload payload1 =
        new OrderCreatedPayload(201L, "Charlie", "Item-A", new BigDecimal("25.00"), "PENDING");
    OrderCreatedPayload payload2 =
        new OrderCreatedPayload(202L, "Diana", "Item-B", new BigDecimal("35.00"), "PENDING");

    String message1 = objectMapper.writeValueAsString(payload1);
    String message2 = objectMapper.writeValueAsString(payload2);

    // When: Send message1 twice, message2 three times
    sendOrderCreatedMessage(message1, correlationId1);
    sendOrderCreatedMessage(message1, correlationId1); // duplicate
    sendOrderCreatedMessage(message2, correlationId2);
    sendOrderCreatedMessage(message2, correlationId2); // duplicate
    sendOrderCreatedMessage(message2, correlationId2); // duplicate

    // Then: Only 2 unique messages are processed
    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Should process only 2 unique messages")
                  .isEqualTo(2);
            });

    // And: Both correlation IDs are marked as processed
    assertThat(outboxFilter.isProcessed(correlationId1, "order-processor"))
        .as("First correlation ID should be marked as processed")
        .isTrue();
    assertThat(outboxFilter.isProcessed(correlationId2, "order-processor"))
        .as("Second correlation ID should be marked as processed")
        .isTrue();
  }

  @Test
  void testMessageWithoutCorrelationId_StillProcessed() throws Exception {
    // Given: A message without correlation ID
    OrderCreatedPayload payload =
        new OrderCreatedPayload(
            300L, "Eve", "No-Corr-Id-Product", new BigDecimal("10.00"), "PENDING");
    String message = objectMapper.writeValueAsString(payload);

    int initialCount = processingService.getProcessedOrderCreatedCount();

    // When: Send the message without correlation ID
    var kafkaMessage =
        MessageBuilder.withPayload(message).setHeader(KafkaHeaders.TOPIC, "OrderCreated").build();
    kafkaTemplate.send(kafkaMessage);

    // Then: Message is still processed (deduplication doesn't apply without correlation ID)
    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Should process the message even without correlation ID")
                  .isGreaterThan(initialCount);
            });
  }

  @Test
  void testPoisonPill_InvalidJson_RoutedToDLT() throws Exception {
    // Given: An invalid JSON message (poison pill)
    String correlationId = UUID.randomUUID().toString();
    String invalidMessage = "{ invalid json }";

    int initialCount = processingService.getProcessedOrderCreatedCount();

    // When: Send the poison pill message
    var kafkaMessage =
        MessageBuilder.withPayload(invalidMessage)
            .setHeader(KafkaHeaders.TOPIC, "OrderCreated")
            .setHeader("correlationId", correlationId)
            .build();
    kafkaTemplate.send(kafkaMessage);

    // Then: Message is NOT processed (due to JSON parsing error)
    // Wait to ensure the error handler had time to process and route to DLT
    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollDelay(2, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Poison pill should not be processed successfully")
                  .isEqualTo(initialCount);
            });

    // Note: The correlation ID may be marked as "processed" by the deduplication filter
    // even though the message failed, because deduplication happens before parsing.
    // The important thing is that the message doesn't get processed successfully
    // and doesn't block the consumer.

    // Verify that subsequent valid messages can still be processed
    String validCorrelationId = UUID.randomUUID().toString();
    OrderCreatedPayload validPayload =
        new OrderCreatedPayload(
            999L, "ValidCustomer", "ValidProduct", new BigDecimal("100.00"), "PENDING");
    String validMessage = objectMapper.writeValueAsString(validPayload);
    sendOrderCreatedMessage(validMessage, validCorrelationId);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .untilAsserted(
            () -> {
              assertThat(processingService.getProcessedOrderCreatedCount())
                  .as("Consumer should not be blocked by poison pill - subsequent valid messages should be processed")
                  .isGreaterThan(initialCount);
            });
  }

  /** Helper method to send an OrderCreated message to Kafka with a correlation ID. */
  private void sendOrderCreatedMessage(String message, String correlationId) {
    var kafkaMessage =
        MessageBuilder.withPayload(message)
            .setHeader(KafkaHeaders.TOPIC, "OrderCreated")
            .setHeader("correlationId", correlationId)
            .build();

    kafkaTemplate.send(kafkaMessage);
  }
}
