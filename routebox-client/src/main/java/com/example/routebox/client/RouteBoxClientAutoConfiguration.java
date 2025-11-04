package com.example.routebox.client;

import com.example.routebox.common.repository.ProcessedMessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for RouteBox client components.
 */
@Configuration
@ComponentScan
public class RouteBoxClientAutoConfiguration {

    /**
     * Provides a default database-backed OutboxFilter bean if no other
     * implementation is configured. Applications can override this by
     * providing their own OutboxFilter bean.
     *
     * <p>The database-backed implementation is production-ready and
     * supports:
     * <ul>
     *   <li>Persistence across restarts</li>
     *   <li>Multi-instance deployments</li>
     *   <li>Per-consumer-group tracking</li>
     *   <li>Archival support</li>
     * </ul>
     *
     * @param repository the processed message repository
     * @return the default OutboxFilter implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public OutboxFilter outboxFilter(
            final ProcessedMessageRepository repository) {
        return new DatabaseOutboxFilter(repository);
    }
}
