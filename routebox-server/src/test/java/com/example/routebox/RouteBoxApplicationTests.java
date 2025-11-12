package com.example.routebox.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class CatboxApplicationTests {

  @Container
  static final MSSQLServerContainer<?> mssql =
      new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

  @Container
  static final KafkaContainer kafkaA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.1"));

  @Container
  static final KafkaContainer kafkaB =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.1"));

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
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
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    registry.add("spring.threads.virtual.enabled", () -> "true");

    registry.add("kafka.clusters.cluster-a.bootstrap-servers", kafkaA::getBootstrapServers);
    registry.add("kafka.clusters.cluster-b.bootstrap-servers", kafkaB::getBootstrapServers);

    // Kafka producer configuration
    registry.add(
        "kafka.clusters.cluster-a.producer.key-serializer",
        () -> "org.apache.kafka.common.serialization.StringSerializer");
    registry.add(
        "kafka.clusters.cluster-a.producer.value-serializer",
        () -> "org.apache.kafka.common.serialization.StringSerializer");
    registry.add("kafka.clusters.cluster-a.producer.properties.linger.ms", () -> "10");
    registry.add("kafka.clusters.cluster-a.producer.properties.acks", () -> "all");

    registry.add(
        "kafka.clusters.cluster-b.producer.key-serializer",
        () -> "org.apache.kafka.common.serialization.StringSerializer");
    registry.add(
        "kafka.clusters.cluster-b.producer.value-serializer",
        () -> "org.apache.kafka.common.serialization.StringSerializer");
    registry.add("kafka.clusters.cluster-b.producer.properties.linger.ms", () -> "10");
    registry.add("kafka.clusters.cluster-b.producer.properties.acks", () -> "all");

    // Outbox configuration
    registry.add("outbox.processing.claim-timeout", () -> "5m");
    registry.add("outbox.processing.batch-size", () -> "100");
    registry.add("outbox.processing.poll-fixed-delay", () -> "2s");
    registry.add("outbox.processing.poll-initial-delay", () -> "10s");
    registry.add("outbox.processing.archival-retention-days", () -> "7");
    registry.add("outbox.processing.max-permanent-retries", () -> "5");
    registry.add("outbox.kafka.factory.idle-eviction-time-minutes", () -> "30");
    registry.add("outbox.kafka.factory.eviction-check-ms", () -> "5m");

    // Server configuration
    registry.add("server.port", () -> "8081");
  }

  @Test
  void contextLoads() {
    // This test verifies that the application context loads successfully
  }
}
