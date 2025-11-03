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
}
