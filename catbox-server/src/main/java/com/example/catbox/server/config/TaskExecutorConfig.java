package com.example.catbox.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
public class TaskExecutorConfig {

  /**
   * Provides a task scheduler that creates a new virtual thread for every task. This will be used
   * by the @Scheduled poller.
   */
  @Bean
  public TaskScheduler taskScheduler() {
    SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();
    scheduler.setVirtualThreads(true);
    // We can set a prefix for easier debugging
    scheduler.setThreadNamePrefix("scheduler-vthread-");
    return scheduler;
  }
}
