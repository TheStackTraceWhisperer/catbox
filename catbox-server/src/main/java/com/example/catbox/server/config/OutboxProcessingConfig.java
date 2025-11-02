package com.example.catbox.server.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "outbox.processing")
@Getter
@Setter
public class OutboxProcessingConfig {

    /**
     * How long an event stays claimed (inProgressUntil) before being eligible for retry.
     */
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration claimTimeout = Duration.ofMinutes(5);

    /**
     * Number of events to claim per poll.
     */
    private int batchSize = 100; // default

    /**
     * Fixed delay between polls.
     */
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration pollFixedDelay = Duration.ofSeconds(2);

    /**
     * Initial delay before first poll.
     */
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration pollInitialDelay = Duration.ofSeconds(10);

    /**
     * Max number of retries for a PERMANENT failure before moving to DLQ.
     */
    private int maxPermanentRetries = 5;

    /**
     * List of fully-qualified exception class names considered permanent,
     * non-retryable failures.
     */
    private List<String> permanentFailureExceptions = List.of(
            // Set sensible defaults that can be overridden/extended
            "java.lang.IllegalStateException",
            "org.apache.kafka.common.errors.InvalidTopicException",
            "org.apache.kafka.common.errors.RecordTooLargeException",
            "org.apache.kafka.common.errors.SerializationException",
            "org.apache.kafka.common.errors.AuthenticationException",
            "org.apache.kafka.common.errors.AuthorizationException"
    );

    // A Set for efficient O(1) lookups
    private final Set<String> permanentExceptionSet = new HashSet<>();

    @PostConstruct
    private void init() {
        // Load all configured exception names into the Set
        permanentExceptionSet.addAll(permanentFailureExceptions);
    }

    // Public getter for the Set - returns unmodifiable view for thread safety
    public Set<String> getPermanentExceptionSet() {
        return Set.copyOf(permanentExceptionSet);
    }
}
