package com.example.catbox.client;

import com.example.catbox.client.metrics.CatboxClientMetricsService;
import com.example.catbox.common.repository.ProcessedMessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** Auto-configuration for Catbox client components. */
@Configuration
@ComponentScan
public class CatboxClientAutoConfiguration {

  /**
   * Provides a default database-backed OutboxFilter bean if no other implementation is configured.
   * Applications can override this by providing their own OutboxFilter bean.
   *
   * <p>The database-backed implementation is production-ready and supports:
   *
   * <ul>
   *   <li>Persistence across restarts
   *   <li>Multi-instance deployments
   *   <li>Per-consumer-group tracking
   *   <li>Archival support
   * </ul>
   *
   * @param repository the processed message repository
   * @param metricsService the client metrics service
   * @return the default OutboxFilter implementation
   */
  @Bean
  @ConditionalOnMissingBean
  public OutboxFilter outboxFilter(
      final ProcessedMessageRepository repository,
      final CatboxClientMetricsService metricsService) {
    return new DatabaseOutboxFilter(repository, metricsService);
  }
}
