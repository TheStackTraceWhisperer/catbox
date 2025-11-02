package com.example.catbox.server.metrics;

import com.example.catbox.common.repository.OutboxEventRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service that tracks custom metrics for the outbox pattern.
 * Provides insights into outbox health and performance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxMetricsService {

    private final OutboxEventRepository outboxEventRepository;
    private final MeterRegistry meterRegistry;

    // Gauges - updated periodically
    private final AtomicLong pendingEventsCount = new AtomicLong(0);
    private final AtomicLong oldestEventAgeSeconds = new AtomicLong(0);

    // Counters for success/failure
    private Counter publishSuccessCounter;
    private Counter publishFailureCounter;

    // Timer for event processing duration
    private Timer eventProcessingTimer;

    /**
     * Initialize metrics on bean construction.
     */
    @PostConstruct
    public void initializeMetrics() {
        // Gauge: Number of pending events
        Gauge.builder("outbox.events.pending", pendingEventsCount, AtomicLong::get)
                .description("Number of pending events in the outbox that haven't been sent")
                .register(meterRegistry);

        // Gauge: Age of oldest unsent event in seconds
        Gauge.builder("outbox.events.oldest.age.seconds", oldestEventAgeSeconds, AtomicLong::get)
                .description("Age in seconds of the oldest unsent event in the outbox")
                .register(meterRegistry);

        // Counter: Successful publishes
        publishSuccessCounter = Counter.builder("outbox.events.published.success")
                .description("Total number of events successfully published to Kafka")
                .register(meterRegistry);

        // Counter: Failed publishes
        publishFailureCounter = Counter.builder("outbox.events.published.failure")
                .description("Total number of events that failed to publish to Kafka")
                .register(meterRegistry);

        // Timer: Event processing duration (from claim to publish)
        eventProcessingTimer = Timer.builder("outbox.events.processing.duration")
                .description("Duration of event processing from claim to publish")
                .register(meterRegistry);

        log.info("Outbox metrics initialized");
    }

    /**
     * Update pending events metrics on a schedule.
     * Runs every 10 seconds to keep metrics current.
     */
    @Scheduled(fixedDelay = 10000, initialDelay = 5000)
    public void updatePendingEventsMetrics() {
        try {
            // Count pending events (those without sentAt timestamp)
            long pendingCount = outboxEventRepository.countBySentAtIsNull();
            pendingEventsCount.set(pendingCount);

            // Find oldest unsent event and calculate age
            LocalDateTime now = LocalDateTime.now();
            outboxEventRepository.findFirstBySentAtIsNullOrderByCreatedAtAsc()
                    .ifPresentOrElse(
                            oldestEvent -> {
                                long ageSeconds = Duration.between(oldestEvent.getCreatedAt(), now).getSeconds();
                                oldestEventAgeSeconds.set(ageSeconds);
                            },
                            () -> oldestEventAgeSeconds.set(0)
                    );

            log.debug("Updated outbox metrics: pending={}, oldestAgeSeconds={}", 
                        pendingCount, oldestEventAgeSeconds.get());
        } catch (Exception e) {
            log.error("Error updating pending events metrics", e);
        }
    }

    /**
     * Record a successful event publish.
     */
    public void recordPublishSuccess() {
        publishSuccessCounter.increment();
    }

    /**
     * Record a failed event publish.
     */
    public void recordPublishFailure() {
        publishFailureCounter.increment();
    }

    /**
     * Record event processing time.
     * 
     * @param startTime When the event was claimed for processing
     */
    public void recordProcessingDuration(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        long durationMillis = Duration.between(startTime, endTime).toMillis();
        eventProcessingTimer.record(Duration.ofMillis(durationMillis));
    }
}
