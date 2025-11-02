package com.example.catbox.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "outbox.routing")
@Getter
@Setter
public class OutboxRoutingConfig {
    private Map<String, String> rules = new HashMap<>();
}
