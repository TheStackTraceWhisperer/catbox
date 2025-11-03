package com.example.catbox.client;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for Catbox client components.
 */
@Configuration
@ComponentScan
public class CatboxClientAutoConfiguration {
    // Components are automatically discovered via @ComponentScan
    // - DefaultOutboxClient (OutboxClient implementation)
    // - DatabaseOutboxFilter (OutboxFilter implementation)
    // - CatboxClientMetricsService (Metrics service)
}
