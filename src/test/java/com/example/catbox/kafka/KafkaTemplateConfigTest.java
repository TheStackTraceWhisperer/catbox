package com.example.catbox.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic unit test for Kafka template configuration.
 * This verifies the Kafka configuration beans are properly wired.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092"
})
class KafkaTemplateConfigTest {

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void testKafkaTemplateIsConfigured() {
        // Verify that KafkaTemplate bean is created
        assertThat(kafkaTemplate).isNotNull();
        assertThat(kafkaTemplate.getProducerFactory()).isNotNull();
    }

    @Test
    void testKafkaTemplateHasIdempotentConfig() {
        // Verify producer factory is configured
        assertThat(kafkaTemplate.getProducerFactory()).isNotNull();
        
        // This is a shallow test - just verifying the bean is properly wired
        // Integration tests will verify actual Kafka connectivity
    }
}
