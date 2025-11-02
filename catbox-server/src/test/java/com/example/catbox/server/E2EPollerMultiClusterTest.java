package com.example.catbox.server;

import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
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
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * End-to-End test for dynamic Kafka routing across multiple clusters.
 * Tests that events are routed to the correct Kafka cluster based on routing rules.
 */
@SpringBootTest(classes = CatboxServerApplication.class)
@Testcontainers
class E2EPollerMultiClusterTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withPassword("YourStrong@Passw0rd");

    @Container
    static KafkaContainer kafkaA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.1"));

    @Container
    static KafkaContainer kafkaB = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.1"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Database configuration
        registry.add("spring.datasource.url", () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
        registry.add("spring.datasource.username", mssql::getUsername);
        registry.add("spring.datasource.password", mssql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        
        // Kafka cluster A configuration
        registry.add("kafka.clusters.cluster-a.bootstrap-servers", kafkaA::getBootstrapServers);
        registry.add("kafka.clusters.cluster-a.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("kafka.clusters.cluster-a.producer.value-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        
        // Kafka cluster B configuration
        registry.add("kafka.clusters.cluster-b.bootstrap-servers", kafkaB::getBootstrapServers);
        registry.add("kafka.clusters.cluster-b.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("kafka.clusters.cluster-b.producer.value-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        
        // Routing configuration - route different event types to different clusters
        registry.add("outbox.routing.rules.OrderCreated", () -> "cluster-a");
        registry.add("outbox.routing.rules.InventoryAdjusted", () -> "cluster-b");
        
        // Speed up polling for tests
        registry.add("outbox.processing.poll-fixed-delay-ms", () -> "500");
        registry.add("outbox.processing.poll-initial-delay-ms", () -> "1000");
    }

    @Autowired
    private OutboxEventRepository outboxEventRepository;

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
        // Set up consumer for OrderCreated on cluster-a
        recordsOrderCreatedA = new LinkedBlockingQueue<>();
        containerOrderCreatedA = createConsumer(kafkaA.getBootstrapServers(), "OrderCreated", "group-a-order", recordsOrderCreatedA);
        containerOrderCreatedA.start();

        // Set up consumer for InventoryAdjusted on cluster-a (should receive nothing)
        recordsInventoryAdjustedA = new LinkedBlockingQueue<>();
        containerInventoryAdjustedA = createConsumer(kafkaA.getBootstrapServers(), "InventoryAdjusted", "group-a-inventory", recordsInventoryAdjustedA);
        containerInventoryAdjustedA.start();

        // Set up consumer for OrderCreated on cluster-b (should receive nothing)
        recordsOrderCreatedB = new LinkedBlockingQueue<>();
        containerOrderCreatedB = createConsumer(kafkaB.getBootstrapServers(), "OrderCreated", "group-b-order", recordsOrderCreatedB);
        containerOrderCreatedB.start();

        // Set up consumer for InventoryAdjusted on cluster-b
        recordsInventoryAdjustedB = new LinkedBlockingQueue<>();
        containerInventoryAdjustedB = createConsumer(kafkaB.getBootstrapServers(), "InventoryAdjusted", "group-b-inventory", recordsInventoryAdjustedB);
        containerInventoryAdjustedB.start();
    }

    @AfterEach
    void tearDown() {
        if (containerOrderCreatedA != null) containerOrderCreatedA.stop();
        if (containerInventoryAdjustedA != null) containerInventoryAdjustedA.stop();
        if (containerOrderCreatedB != null) containerOrderCreatedB.stop();
        if (containerInventoryAdjustedB != null) containerInventoryAdjustedB.stop();
    }

    private KafkaMessageListenerContainer<String, String> createConsumer(
            String bootstrapServers, String topic, String groupId, BlockingQueue<ConsumerRecord<String, String>> queue) {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        ContainerProperties containerProps = new ContainerProperties(topic);
        KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        container.setupMessageListener((MessageListener<String, String>) queue::add);
        return container;
    }

    /**
     * Gap 4: Tests that events are routed to the correct Kafka clusters.
     * Creates OrderCreated and InventoryAdjusted events and verifies they
     * are published to cluster-a and cluster-b respectively.
     */
    @Test
    void testPollerRoutesEventsToCorrectClusters() throws Exception {
        // Arrange: Create OrderCreated event (should go to cluster-a)
        OutboxEvent orderEvent = new OutboxEvent(
                "Order",
                "order-999",
                "OrderCreated",
                "{\"orderId\":999,\"customerName\":\"Bob\",\"amount\":199.99}"
        );
        OutboxEvent savedOrderEvent = outboxEventRepository.save(orderEvent);

        // Arrange: Create InventoryAdjusted event (should go to cluster-b)
        OutboxEvent inventoryEvent = new OutboxEvent(
                "Inventory",
                "item-555",
                "InventoryAdjusted",
                "{\"itemId\":555,\"quantity\":50}"
        );
        OutboxEvent savedInventoryEvent = outboxEventRepository.save(inventoryEvent);

        // Act: Wait for the poller to claim and publish both events
        await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    OutboxEvent updatedOrderEvent = outboxEventRepository.findById(savedOrderEvent.getId()).orElseThrow();
                    OutboxEvent updatedInventoryEvent = outboxEventRepository.findById(savedInventoryEvent.getId()).orElseThrow();
                    assertThat(updatedOrderEvent.getSentAt()).isNotNull();
                    assertThat(updatedInventoryEvent.getSentAt()).isNotNull();
                });

        // Assert: OrderCreated should arrive only on cluster-a
        ConsumerRecord<String, String> receivedOrderA = recordsOrderCreatedA.poll(5, TimeUnit.SECONDS);
        assertThat(receivedOrderA).isNotNull();
        assertThat(receivedOrderA.topic()).isEqualTo("OrderCreated");
        assertThat(receivedOrderA.key()).isEqualTo("order-999");
        assertThat(receivedOrderA.value()).contains("Bob");

        // Assert: InventoryAdjusted should arrive only on cluster-b
        ConsumerRecord<String, String> receivedInventoryB = recordsInventoryAdjustedB.poll(5, TimeUnit.SECONDS);
        assertThat(receivedInventoryB).isNotNull();
        assertThat(receivedInventoryB.topic()).isEqualTo("InventoryAdjusted");
        assertThat(receivedInventoryB.key()).isEqualTo("item-555");
        assertThat(receivedInventoryB.value()).contains("555");

        // Assert: Verify cross-contamination did not occur
        // OrderCreated should NOT arrive on cluster-b
        ConsumerRecord<String, String> shouldBeNullOrderB = recordsOrderCreatedB.poll(2, TimeUnit.SECONDS);
        assertThat(shouldBeNullOrderB).isNull();

        // InventoryAdjusted should NOT arrive on cluster-a
        ConsumerRecord<String, String> shouldBeNullInventoryA = recordsInventoryAdjustedA.poll(2, TimeUnit.SECONDS);
        assertThat(shouldBeNullInventoryA).isNull();
    }
}
