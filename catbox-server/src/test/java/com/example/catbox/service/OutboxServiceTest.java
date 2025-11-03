package com.example.catbox.server.service;

import com.example.catbox.server.CatboxServerApplication;
import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CatboxServerApplication.class)
@Transactional
@Testcontainers
class OutboxServiceTest {

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
    }

    @Autowired
    OutboxEventRepository outboxEventRepository;

    @Autowired
    OutboxService outboxService;

    @BeforeEach
    void setup() {
        outboxEventRepository.deleteAll();
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
        outboxEventRepository.save(new OutboxEvent("Order", "A2", "OrderStatusChanged", "{}"));
        outboxEventRepository.save(new OutboxEvent("Inventory", "I1", "InventoryAdjusted", "{}"));
    }

    @Test
    void findPaged_filtersAndSorts() {
        Page<OutboxService.OutboxEventSummaryDto> page = outboxService.findPaged(
                0, 2, "OrderCreated", "Order", null, null, "createdAt", Sort.Direction.ASC
        );
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getEventType()).isEqualTo("OrderCreated");
    }

    @Test
    void getPendingEvents_returnsAllUnsent() {
        List<OutboxEvent> pending = outboxService.getPendingEvents();
        assertThat(pending).hasSize(3);
    }

    @Test
    void markUnsent_clearsSentAtAndLease() {
        OutboxEvent e = outboxEventRepository.save(new OutboxEvent("Order", "A3", "OrderCreated", "{}"));
        e.setSentAt(java.time.LocalDateTime.now());
        e.setInProgressUntil(java.time.LocalDateTime.now());
        outboxEventRepository.save(e);

        outboxService.markUnsent(e.getId());
        OutboxEvent reloaded = outboxEventRepository.findById(e.getId()).orElseThrow();
        assertThat(reloaded.getSentAt()).isNull();
        assertThat(reloaded.getInProgressUntil()).isNull();
    }

    @Test
    void getAllEvents_returnsAllEvents() {
        List<OutboxEvent> events = outboxService.getAllEvents();
        assertThat(events).hasSize(3);
    }

    @Test
    void markUnsent_throwsExceptionWhenNotFound() {
        // When & Then
        try {
            outboxService.markUnsent(99999L);
            org.junit.jupiter.api.Assertions.fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("OutboxEvent not found");
        }
    }

    @Test
    void findPaged_filtersWithBlankValues() {
        // Test that blank string parameters are ignored
        Page<OutboxService.OutboxEventSummaryDto> page = outboxService.findPaged(
                0, 10, "  ", "", null, null, "createdAt", Sort.Direction.ASC
        );
        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findPaged_filtersWithAggregateIdAndPending() {
        outboxEventRepository.save(new OutboxEvent("Order", "A1", "OrderCreated", "{}"));
        OutboxEvent sent = new OutboxEvent("Order", "A1", "OrderStatusChanged", "{}");
        sent.setSentAt(java.time.LocalDateTime.now());
        outboxEventRepository.save(sent);

        Page<OutboxService.OutboxEventSummaryDto> page = outboxService.findPaged(
                0, 10, null, null, "A1", true, "createdAt", Sort.Direction.DESC
        );
        // Should only return pending events for A1
        assertThat(page.getTotalElements()).isEqualTo(2); // A1 from setup + new A1
    }

    @Test
    void findPaged_withAllNullParameters() {
        Page<OutboxService.OutboxEventSummaryDto> page = outboxService.findPaged(
                null, null, null, null, null, null, null, null
        );
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0); // Default page
        assertThat(page.getSize()).isEqualTo(20); // Default size
    }
}
