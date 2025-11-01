package com.example.catbox.server.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaClustersConfig {
    private Map<String, KafkaProperties> clusters = new HashMap<>();

    public Map<String, KafkaProperties> getClusters() {
        return clusters;
    }

    public void setClusters(Map<String, KafkaProperties> clusters) {
        this.clusters = clusters;
    }
}
