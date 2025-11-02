package com.example.catbox.server.service;

import com.example.catbox.common.entity.OutboxDeadLetterEvent;
import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxDeadLetterEventRepository;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.catbox.server.CatboxServerApplication;
import com.example.catbox.server.config.OutboxProcessingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the OutboxFailureHandler service that manages permanent failures
 * and dead-letter events.
 */
@SpringBootTest(classes = CatboxServerApplication.class)
@Testcontainers
class OutboxFailureHandlerTest {

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
        // Set a low max retries for testing
        registry.add("outbox.processing.max-permanent-retries", () -> "3");
    }

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @Autowired
    OutboxDeadLetterEventRepository deadLetterRepository;

    @Autowired
    OutboxFailureHandler failureHandler;

    @Autowired
    OutboxProcessingConfig processingConfig;

    @BeforeEach
    void setup() {
        deadLetterRepository.deleteAll();
        outboxEventRepository.deleteAll();
    }

    @Test
    void recordPermanentFailure_incrementsFailureCount() {
        // Given
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        assertThat(event.getPermanentFailureCount()).isEqualTo(0);

        // When
        failureHandler.recordPermanentFailure(event.getId(), "Test error");

        // Then
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getPermanentFailureCount()).isEqualTo(1);
        assertThat(updated.getLastError()).isEqualTo("Test error");
        assertThat(updated.getInProgressUntil()).isNull(); // Claim cleared for retry
    }

    @Test
    void recordPermanentFailure_movesToDeadLetterAfterMaxRetries() {
        // Given
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        Long eventId = event.getId();
        
        // When - Record failures up to max retries
        int maxRetries = processingConfig.getMaxPermanentRetries();
        for (int i = 0; i < maxRetries; i++) {
            failureHandler.recordPermanentFailure(eventId, "Error attempt " + (i + 1));
            
            if (i < maxRetries - 1) {
                // Event should still exist before last failure
                assertThat(outboxEventRepository.findById(eventId)).isPresent();
            }
        }

        // Then - Event should be moved to dead letter queue
        Optional<OutboxEvent> deletedEvent = outboxEventRepository.findById(eventId);
        assertThat(deletedEvent).isEmpty();

        List<OutboxDeadLetterEvent> deadLetters = deadLetterRepository.findAll();
        assertThat(deadLetters).hasSize(1);
        
        OutboxDeadLetterEvent deadLetter = deadLetters.get(0);
        assertThat(deadLetter.getOriginalEventId()).isEqualTo(eventId);
        assertThat(deadLetter.getAggregateType()).isEqualTo("Order");
        assertThat(deadLetter.getAggregateId()).isEqualTo("A1");
        assertThat(deadLetter.getEventType()).isEqualTo("OrderCreated");
        assertThat(deadLetter.getPayload()).isEqualTo("{}");
        assertThat(deadLetter.getFinalError()).contains("Error attempt");
        assertThat(deadLetter.getFailedAt()).isNotNull();
    }

    @Test
    @Transactional
    void resetFailureCount_clearsFailureData() {
        // Given
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        event.setPermanentFailureCount(2);
        event.setLastError("Previous error");
        outboxEventRepository.save(event);

        // When
        failureHandler.resetFailureCount(event);
        outboxEventRepository.save(event);

        // Then
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
        assertThat(updated.getLastError()).isNull();
    }

    @Test
    @Transactional
    void resetFailureCount_doesNothingWhenCountIsZero() {
        // Given
        OutboxEvent event = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        assertThat(event.getPermanentFailureCount()).isEqualTo(0);

        // When
        failureHandler.resetFailureCount(event);
        outboxEventRepository.save(event);

        // Then
        OutboxEvent updated = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(updated.getPermanentFailureCount()).isEqualTo(0);
    }

    @Test
    void recordPermanentFailure_handlesMultipleEvents() {
        // Given
        OutboxEvent event1 = outboxEventRepository.save(
                new OutboxEvent("Order", "A1", "OrderCreated", "{}")
        );
        OutboxEvent event2 = outboxEventRepository.save(
                new OutboxEvent("Order", "A2", "OrderCreated", "{}")
        );

        // When
        failureHandler.recordPermanentFailure(event1.getId(), "Error 1");
        failureHandler.recordPermanentFailure(event2.getId(), "Error 2");
        failureHandler.recordPermanentFailure(event1.getId(), "Error 1 again");

        // Then
        OutboxEvent updated1 = outboxEventRepository.findById(event1.getId()).orElseThrow();
        assertThat(updated1.getPermanentFailureCount()).isEqualTo(2);
        assertThat(updated1.getLastError()).isEqualTo("Error 1 again");

        OutboxEvent updated2 = outboxEventRepository.findById(event2.getId()).orElseThrow();
        assertThat(updated2.getPermanentFailureCount()).isEqualTo(1);
        assertThat(updated2.getLastError()).isEqualTo("Error 2");
    }
}
