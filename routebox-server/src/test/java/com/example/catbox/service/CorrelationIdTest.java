package com.example.catbox.server.service;

import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.catbox.server.CatboxServerApplication;
import com.example.catbox.server.config.DynamicKafkaTemplateFactory;
import com.example.catbox.server.config.OutboxRoutingConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Tests for correlation ID functionality in OutboxEventPublisher.
 */
@SpringBootTest(classes = CatboxServerApplication.class)
@Testcontainers
class CorrelationIdTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withReuse(true);

    @DynamicPropertySource
    static void sqlProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
        registry.add("spring.datasource.username", mssql::getUsername);
        registry.add("spring.datasource.password", mssql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("outbox.routing.rules.OrderCreated", () -> "cluster-a");
    }

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @Autowired
    OutboxEventPublisher publisher;

    @MockitoBean
    DynamicKafkaTemplateFactory kafkaTemplateFactory;

    @BeforeEach
    void setup() {
        outboxEventRepository.deleteAll();
    }

    /**
     * Helper method to create a mock SendResult with RecordMetadata
     */
    private SendResult<String, String> createMockSendResult(String topic, int partition, long offset) {
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        RecordMetadata recordMetadata = new RecordMetadata(
            topicPartition,
            offset,
            0, // batch index
            System.currentTimeMillis(),
            0, // serialized key size
            0  // serialized value size
        );
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, "key", "value");
        return new SendResult<>(producerRecord, recordMetadata);
    }

    @Test
    void publishEvent_withCorrelationId_sendsCorrelationIdInKafkaHeader() throws Exception {
        // Given
        String correlationId = "test-correlation-id-12345";
        OutboxEvent event = new OutboxEvent("Order", "A1", "OrderCreated", correlationId, "{}");
        event = outboxEventRepository.save(event);
        
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
        SendResult<String, String> mockSendResult = createMockSendResult("OrderCreated", 0, 12345L);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(mockSendResult);
        
        Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
        
        // Capture the ProducerRecord
        @SuppressWarnings("unchecked")
        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        Mockito.when(mockTemplate.send(recordCaptor.capture())).thenReturn(future);

        // When
        publisher.publishEvent(event);

        // Then
        ProducerRecord<String, String> capturedRecord = recordCaptor.getValue();
        assertThat(capturedRecord).isNotNull();
        assertThat(capturedRecord.topic()).isEqualTo("OrderCreated");
        assertThat(capturedRecord.key()).isEqualTo("A1");
        assertThat(capturedRecord.value()).isEqualTo("{}");
        
        // Verify correlation ID is in headers
        byte[] headerValue = capturedRecord.headers().lastHeader("correlationId").value();
        assertThat(headerValue).isNotNull();
        assertThat(new String(headerValue, StandardCharsets.UTF_8)).isEqualTo(correlationId);
        
        // Verify event was marked as sent
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getSentAt()).isNotNull();
    }

    @Test
    void publishEvent_withoutCorrelationId_sendsWithoutHeader() throws Exception {
        // Given - Event without correlation ID
        OutboxEvent event = new OutboxEvent("Order", "A2", "OrderCreated", "{}");
        event = outboxEventRepository.save(event);
        
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
        SendResult<String, String> mockSendResult = createMockSendResult("OrderCreated", 0, 12345L);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(mockSendResult);
        
        Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
        
        @SuppressWarnings("unchecked")
        ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
        Mockito.when(mockTemplate.send(recordCaptor.capture())).thenReturn(future);

        // When
        publisher.publishEvent(event);

        // Then
        ProducerRecord<String, String> capturedRecord = recordCaptor.getValue();
        assertThat(capturedRecord).isNotNull();
        
        // Verify no correlation ID header
        assertThat(capturedRecord.headers().lastHeader("correlationId")).isNull();
        
        // Verify event was marked as sent
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getSentAt()).isNotNull();
    }

    @Test
    void createEvent_withCorrelationId_storesInDatabase() {
        // Given
        String correlationId = "unique-corr-id-999";
        OutboxEvent event = new OutboxEvent("Order", "A3", "OrderCreated", correlationId, "{}");
        
        // When
        OutboxEvent saved = outboxEventRepository.save(event);
        
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCorrelationId()).isEqualTo(correlationId);
        
        // Verify it can be retrieved
        OutboxEvent retrieved = outboxEventRepository.findById(saved.getId()).orElseThrow();
        assertThat(retrieved.getCorrelationId()).isEqualTo(correlationId);
    }
}
