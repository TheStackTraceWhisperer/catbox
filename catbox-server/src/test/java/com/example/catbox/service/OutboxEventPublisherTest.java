package com.example.catbox.server.service;

import com.example.catbox.common.entity.OutboxDeadLetterEvent;
import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxDeadLetterEventRepository;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.catbox.server.CatboxServerApplication;
import com.example.catbox.server.config.DynamicKafkaTemplateFactory;
import com.example.catbox.server.config.OutboxProcessingConfig;
import com.example.catbox.server.config.OutboxRoutingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Tests for OutboxEventPublisher focusing on permanent vs transient failure classification.
 */
@SpringBootTest(classes = CatboxServerApplication.class)
@Testcontainers
class OutboxEventPublisherTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense();

    @DynamicPropertySource
    static void sqlProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
        registry.add("spring.datasource.username", mssql::getUsername);
        registry.add("spring.datasource.password", mssql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Configure permanent failure exceptions
        registry.add("outbox.processing.max-permanent-retries", () -> "3");
        registry.add("outbox.processing.permanent-failure-exceptions[0]", () -> "java.lang.IllegalStateException");
        registry.add("outbox.processing.permanent-failure-exceptions[1]", () -> "org.apache.kafka.common.errors.InvalidTopicException");
        // Add routing configuration
        registry.add("outbox.routing.rules.OrderCreated", () -> "cluster-a");
    }

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @Autowired
    OutboxDeadLetterEventRepository deadLetterRepository;

    @Autowired
    OutboxEventPublisher publisher;

    @Autowired
    OutboxProcessingConfig processingConfig;

    @Autowired
    OutboxRoutingConfig routingConfig;

    @MockBean
    DynamicKafkaTemplateFactory kafkaTemplateFactory;

    @BeforeEach
    void setup() {
        deadLetterRepository.deleteAll();
        outboxEventRepository.deleteAll();
    }

    @Test
    void publishEvent_successfullySendsAndMarksSent() throws Exception {
        // Given
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        
        Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
        Mockito.when(mockTemplate.send(eq("OrderCreated"), eq("A1"), eq("{}"))).thenReturn(future);

        // When
        publisher.publishEvent(event);

        // Then
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getSentAt()).isNotNull();
        assertThat(updated.getInProgressUntil()).isNull();
        assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
        assertThat(updated.getLastError()).isNull();
    }

    @Test
    void publishEvent_handlesPermanentFailure_noRoute() throws Exception {
        // Given - Event with no configured route
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "UnknownEventType", "{}")
        );

        // When - The routing lookup will fail with IllegalStateException
        publisher.publishEvent(event);

        // Then - Should record permanent failure
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getSentAt()).isNull();
        assertThat(updated.getPermanentFailureCount()).isEqualTo(1);
        assertThat(updated.getLastError()).contains("No Kafka route found");
        assertThat(updated.getInProgressUntil()).isNull(); // Claim cleared for retry
    }

    @Test
    void publishEvent_handlesPermanentFailure_kafkaException() throws Exception {
        // Given
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalStateException("Some permanent Kafka error"));
        
        Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
        Mockito.when(mockTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        // When
        publisher.publishEvent(event);

        // Then - Should record permanent failure
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getSentAt()).isNull();
        assertThat(updated.getPermanentFailureCount()).isEqualTo(1);
        assertThat(updated.getLastError()).isNotNull();
        assertThat(updated.getInProgressUntil()).isNull(); // Claim cleared for retry
    }

    @Test
    void publishEvent_movesToDeadLetterAfterMaxPermanentRetries() throws Exception {
        // Given
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "UnknownEventType", "{}")
        );
        Long eventId = event.getId();

        // When - Publish multiple times to exceed max retries (no route = permanent failure)
        int maxRetries = processingConfig.getMaxPermanentRetries();
        for (int i = 0; i < maxRetries; i++) {
            OutboxEvent current = outboxEventRepository.findById(eventId).orElse(null);
            if (current != null) {
                publisher.publishEvent(current);
            }
        }

        // Then - Should be in dead letter queue
        assertThat(outboxEventRepository.findById(eventId)).isEmpty();
        
        List<OutboxDeadLetterEvent> deadLetters = deadLetterRepository.findAll();
        assertThat(deadLetters).hasSize(1);
        assertThat(deadLetters.get(0).getOriginalEventId()).isEqualTo(eventId);
    }

    @Test
    void publishEvent_resetsFailureCountOnSuccess() throws Exception {
        // Given - Event with previous failures
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        event.setPermanentFailureCount(2);
        event.setLastError("Previous error");
        event = outboxEventRepository.save(event);
        
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> mockTemplate = Mockito.mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        
        Mockito.when(kafkaTemplateFactory.getTemplate(eq("cluster-a"))).thenReturn(mockTemplate);
        Mockito.when(mockTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        // When
        publisher.publishEvent(event);

        // Then
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getSentAt()).isNotNull();
        assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
        assertThat(updated.getLastError()).isNull();
    }
}
