package com.example.catbox.server.kafka;

import com.example.testconfig.TestKafkaOnlyApplication;
import com.example.catbox.server.config.DynamicKafkaTemplateFactory;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.SslBundleSslEngineFactory;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests to verify that DynamicKafkaTemplateFactory properly handles SSL Bundle configuration.
 */
@SpringBootTest(classes = TestKafkaOnlyApplication.class)
@TestPropertySource(properties = {
    "kafka.clusters.non-ssl-cluster.bootstrap-servers=localhost:9092",
    "kafka.clusters.non-ssl-cluster.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "kafka.clusters.non-ssl-cluster.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
@DirtiesContext
class DynamicKafkaTemplateFactorySslBundleTest {

    @Autowired
    private DynamicKafkaTemplateFactory factory;

    @Test
    void testClusterWithoutSslBundleDoesNotHaveSslConfig() {
        // Given: a cluster without SSL bundle configured
        String clusterKey = "non-ssl-cluster";
        
        // When: we get a template from the factory
        KafkaTemplate<String, String> template = factory.getTemplate(clusterKey);
        
        // Then: the template should not be null
        assertThat(template).isNotNull();
        
        // And: the producer factory should not have SSL bundle configuration
        DefaultKafkaProducerFactory<String, String> producerFactory = 
            (DefaultKafkaProducerFactory<String, String>) template.getProducerFactory();
        assertThat(producerFactory).isNotNull();
        
        // And: the configuration should NOT include SSL engine factory or SSL bundle
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        assertThat(configProps)
            .as("Producer config should not contain SSL engine factory class when no SSL bundle is configured")
            .doesNotContainKey(SslConfigs.SSL_ENGINE_FACTORY_CLASS_CONFIG);
        
        assertThat(configProps)
            .as("Producer config should not contain SSL bundle when no SSL bundle is configured")
            .doesNotContainKey(SslBundle.class.getName());
    }

    @Test
    void testMissingClusterThrowsException() {
        // Given: a cluster key that doesn't exist in configuration
        String clusterKey = "missing-cluster";
        
        // When/Then: attempting to get a template should throw an exception
        assertThatThrownBy(() -> factory.getTemplate(clusterKey))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No Kafka cluster configuration found for key: missing-cluster");
    }
}
