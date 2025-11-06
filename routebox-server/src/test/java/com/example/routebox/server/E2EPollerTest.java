package com.example.routebox.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.test.listener.SharedTestcontainers;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-End test for the OutboxEventPoller, OutboxEventClaimer, and OutboxEventPublisher. Tests
 * the complete processing loop from event creation to Kafka publication.
 */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class E2EPollerTest {

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // Routing configuration
    registry.add("outbox.routing.rules.OrderCreated", () -> "cluster-a");

    // Speed up polling for tests
    registry.add("outbox.processing.poll-fixed-delay", () -> "500ms");
    registry.add("outbox.processing.poll-initial-delay", () -> "1s");
  }

  @Autowired private OutboxEventRepository outboxEventRepository;

  private KafkaMessageListenerContainer<String, String> container;
  private BlockingQueue<ConsumerRecord<String, String>> records;

  @BeforeEach
  void setUp() {
    // Set up Kafka consumer for OrderCreated topic
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        SharedTestcontainers.kafkaA.getBootstrapServers());
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(consumerProps);
    ContainerProperties containerProps = new ContainerProperties("OrderCreated");
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);

    records = new LinkedBlockingQueue<>();
    container.setupMessageListener((MessageListener<String, String>) records::add);
    container.start();
  }

  @AfterEach
  void tearDown() {
    if (container != null) {
      container.stop();
    }
  }

  /**
   * Gap 1: Tests the core end-to-end poller loop. Verifies that an event, once created, is claimed,
   * published, and marked as sent.
   */
  @Test
  void testPollerClaimsAndPublishesEvent() throws Exception {
    // Arrange: Create a test outbox event
    OutboxEvent event =
        new OutboxEvent(
            "Order",
            "order-12345",
            "OrderCreated",
            "{\"orderId\":12345,\"customerName\":\"John Doe\",\"amount\":99.99}");
    OutboxEvent savedEvent = outboxEventRepository.save(event);
    assertThat(savedEvent.getId()).isNotNull();
    assertThat(savedEvent.getSentAt()).isNull();

    // Act: Wait for the poller to claim and publish the event
    await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              OutboxEvent updatedEvent =
                  outboxEventRepository.findById(savedEvent.getId()).orElseThrow();
              assertThat(updatedEvent.getSentAt()).isNotNull();
            });

    // Assert (Database): Verify the event is marked as sent
    OutboxEvent publishedEvent = outboxEventRepository.findById(savedEvent.getId()).orElseThrow();
    assertThat(publishedEvent.getSentAt()).isNotNull();
    assertThat(publishedEvent.getInProgressUntil()).isNull();

    // Assert (Kafka): Verify the message was published to Kafka
    ConsumerRecord<String, String> received = records.poll(5, TimeUnit.SECONDS);
    assertThat(received).isNotNull();
    assertThat(received.topic()).isEqualTo("OrderCreated");
    assertThat(received.key()).isEqualTo("order-12345");
    assertThat(received.value()).contains("John Doe");
    assertThat(received.value()).contains("12345");
  }
}
