package com.example.catbox.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class CatboxClientAutoConfiguration {
    
    /**
     * Provides a default in-memory OutboxFilter bean if no other implementation is configured.
     * Applications can override this by providing their own OutboxFilter bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public OutboxFilter outboxFilter() {
        return new InMemoryOutboxFilter();
    }
}
