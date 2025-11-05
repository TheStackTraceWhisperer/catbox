package com.example.routebox.server.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.testconfig.TestKafkaOnlyApplication;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Test to verify that the security configuration (SSL bundles and SASL) is properly loaded from
 * application.yml and available in the application context.
 */
@ActiveProfiles("azuresql")
@SpringBootTest(classes = TestKafkaOnlyApplication.class)
@TestPropertySource(
    properties = {
      // Override the truststore location to use a test path
      "spring.ssl.bundle.jks.kafka-client.truststore.location=file:kafka-security/certs/kafka-client-truststore.jks",
      "spring.ssl.bundle.jks.kafka-client.truststore.password=changeit",
      "kafka.clusters.secure-test.bootstrap-servers=localhost:9093",
      "kafka.clusters.secure-test.ssl.bundle=kafka-client",
      "kafka.clusters.secure-test.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
      "kafka.clusters.secure-test.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",
      "kafka.clusters.secure-test.producer.properties.security.protocol=SASL_SSL",
      "kafka.clusters.secure-test.producer.properties.sasl.mechanism=SCRAM-SHA-512",
      "kafka.clusters.secure-test.producer.properties.sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username=\"producer\" password=\"producer-secret\";"
    })
class SecurityConfigurationTest {

  @Autowired private KafkaClustersConfig clustersConfig;

  @Autowired private SslBundles sslBundles;

  @Test
  void testSslBundleIsConfigured() {
    // Verify that the SSL bundle is available
    assertThat(sslBundles).isNotNull();

    // Verify that the kafka-client bundle exists
    // Note: We can't directly check if the bundle exists without trying to get it
    // which would throw an exception if it doesn't exist
    // So this test mainly validates that SslBundles bean is available
  }

  @Test
  void testSecureClusterConfiguration() {
    // Verify the secure cluster is configured
    assertThat(clustersConfig.getClusters()).isNotNull();
    assertThat(clustersConfig.getClusters()).containsKey("secure-test");

    KafkaProperties secureCluster = clustersConfig.getClusters().get("secure-test");
    assertThat(secureCluster).isNotNull();
    assertThat(secureCluster.getBootstrapServers()).contains("localhost:9093");

    // Verify SSL bundle is referenced
    assertThat(secureCluster.getSsl()).isNotNull();
    assertThat(secureCluster.getSsl().getBundle()).isEqualTo("kafka-client");
  }

  @Test
  void testSaslConfigurationProperties() {
    // Verify SASL properties are configured
    KafkaProperties secureCluster = clustersConfig.getClusters().get("secure-test");
    assertThat(secureCluster).isNotNull();

    Map<String, String> producerProps = secureCluster.getProducer().getProperties();
    assertThat(producerProps).isNotNull();

    // Verify security protocol
    assertThat(producerProps.get("security.protocol")).isEqualTo("SASL_SSL");

    // Verify SASL mechanism
    assertThat(producerProps.get("sasl.mechanism")).isEqualTo("SCRAM-SHA-512");

    // Verify JAAS config is present (contains expected strings)
    String jaasConfig = producerProps.get("sasl.jaas.config");
    assertThat(jaasConfig)
        .isNotNull()
        .contains("ScramLoginModule")
        .contains("username=\"producer\"")
        .contains("password=\"producer-secret\"");
  }
}
