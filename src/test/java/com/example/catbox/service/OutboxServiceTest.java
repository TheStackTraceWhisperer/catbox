package com.example.catbox.service;

import com.example.catbox.CatboxApplication;
import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CatboxApplication.class)
class OutboxServiceTest {

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
