package com.example.catbox.server.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for outbox event routing rules. Supports both simple string-based routing (backward
 * compatible) and complex multi-cluster routing.
 */
@Configuration
@ConfigurationProperties(prefix = "outbox.routing")
@Getter
@Setter
public class OutboxRoutingConfig {
  /**
   * Raw rules map from configuration. Can contain either String values (single cluster) or Map
   * values (multi-cluster config).
   */
  private Map<String, Object> rules = new HashMap<>();

  /**
   * Gets the routing rule for a given event type. Handles both old String format and new
   * RoutingRule format.
   *
   * @param eventType The event type to look up
   * @return RoutingRule object, or null if not found
   */
  public RoutingRule getRoutingRule(String eventType) {
    Object ruleConfig = rules.get(eventType);
    if (ruleConfig == null) {
      return null;
    }

    // Handle backward compatible String format: "OrderCreated: cluster-a"
    if (ruleConfig instanceof String) {
      return RoutingRule.singleCluster((String) ruleConfig);
    }

    // Handle new Map format with clusters, optional, and strategy
    if (ruleConfig instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> ruleMap = (Map<String, Object>) ruleConfig;
      return parseRoutingRule(ruleMap);
    }

    throw new IllegalArgumentException(
        "Invalid routing rule format for event type: "
            + eventType
            + ". Expected String or Map, got: "
            + ruleConfig.getClass().getName());
  }

  /** Parses a routing rule from a map configuration. */
  @SuppressWarnings("unchecked")
  private RoutingRule parseRoutingRule(Map<String, Object> ruleMap) {
    RoutingRule rule = new RoutingRule();

    // Parse clusters (required)
    Object clustersObj = ruleMap.get("clusters");
    if (clustersObj instanceof java.util.List) {
      rule.setClusters((java.util.List<String>) clustersObj);
    } else if (clustersObj instanceof String) {
      rule.setClusters(java.util.List.of((String) clustersObj));
    } else if (clustersObj instanceof Map) {
      // Handle indexed properties like clusters[0], clusters[1] from DynamicPropertyRegistry
      // These come in as Map<String, Object> with keys "0", "1", etc.
      Map<String, Object> clusterMap = (Map<String, Object>) clustersObj;
      java.util.List<String> clusterList = new java.util.ArrayList<>();
      // Sort by index and extract values
      clusterMap.keySet().stream()
          .sorted((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)))
          .forEach(key -> clusterList.add((String) clusterMap.get(key)));
      rule.setClusters(clusterList);
    } else if (clustersObj != null) {
      throw new IllegalArgumentException(
          "Invalid 'clusters' format. Expected List, String, or Map, got: "
              + clustersObj.getClass().getName());
    }

    // Parse optional clusters
    Object optionalObj = ruleMap.get("optional");
    if (optionalObj instanceof java.util.List) {
      rule.setOptional((java.util.List<String>) optionalObj);
    } else if (optionalObj instanceof String) {
      rule.setOptional(java.util.List.of((String) optionalObj));
    } else if (optionalObj instanceof Map) {
      // Handle indexed properties like optional[0], optional[1]
      Map<String, Object> optionalMap = (Map<String, Object>) optionalObj;
      java.util.List<String> optionalList = new java.util.ArrayList<>();
      optionalMap.keySet().stream()
          .sorted((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)))
          .forEach(key -> optionalList.add((String) optionalMap.get(key)));
      rule.setOptional(optionalList);
    }

    // Parse strategy
    Object strategyObj = ruleMap.get("strategy");
    if (strategyObj instanceof String) {
      String strategyStr = ((String) strategyObj).toUpperCase().replace("-", "_");
      rule.setStrategy(ClusterPublishingStrategy.valueOf(strategyStr));
    }

    return rule;
  }
}
