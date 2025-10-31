package com.example.catbox.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "outbox.routing")
public class OutboxRoutingConfig {
  private Map<String, String> rules = new HashMap<>();

  public Map<String, String> getRules() {
    return rules;
  }

  public void setRules(Map<String, String> rules) {
    this.rules = rules;
  }
}
