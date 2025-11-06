package com.example.orderprocessor.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka consumer configuration for the order processor.
 *
 * <p>Configures:
 *
 * <ul>
 *   <li>Manual acknowledgment mode for fine-grained control
 *   <li>Consumer settings optimized for at-least-once delivery
 *   <li>Deserialization for string-based messages
 * </ul>
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
  private String bootstrapServers;

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processor");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    // Enable auto-commit is disabled to use manual acknowledgment
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    // Start from earliest if no offset exists
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    // Max poll records to prevent overwhelming the consumer
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    // Configure for reliability when publishing to DLT
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, 3);
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
      KafkaTemplate<String, String> kafkaTemplate) {
    return new DeadLetterPublishingRecoverer(
        kafkaTemplate,
        (record, ex) -> {
          // Derive DLT name from original topic (e.g., OrderCreated -> OrderCreated.DLT)
          String originalTopic = record.topic();
          String dltTopic = originalTopic + ".DLT";
          return new org.apache.kafka.common.TopicPartition(dltTopic, 0);
        });
  }

  @Bean
  public CommonErrorHandler defaultErrorHandler(
      DeadLetterPublishingRecoverer deadLetterPublishingRecoverer) {
    // Configure retry policy: 3 retries, 1 second apart
    DefaultErrorHandler errorHandler =
        new DefaultErrorHandler(deadLetterPublishingRecoverer, new FixedBackOff(1000L, 3L));

    // Classify permanent errors that should not be retried
    errorHandler.addNotRetryableExceptions(
        NullPointerException.class,
        IllegalArgumentException.class,
        com.fasterxml.jackson.core.JsonProcessingException.class);

    return errorHandler;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
      CommonErrorHandler defaultErrorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());

    // Enable manual acknowledgment
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

    // Set concurrency to leverage virtual threads
    factory.setConcurrency(3);

    // Set the error handler
    factory.setCommonErrorHandler(defaultErrorHandler);

    return factory;
  }
}
