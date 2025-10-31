package com.example.catbox.kafka;

import com.example.catbox.config.DynamicKafkaTemplateFactory;
import com.example.catbox.config.OutboxRoutingConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the DYNAMIC Kafka factory and routing.
 * This test verifies that the factory can:
 * 1. Read test properties for a cluster
 * 2. Build a template for that cluster
 * 3. Send a message to the EmbeddedKafka broker
 */
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {KafkaIntegrationTest.TEST_TOPIC})
@TestPropertySource(properties = {
    // 1. Define a "test-cluster" and point it to the embedded broker
    "kafka.clusters.test-cluster.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "kafka.clusters.test-cluster.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "kafka.clusters.test-cluster.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",

    // 2. Define a routing rule to use the "test-cluster"
    "outbox.routing.rules.TestEvent=test-cluster"
})
@DirtiesContext
class KafkaIntegrationTest {

    public static final String TEST_TOPIC = "test-outbox-events";
    private static final String TEST_EVENT_TYPE = "TestEvent";
    private static final String TEST_CLUSTER_KEY = "test-cluster";

    @Autowired
    private DynamicKafkaTemplateFactory kafkaTemplateFactory;

    @Autowired
    private OutboxRoutingConfig routingConfig;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, String> container;
    private BlockingQueue<ConsumerRecord<String, String>> records;

    @BeforeEach
    void setUp() {
        // Setup consumer
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        
        ContainerProperties containerProps = new ContainerProperties(TEST_TOPIC);
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);

        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) records::add);
        container.start();

        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    void tearDown() {
        if (container != null) {
            container.stop();
        }
    }

    @Test
    void testDynamicFactorySendAndReceive() throws Exception {
        // 1. Verify the routing rule was loaded
        String clusterKey = routingConfig.getRules().get(TEST_EVENT_TYPE);
        assertThat(clusterKey).isEqualTo(TEST_CLUSTER_KEY);

        // 2. Get the DYNAMIC template from the factory
        KafkaTemplate<String, String> template = kafkaTemplateFactory.getTemplate(clusterKey);
        assertThat(template).isNotNull();

        // 3. Send message
        String testKey = "order-123";
        String testValue = "{\"orderId\":123,\"status\":\"CREATED\"}";
        template.send(TEST_TOPIC, testKey, testValue).get(10, TimeUnit.SECONDS);

        // 4. Receive and verify
        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo(testKey);
        assertThat(received.value()).isEqualTo(testValue);

        // 5. Verify caching
        KafkaTemplate<String, String> cachedTemplate = kafkaTemplateFactory.getTemplate(clusterKey);
        assertThat(cachedTemplate).isSameAs(template);
    }
}
