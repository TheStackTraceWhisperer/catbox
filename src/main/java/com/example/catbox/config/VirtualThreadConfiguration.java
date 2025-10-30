package com.example.catbox.config;

import org.springframework.context.annotation.Configuration;

// Virtual threads require Java 21+
// This configuration is disabled for Java 17 compatibility
@Configuration
public class VirtualThreadConfiguration {
    // Virtual thread configuration commented out for Java 17
    // Uncomment when using Java 21+
    
    /*
    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }
    */
}
