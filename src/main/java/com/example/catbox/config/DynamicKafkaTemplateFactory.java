package com.example.catbox.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic factory for KafkaTemplate instances.
 * Creates templates on-demand for different clusters and evicts idle ones.
 */
@Configuration
public class DynamicKafkaTemplateFactory {

    private static final Logger logger = LoggerFactory.getLogger(DynamicKafkaTemplateFactory.class);

    private final KafkaClustersConfig clustersConfig;
    private final Map<String, KafkaTemplate<String, String>> templateCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> lastAccessTime = new ConcurrentHashMap<>();
    private final ScheduledExecutorService evictionScheduler = Executors.newSingleThreadScheduledExecutor();

    @Value("${outbox.kafka.factory.idle-eviction-time-minutes:30}")
    private long idleEvictionTimeMinutes;

    @Value("${outbox.kafka.factory.eviction-check-ms:300000}")
    private long evictionCheckMs;

    public DynamicKafkaTemplateFactory(KafkaClustersConfig clustersConfig) {
        this.clustersConfig = clustersConfig;
    }

    @PostConstruct
    public void init() {
        startEvictionScheduler();
    }

    /**
     * Get or create a KafkaTemplate for the specified cluster.
     */
    public KafkaTemplate<String, String> getTemplate(String clusterKey) {
        lastAccessTime.put(clusterKey, Instant.now());
        
        return templateCache.computeIfAbsent(clusterKey, key -> {
            KafkaProperties clusterProps = clustersConfig.getClusters().get(key);
            if (clusterProps == null) {
                throw new IllegalArgumentException("No Kafka cluster configuration found for key: " + key);
            }
            return createKafkaTemplate(clusterProps);
        });
    }

    /**
     * Create a KafkaTemplate from cluster properties.
     */
    private KafkaTemplate<String, String> createKafkaTemplate(KafkaProperties properties) {
        Map<String, Object> producerProps = new HashMap<>(properties.buildProducerProperties(null));
        
        // Ensure key and value serializers are set
        if (!producerProps.containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG)) {
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
                org.apache.kafka.common.serialization.StringSerializer.class);
        }
        if (!producerProps.containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)) {
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
                org.apache.kafka.common.serialization.StringSerializer.class);
        }
        
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Start the background eviction scheduler.
     */
    private void startEvictionScheduler() {
        evictionScheduler.scheduleAtFixedRate(() -> {
            try {
                evictIdleTemplates();
            } catch (Exception e) {
                // Log but don't let exceptions kill the scheduler
                logger.error("Error during template eviction", e);
            }
        }, evictionCheckMs, evictionCheckMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Evict templates that have been idle for too long.
     */
    private void evictIdleTemplates() {
        Instant cutoff = Instant.now().minusSeconds(idleEvictionTimeMinutes * 60);
        
        lastAccessTime.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(cutoff)) {
                String clusterKey = entry.getKey();
                KafkaTemplate<String, String> template = templateCache.remove(clusterKey);
                if (template != null) {
                    // Clean up the producer factory
                    try {
                        template.destroy();
                    } catch (Exception e) {
                        logger.error("Error destroying template for cluster: {}", clusterKey, e);
                    }
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Shutdown hook to clean up resources.
     */
    @PreDestroy
    public void shutdown() {
        evictionScheduler.shutdown();
        try {
            if (!evictionScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                evictionScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            evictionScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Clean up all templates
        templateCache.values().forEach(template -> {
            try {
                template.destroy();
            } catch (Exception e) {
                logger.error("Error destroying template during shutdown", e);
            }
        });
        templateCache.clear();
        lastAccessTime.clear();
    }
}
