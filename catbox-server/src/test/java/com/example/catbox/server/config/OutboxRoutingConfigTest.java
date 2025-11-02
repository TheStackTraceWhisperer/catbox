package com.example.catbox.server.config;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for OutboxRoutingConfig parsing logic.
 */
class OutboxRoutingConfigTest {

    @Test
    void getRoutingRule_handlesBackwardCompatibleStringFormat() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of("OrderCreated", "cluster-a"));
        
        // When
        RoutingRule rule = config.getRoutingRule("OrderCreated");
        
        // Then
        assertThat(rule).isNotNull();
        assertThat(rule.getClusters()).containsExactly("cluster-a");
        assertThat(rule.getOptional()).isEmpty();
        assertThat(rule.getStrategy()).isEqualTo(ClusterPublishingStrategy.ALL_MUST_SUCCEED);
    }

    @Test
    void getRoutingRule_handlesMultipleClustersWithStrategy() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of(
            "OrderStatusChanged", Map.of(
                "clusters", List.of("cluster-a", "cluster-b"),
                "strategy", "at-least-one"
            )
        ));
        
        // When
        RoutingRule rule = config.getRoutingRule("OrderStatusChanged");
        
        // Then
        assertThat(rule).isNotNull();
        assertThat(rule.getClusters()).containsExactly("cluster-a", "cluster-b");
        assertThat(rule.getOptional()).isEmpty();
        assertThat(rule.getStrategy()).isEqualTo(ClusterPublishingStrategy.AT_LEAST_ONE);
    }

    @Test
    void getRoutingRule_handlesOptionalClusters() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of(
            "InventoryAdjusted", Map.of(
                "clusters", List.of("cluster-a", "cluster-b"),
                "optional", List.of("cluster-c", "cluster-d"),
                "strategy", "all-must-succeed"
            )
        ));
        
        // When
        RoutingRule rule = config.getRoutingRule("InventoryAdjusted");
        
        // Then
        assertThat(rule).isNotNull();
        assertThat(rule.getClusters()).containsExactly("cluster-a", "cluster-b");
        assertThat(rule.getOptional()).containsExactly("cluster-c", "cluster-d");
        assertThat(rule.getStrategy()).isEqualTo(ClusterPublishingStrategy.ALL_MUST_SUCCEED);
    }

    @Test
    void getRoutingRule_handlesSingleClusterAsString() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of(
            "TestEvent", Map.of(
                "clusters", "cluster-a",
                "strategy", "at-least-one"
            )
        ));
        
        // When
        RoutingRule rule = config.getRoutingRule("TestEvent");
        
        // Then
        assertThat(rule).isNotNull();
        assertThat(rule.getClusters()).containsExactly("cluster-a");
    }

    @Test
    void getRoutingRule_handlesStrategyWithHyphens() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of(
            "TestEvent", Map.of(
                "clusters", List.of("cluster-a"),
                "strategy", "at-least-one"
            )
        ));
        
        // When
        RoutingRule rule = config.getRoutingRule("TestEvent");
        
        // Then
        assertThat(rule.getStrategy()).isEqualTo(ClusterPublishingStrategy.AT_LEAST_ONE);
    }

    @Test
    void getRoutingRule_handlesStrategyWithUnderscores() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of(
            "TestEvent", Map.of(
                "clusters", List.of("cluster-a"),
                "strategy", "ALL_MUST_SUCCEED"
            )
        ));
        
        // When
        RoutingRule rule = config.getRoutingRule("TestEvent");
        
        // Then
        assertThat(rule.getStrategy()).isEqualTo(ClusterPublishingStrategy.ALL_MUST_SUCCEED);
    }

    @Test
    void getRoutingRule_returnsNullForUnknownEventType() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of("OrderCreated", "cluster-a"));
        
        // When
        RoutingRule rule = config.getRoutingRule("UnknownEvent");
        
        // Then
        assertThat(rule).isNull();
    }

    @Test
    void getRoutingRule_throwsExceptionForInvalidFormat() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of("OrderCreated", 123)); // Invalid: Integer
        
        // When/Then
        assertThatThrownBy(() -> config.getRoutingRule("OrderCreated"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid routing rule format");
    }

    @Test
    void getRoutingRule_defaultsToAllMustSucceedWhenStrategyNotSpecified() {
        // Given
        OutboxRoutingConfig config = new OutboxRoutingConfig();
        config.setRules(Map.of(
            "TestEvent", Map.of(
                "clusters", List.of("cluster-a", "cluster-b")
                // No strategy specified
            )
        ));
        
        // When
        RoutingRule rule = config.getRoutingRule("TestEvent");
        
        // Then
        assertThat(rule.getStrategy()).isEqualTo(ClusterPublishingStrategy.ALL_MUST_SUCCEED);
    }
}
