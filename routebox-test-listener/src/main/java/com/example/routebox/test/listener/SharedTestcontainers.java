package com.example.routebox.test.listener;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * JVM-level singleton for managing Testcontainers. Starts MSSQL and Kafka containers once for the
 * entire build. Stops them gracefully using a JVM shutdown hook.
 */
public class SharedTestcontainers {

  // 1. Define static container instances
  public static final MSSQLServerContainer<?> mssql;
  public static final KafkaContainer kafkaA;
  public static final KafkaContainer kafkaB;

  static {
    // 2. Start all containers in a static block (runs once per JVM)
    mssql =
        new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withReuse(true); // Keep reuse for local runs

    kafkaA =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.1")).withReuse(true);

    kafkaB =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.1")).withReuse(true);

    // Start containers sequentially
    mssql.start();
    kafkaA.start();
    kafkaB.start();

    // 3. Set system properties for Spring Boot
    // This replaces all @DynamicPropertySource methods in test classes
    System.setProperty(
        "spring.datasource.url",
        mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
    System.setProperty("spring.datasource.username", mssql.getUsername());
    System.setProperty("spring.datasource.password", mssql.getPassword());
    System.setProperty(
        "spring.datasource.driver-class-name", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    System.setProperty(
        "spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");

    // Kafka properties for E2E tests
    System.setProperty("kafka.clusters.cluster-a.bootstrap-servers", kafkaA.getBootstrapServers());
    System.setProperty("kafka.clusters.cluster-b.bootstrap-servers", kafkaB.getBootstrapServers());

    // 4. Register a JVM shutdown hook to stop containers
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  System.out.println("JVM shutting down. Stopping Testcontainers...");
                  mssql.stop();
                  kafkaA.stop();
                  kafkaB.stop();
                }));
  }

  /**
   * A simple method to be called from test classes to ensure this singleton is initialized.
   */
  public static void ensureInitialized() {
    // This method is intentionally empty.
    // Calling it will trigger the static block if it hasn't run yet.
  }
}
