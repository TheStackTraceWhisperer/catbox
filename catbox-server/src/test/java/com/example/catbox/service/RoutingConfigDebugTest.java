package com.example.catbox.service;

import com.example.catbox.server.CatboxServerApplication;
import com.example.catbox.server.config.OutboxRoutingConfig;
import com.example.catbox.server.config.RoutingRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Debug test to verify routing configuration is being loaded correctly.
 */
@SpringBootTest(classes = CatboxServerApplication.class)
@Testcontainers
class RoutingConfigDebugTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense();

    @DynamicPropertySource
    static void sqlProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
        registry.add("spring.datasource.username", mssql::getUsername);
        registry.add("spring.datasource.password", mssql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        
        // Configure multi-cluster routing
        registry.add("outbox.routing.rules.OrderCreated.clusters[0]", () -> "cluster-a");
        registry.add("outbox.routing.rules.OrderCreated.clusters[1]", () -> "cluster-b");
        registry.add("outbox.routing.rules.OrderCreated.strategy", () -> "all-must-succeed");
    }

    @Autowired
    OutboxRoutingConfig routingConfig;

    @Test
    void verifyRoutingRuleIsLoaded() {
        // When
        RoutingRule rule = routingConfig.getRoutingRule("OrderCreated");
        
        // Then
        System.out.println("Rule: " + rule);
        System.out.println("Clusters: " + (rule != null ? rule.getClusters() : "null"));
        System.out.println("Strategy: " + (rule != null ? rule.getStrategy() : "null"));
        System.out.println("Optional: " + (rule != null ? rule.getOptional() : "null"));
        
        assertThat(rule).isNotNull();
        assertThat(rule.getClusters()).containsExactly("cluster-a", "cluster-b");
        assertThat(rule.getStrategy()).isEqualTo(com.example.catbox.server.config.ClusterPublishingStrategy.ALL_MUST_SUCCEED);
    }
}
