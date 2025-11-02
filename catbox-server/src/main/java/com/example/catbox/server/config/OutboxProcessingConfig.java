package com.example.catbox.server.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "outbox.processing")
@Getter
@Setter
public class OutboxProcessingConfig {

    /**
     * How long (in milliseconds) an event stays claimed (inProgressUntil) before being eligible for retry.
     */
    private long claimTimeoutMs = 5 * 60 * 1000L; // default 5 minutes

    /**
     * Number of events to claim per poll.
     */
    private int batchSize = 100; // default

    /**
     * Fixed delay between polls in milliseconds.
     */
    private long pollFixedDelayMs = 2000; // default 2s

    /**
     * Initial delay before first poll in milliseconds.
     */
    private long pollInitialDelayMs = 10000; // default 10s

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

    /**
     * Number of days to retain sent events before archiving them.
     * Set to 0 or negative to disable archival.
     */
    private int archivalRetentionDays = 7; // default 7 days

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
