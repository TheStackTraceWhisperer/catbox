package com.example.catbox.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Creates, registers, and caches Spring-managed KafkaTemplate beans on demand.
 * This service is the core of the dynamic routing, allowing us to
 * add new clusters without any Java code changes.
 *
 * It also evicts and destroys idle beans to conserve resources.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicKafkaTemplateFactory implements ApplicationContextAware {

    private final KafkaClustersConfig clustersConfig;
    private ConfigurableApplicationContext applicationContext;

    // This is our thread-safe cache: Map<ClusterKey, KafkaTemplate>
    private final Map<String, KafkaTemplate<String, String>> templateCache = new ConcurrentHashMap<>();

    // Tracks last access time for eviction: Map<ClusterKey, Timestamp>
    private final Map<String, Long> lastAccessTime = new ConcurrentHashMap<>();

    @Value("${outbox.kafka.factory.idle-eviction-time-minutes:30}")
    private long idleEvictionTimeMinutes;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    /**
     * Gets a KafkaTemplate for the given cluster key (e.g., "cluster-a").
     * If the template is not in the cache, it will be created, registered,
     * and stored.
     *
     * @param clusterKey The identifier from application.properties
     * @return A thread-safe, Spring-managed KafkaTemplate bean.
     */
    public KafkaTemplate<String, String> getTemplate(String clusterKey) {
        // computeIfAbsent is atomic and ensures createAndRegisterTemplate
        // is called only once per key, eliminating the need for manual locking.
        KafkaTemplate<String, String> template = templateCache.computeIfAbsent(clusterKey, this::createAndRegisterTemplate);

        // Update last access time *after* successful retrieval/creation
        lastAccessTime.put(clusterKey, System.currentTimeMillis());
        return template;
    }

    /**
     * Creates and registers a new KafkaTemplate and its ProducerFactory
     * as Spring-managed beans.
     * This method is public so it can be proxied by Spring.
     *
     * @param clusterKey The key for the cluster
     * @return The newly created, fully managed KafkaTemplate bean
     */
    public KafkaTemplate<String, String> createAndRegisterTemplate(String clusterKey) {
        log.info("No KafkaTemplate in cache for '{}'. Creating and registering new Spring beans.", clusterKey);

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        
        // Step 1: Get the configuration for the producer
        Map<String, Object> producerProps = getProducerProperties(clusterKey);

        // Step 2: Register the ProducerFactory bean
        String factoryBeanName = registerProducerFactory(registry, clusterKey, producerProps);

        // Step 3: Register the KafkaTemplate bean that depends on the factory
        String templateBeanName = registerKafkaTemplate(registry, clusterKey, factoryBeanName);

        // Step 4: Get the fully managed bean from the context.
        // This bean *will* be proxied and respect all Spring AOP.
        return (KafkaTemplate<String, String>) applicationContext.getBean(templateBeanName);
    }

    /**
     * Finds the KafkaProperties for the cluster and builds the producer config map.
     */
    private Map<String, Object> getProducerProperties(String clusterKey) {
        KafkaProperties props = clustersConfig.getClusters().get(clusterKey);
        if (props == null) {
            // This is a fatal configuration error
            throw new IllegalArgumentException("No Kafka cluster configuration found for key: " + clusterKey);
        }
        return props.buildProducerProperties(null);
    }

    /**
     * Creates and registers a Spring-managed DefaultKafkaProducerFactory bean.
     * @return The bean name of the newly registered factory.
     */
    private String registerProducerFactory(BeanDefinitionRegistry registry, String clusterKey, Map<String, Object> producerProps) {
        String factoryBeanName = clusterKey + "-ProducerFactory";

        BeanDefinition factoryBeanDef = BeanDefinitionBuilder
                .rootBeanDefinition(DefaultKafkaProducerFactory.class)
                .addConstructorArgValue(producerProps)
                .getBeanDefinition();
                
        registry.registerBeanDefinition(factoryBeanName, factoryBeanDef);
        log.debug("Registered bean: {}", factoryBeanName);
        return factoryBeanName;
    }

    /**
     * Creates and registers a Spring-managed KafkaTemplate bean that depends on the factory.
     * @return The bean name of the newly registered template.
     */
    private String registerKafkaTemplate(BeanDefinitionRegistry registry, String clusterKey, String factoryBeanName) {
        String templateBeanName = clusterKey + "-KafkaTemplate";

        BeanDefinition templateBeanDef = BeanDefinitionBuilder
                .rootBeanDefinition(KafkaTemplate.class)
                .addConstructorArgReference(factoryBeanName) // Reference the factory bean
                .getBeanDefinition();
                
        registry.registerBeanDefinition(templateBeanName, templateBeanDef);
        log.debug("Registered bean: {}", templateBeanName);
        return templateBeanName;
    }

    /**
     * Scheduled task to find and evict idle KafkaTemplates.
     * Runs every 5 minutes.
     */
    @Scheduled(fixedRateString = "${outbox.kafka.factory.eviction-check-ms:300000}")
    public void evictIdleTemplates() {
        log.debug("Running idle KafkaTemplate eviction check...");
        long idleThreshold = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(idleEvictionTimeMinutes);

        // Use removeIf for atomic check-and-remove from the access map
        lastAccessTime.entrySet().removeIf(entry -> {
            if (entry.getValue() < idleThreshold) {
                String clusterKey = entry.getKey();
                log.info("Evicting idle KafkaTemplate for cluster: {}", clusterKey);

                // Atomically remove from the main cache.
                if (templateCache.remove(clusterKey) != null) {
                    destroyBeans(clusterKey);
                }
                return true; // Remove from lastAccessTime map
            }
            return false; // Keep in lastAccessTime map
        });
    }

    /**
     * Destroys the Spring-managed beans (KafkaTemplate and ProducerFactory)
     * for the given cluster key, releasing all resources.
     */
    private void destroyBeans(String clusterKey) {
        try {
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

            String templateBeanName = clusterKey + "-KafkaTemplate";
            String factoryBeanName = clusterKey + "-ProducerFactory";

            // Destroy the singleton instances (cast to access destroySingleton)
            if (beanFactory instanceof org.springframework.beans.factory.support.DefaultSingletonBeanRegistry singletonRegistry) {
                singletonRegistry.destroySingleton(templateBeanName);
                singletonRegistry.destroySingleton(factoryBeanName);
            }

            // Remove the bean definitions
            registry.removeBeanDefinition(templateBeanName);
            registry.removeBeanDefinition(factoryBeanName);

            log.info("Successfully destroyed and unregistered beans for cluster: {}", clusterKey);
        } catch (Exception e) {
            log.error("Error while destroying beans for cluster: {}", clusterKey, e);
        }
    }
}
