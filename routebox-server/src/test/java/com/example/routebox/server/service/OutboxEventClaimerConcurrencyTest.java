package com.example.routebox.server.service;

import com.example.catbox.common.entity.OutboxEvent;
import com.example.catbox.common.repository.OutboxEventRepository;
import com.example.catbox.server.RouteBoxServerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the concurrency safety of the OutboxEventClaimer.
 * Verifies that multiple threads/nodes claiming events simultaneously
 * do not process the same event twice (using SKIP LOCKED/READPAST).
 */
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class OutboxEventClaimerConcurrencyTest {

    @Container
    static MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
        registry.add("spring.datasource.username", mssql::getUsername);
        registry.add("spring.datasource.password", mssql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private OutboxEventClaimer claimer;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @BeforeEach
    void setUp() {
        // Clean database
        outboxEventRepository.deleteAll();
    }

    /**
     * Gap 3: Tests that concurrent claimers do not process the same events.
     * Creates 100 events and has two threads claim them simultaneously.
     * Verifies no overlap between the claimed event sets.
     */
    @Test
    void testConcurrentClaimersDoNotProcessSameEvents() throws Exception {
        // Arrange: Create 100 pending events
        List<OutboxEvent> events = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            OutboxEvent event = new OutboxEvent(
                    "Order",
                    "order-" + i,
                    "OrderCreated",
                    "{\"orderId\":" + i + "}"
            );
            events.add(outboxEventRepository.save(event));
        }

        // Act: Use two threads to claim events simultaneously
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        CompletableFuture<List<OutboxEvent>> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                startLatch.await(); // Wait for signal to start
                List<OutboxEvent> claimed = claimer.claimEvents();
                doneLatch.countDown();
                return claimed;
            } catch (Exception e) {
                doneLatch.countDown();
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<List<OutboxEvent>> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                startLatch.await(); // Wait for signal to start
                List<OutboxEvent> claimed = claimer.claimEvents();
                doneLatch.countDown();
                return claimed;
            } catch (Exception e) {
                doneLatch.countDown();
                throw new RuntimeException(e);
            }
        });

        // Start both threads simultaneously
        startLatch.countDown();
        
        // Wait for both to complete
        doneLatch.await();

        List<OutboxEvent> claimedByThread1 = future1.get();
        List<OutboxEvent> claimedByThread2 = future2.get();

        // Assert: Verify total count is 100
        int totalClaimed = claimedByThread1.size() + claimedByThread2.size();
        assertThat(totalClaimed).isEqualTo(100);

        // Assert: Verify no intersection (no event claimed by both threads)
        Set<Long> idsFromThread1 = new HashSet<>();
        for (OutboxEvent event : claimedByThread1) {
            idsFromThread1.add(event.getId());
        }

        Set<Long> idsFromThread2 = new HashSet<>();
        for (OutboxEvent event : claimedByThread2) {
            idsFromThread2.add(event.getId());
        }

        // Find intersection
        Set<Long> intersection = new HashSet<>(idsFromThread1);
        intersection.retainAll(idsFromThread2);

        assertThat(intersection).isEmpty();
        assertThat(idsFromThread1.size()).isEqualTo(claimedByThread1.size());
        assertThat(idsFromThread2.size()).isEqualTo(claimedByThread2.size());
    }
}
