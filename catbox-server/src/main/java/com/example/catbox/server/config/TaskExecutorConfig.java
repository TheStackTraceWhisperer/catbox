package com.example.catbox.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class TaskExecutorConfig {

    /**
     * Provides a task executor that creates a new virtual thread
     * for every task. This will be used by the @Scheduled poller.
     */
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        // We can set a prefix for easier debugging
        executor.setThreadNamePrefix("scheduler-vthread-");
        return executor;
    }
}
