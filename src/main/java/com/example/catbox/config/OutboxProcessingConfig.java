package com.example.catbox.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
}
