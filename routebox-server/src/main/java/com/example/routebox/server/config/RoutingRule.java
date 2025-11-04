package com.example.routebox.server.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for routing an event type to one or more Kafka clusters.
 * Supports both single cluster routing (backward compatible) and multi-cluster routing.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutingRule {
    /**
     * List of required cluster keys. All of these must succeed if strategy is ALL_MUST_SUCCEED.
     * At least one must succeed if strategy is AT_LEAST_ONE.
     */
    private List<String> clusters = new ArrayList<>();
    
    /**
     * List of optional cluster keys. Failures on these clusters are ignored.
     * Only applicable when strategy is ALL_MUST_SUCCEED.
     */
    private List<String> optional = new ArrayList<>();
    
    /**
     * Publishing strategy: AT_LEAST_ONE or ALL_MUST_SUCCEED.
     * Defaults to ALL_MUST_SUCCEED for backward compatibility.
     */
    private ClusterPublishingStrategy strategy = ClusterPublishingStrategy.ALL_MUST_SUCCEED;
    
    /**
     * Creates a simple routing rule with a single cluster.
     * This is for backward compatibility with the old String-based routing.
     */
    public static RoutingRule singleCluster(String clusterKey) {
        RoutingRule rule = new RoutingRule();
        rule.clusters = List.of(clusterKey);
        rule.strategy = ClusterPublishingStrategy.ALL_MUST_SUCCEED;
        return rule;
    }
}
