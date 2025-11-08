package com.example.routebox.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import com.example.routebox.common.util.TimeBasedUuidGenerator;
import com.example.routebox.test.listener.SharedTestcontainers;
import jakarta.persistence.EntityManager;
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
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-End test for dynamic Kafka routing across multiple clusters. Tests that events are routed
 * to the correct Kafka cluster based on routing rules.
 */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class E2EPollerMultiClusterTest {

  // Use unique event types for this test run to avoid cross-test contamination
  private static final String ORDER_EVENT_TYPE = "OrderCreated-" + TimeBasedUuidGenerator.generate().toString().substring(0, 8);
  private static final String INVENTORY_EVENT_TYPE = "InventoryAdjusted-" + TimeBasedUuidGenerator.generate().toString().substring(0, 8);

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // Routing configuration - route different unique event types to different clusters
    // Topic names will be derived from event types
    registry.add("outbox.routing.rules." + ORDER_EVENT_TYPE, () -> "cluster-a");
    registry.add("outbox.routing.rules." + INVENTORY_EVENT_TYPE, () -> "cluster-b");

    // Speed up polling for tests
    registry.add("outbox.processing.poll-fixed-delay", () -> "500ms");
    registry.add("outbox.processing.poll-initial-delay", () -> "1s");
  }

  @Autowired private OutboxEventRepository outboxEventRepository;

  private KafkaMessageListenerContainer<String, String> containerOrderCreatedA;
  private KafkaMessageListenerContainer<String, String> containerInventoryAdjustedA;
  private KafkaMessageListenerContainer<String, String> containerOrderCreatedB;
  private KafkaMessageListenerContainer<String, String> containerInventoryAdjustedB;

  private BlockingQueue<ConsumerRecord<String, String>> recordsOrderCreatedA;
  private BlockingQueue<ConsumerRecord<String, String>> recordsInventoryAdjustedA;
  private BlockingQueue<ConsumerRecord<String, String>> recordsOrderCreatedB;
  private BlockingQueue<ConsumerRecord<String, String>> recordsInventoryAdjustedB;

  @BeforeEach
  void setUp() {
    // Use unique consumer group IDs
    String uniqueSuffix = TimeBasedUuidGenerator.generate().toString().substring(0, 8);
    
    // Set up consumers for unique event type topics on each cluster
    // Topic names equal event types
    recordsOrderCreatedA = new LinkedBlockingQueue<>();
    containerOrderCreatedA =
        createConsumer(
            SharedTestcontainers.kafkaA.getBootstrapServers(),
            ORDER_EVENT_TYPE,
            "group-a-order-" + uniqueSuffix,
            recordsOrderCreatedA);
    containerOrderCreatedA.start();

    recordsInventoryAdjustedA = new LinkedBlockingQueue<>();
    containerInventoryAdjustedA =
        createConsumer(
            SharedTestcontainers.kafkaA.getBootstrapServers(),
            INVENTORY_EVENT_TYPE,
            "group-a-inventory-" + uniqueSuffix,
            recordsInventoryAdjustedA);
    containerInventoryAdjustedA.start();

    recordsOrderCreatedB = new LinkedBlockingQueue<>();
    containerOrderCreatedB =
        createConsumer(
            SharedTestcontainers.kafkaB.getBootstrapServers(),
            ORDER_EVENT_TYPE,
            "group-b-order-" + uniqueSuffix,
            recordsOrderCreatedB);
    containerOrderCreatedB.start();

    recordsInventoryAdjustedB = new LinkedBlockingQueue<>();
    containerInventoryAdjustedB =
        createConsumer(
            SharedTestcontainers.kafkaB.getBootstrapServers(),
            INVENTORY_EVENT_TYPE,
            "group-b-inventory-" + uniqueSuffix,
            recordsInventoryAdjustedB);
    containerInventoryAdjustedB.start();
    
    // Wait for all consumers to be assigned partitions
    ContainerTestUtils.waitForAssignment(containerOrderCreatedA, 1);
    ContainerTestUtils.waitForAssignment(containerInventoryAdjustedA, 1);
    ContainerTestUtils.waitForAssignment(containerOrderCreatedB, 1);
    ContainerTestUtils.waitForAssignment(containerInventoryAdjustedB, 1);
  }

  @AfterEach
  void tearDown() {
    if (containerOrderCreatedA != null) containerOrderCreatedA.stop();
    if (containerInventoryAdjustedA != null) containerInventoryAdjustedA.stop();
    if (containerOrderCreatedB != null) containerOrderCreatedB.stop();
    if (containerInventoryAdjustedB != null) containerInventoryAdjustedB.stop();
  }

  private KafkaMessageListenerContainer<String, String> createConsumer(
      String bootstrapServers,
      String topic,
      String groupId,
      BlockingQueue<ConsumerRecord<String, String>> queue) {
    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(consumerProps);
    ContainerProperties containerProps = new ContainerProperties(topic);
    KafkaMessageListenerContainer<String, String> container =
        new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
    container.setupMessageListener((MessageListener<String, String>) queue::add);
    return container;
  }

  /**
   * Gap 4: Tests that events are routed to the correct Kafka clusters. Creates OrderCreated and
   * InventoryAdjusted events and verifies they are published to cluster-a and cluster-b
   * respectively.
   */
  @Test
  void testPollerRoutesEventsToCorrectClusters() throws Exception {
    // Arrange: Create OrderCreated event with unique event type (should go to cluster-a)
    String orderId = "order-" + TimeBasedUuidGenerator.generate().toString();
    OutboxEvent orderEvent =
        new OutboxEvent(
            "Order",
            orderId,
            ORDER_EVENT_TYPE,  // Use unique event type
            "{\"orderId\":\"" + orderId + "\",\"customerName\":\"Bob\",\"amount\":199.99}");
    OutboxEvent savedOrderEvent = outboxEventRepository.save(orderEvent);

    // Arrange: Create InventoryAdjusted event with unique event type (should go to cluster-b)
    String itemId = "item-" + TimeBasedUuidGenerator.generate().toString();
    OutboxEvent inventoryEvent =
        new OutboxEvent(
            "Inventory",
            itemId,
            INVENTORY_EVENT_TYPE,  // Use unique event type
            "{\"itemId\":\"" + itemId + "\",\"quantity\":50}");
    OutboxEvent savedInventoryEvent = outboxEventRepository.save(inventoryEvent);

    // Act: Wait for the poller to claim and publish both events
    await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(
            () -> {
              OutboxEvent updatedOrderEvent =
                  outboxEventRepository.findById(savedOrderEvent.getId()).orElseThrow();
              OutboxEvent updatedInventoryEvent =
                  outboxEventRepository.findById(savedInventoryEvent.getId()).orElseThrow();
              assertThat(updatedOrderEvent.getSentAt()).isNotNull();
              assertThat(updatedInventoryEvent.getSentAt()).isNotNull();
            });

    // Assert: OrderCreated should arrive only on cluster-a
    ConsumerRecord<String, String> receivedOrderA = recordsOrderCreatedA.poll(10, TimeUnit.SECONDS);
    assertThat(receivedOrderA).isNotNull();
    assertThat(receivedOrderA.topic()).isEqualTo(ORDER_EVENT_TYPE);  // Topic equals event type
    assertThat(receivedOrderA.key()).isEqualTo(orderId);
    assertThat(receivedOrderA.value()).contains("Bob");

    // Assert: InventoryAdjusted should arrive only on cluster-b
    ConsumerRecord<String, String> receivedInventoryB =
        recordsInventoryAdjustedB.poll(10, TimeUnit.SECONDS);
    assertThat(receivedInventoryB).isNotNull();
    assertThat(receivedInventoryB.topic()).isEqualTo(INVENTORY_EVENT_TYPE);  // Topic equals event type
    assertThat(receivedInventoryB.key()).isEqualTo(itemId);
    assertThat(receivedInventoryB.value()).contains(itemId);

    // Assert: Verify cross-contamination did not occur
    // OrderCreated should NOT arrive on cluster-b
    ConsumerRecord<String, String> shouldBeNullOrderB =
        recordsOrderCreatedB.poll(3, TimeUnit.SECONDS);
    assertThat(shouldBeNullOrderB).isNull();

    // InventoryAdjusted should NOT arrive on cluster-a
    ConsumerRecord<String, String> shouldBeNullInventoryA =
        recordsInventoryAdjustedA.poll(3, TimeUnit.SECONDS);
    assertThat(shouldBeNullInventoryA).isNull();
  }
}
