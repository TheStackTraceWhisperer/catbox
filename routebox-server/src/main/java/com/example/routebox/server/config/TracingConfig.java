package com.example.routebox.server.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for distributed tracing support.
 * Enables observation aspects to automatically create spans for @Observed methods.
 */
@Configuration
public class TracingConfig {

    /**
     * Enable automatic span creation for methods annotated with @Observed.
     */
    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
