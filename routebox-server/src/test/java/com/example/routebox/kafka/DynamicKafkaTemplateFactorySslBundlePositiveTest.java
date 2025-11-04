package com.example.routebox.server.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.routebox.server.config.DynamicKafkaTemplateFactory;
import com.example.routebox.server.config.KafkaClustersConfig;
import com.example.testconfig.TestKafkaOnlyApplication;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.kafka.SslBundleSslEngineFactory;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Tests to verify SSL bundle configuration in DynamicKafkaTemplateFactory. This test uses mocked
 * SslBundles to avoid needing actual SSL certificates.
 */
@SpringBootTest(
    classes = {
      TestKafkaOnlyApplication.class,
      DynamicKafkaTemplateFactorySslBundlePositiveTest.TestConfig.class
    })
@DirtiesContext
class DynamicKafkaTemplateFactorySslBundlePositiveTest {

  @Autowired private DynamicKafkaTemplateFactory factory;

  @Test
  void testClusterWithSslBundleHasSslConfiguration() {
    // Given: a cluster key configured with SSL bundle
    String clusterKey = "ssl-cluster";

    // When: we get a template from the factory
    KafkaTemplate<String, String> template = factory.getTemplate(clusterKey);

    // Then: the template should not be null
    assertThat(template).isNotNull();

    // And: the producer factory should have SSL bundle configuration
    DefaultKafkaProducerFactory<String, String> producerFactory =
        (DefaultKafkaProducerFactory<String, String>) template.getProducerFactory();
    assertThat(producerFactory).isNotNull();

    // And: the configuration should include SSL engine factory class
    Map<String, Object> configProps = producerFactory.getConfigurationProperties();
    assertThat(configProps)
        .as("Producer config should contain SSL engine factory class when SSL bundle is configured")
        .containsKey(SslConfigs.SSL_ENGINE_FACTORY_CLASS_CONFIG);

    assertThat(configProps.get(SslConfigs.SSL_ENGINE_FACTORY_CLASS_CONFIG))
        .as("SSL engine factory class should be SslBundleSslEngineFactory")
        .isEqualTo(SslBundleSslEngineFactory.class.getName());

    // And: the configuration should include the SSL bundle instance
    assertThat(configProps)
        .as("Producer config should contain SSL bundle when SSL bundle is configured")
        .containsKey(SslBundle.class.getName());

    assertThat(configProps.get(SslBundle.class.getName()))
        .as("SSL bundle should be present in configuration")
        .isNotNull()
        .isInstanceOf(SslBundle.class);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public KafkaClustersConfig kafkaClustersConfig() {
      KafkaClustersConfig config = new KafkaClustersConfig();
      Map<String, KafkaProperties> clusters = new HashMap<>();

      // Configure a cluster with SSL bundle
      KafkaProperties kafkaProps = new KafkaProperties();
      kafkaProps.setBootstrapServers(java.util.Collections.singletonList("localhost:9093"));

      // Configure producer serializers
      Map<String, String> producerProps = new HashMap<>();
      producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      producerProps.put(
          "value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
      kafkaProps.getProducer().getProperties().putAll(producerProps);

      // Configure SSL with bundle - using the getSsl() object which is auto-created by Spring
      kafkaProps.getSsl().setBundle("test-bundle");

      clusters.put("ssl-cluster", kafkaProps);
      config.setClusters(clusters);

      return config;
    }

    @Bean
    public SslBundles sslBundles() {
      // Mock SslBundles to return a test SSL bundle
      SslBundles mockBundles = mock(SslBundles.class);
      SslBundle mockBundle = mock(SslBundle.class);

      when(mockBundles.getBundle("test-bundle")).thenReturn(mockBundle);

      return mockBundles;
    }
  }
}
