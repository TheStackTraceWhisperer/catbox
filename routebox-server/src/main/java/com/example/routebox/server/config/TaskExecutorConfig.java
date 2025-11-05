package com.example.routebox.server.config;

import com.example.routebox.common.entity.OutboxEvent;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
@RequiredArgsConstructor
public class TaskExecutorConfig {

  private final OutboxProcessingConfig processingConfig;

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

  /**
   * Provides a bounded blocking queue for outbox events. This queue enforces backpressure by
   * limiting the number of events buffered in memory, preventing resource exhaustion.
   */
  @Bean
  public BlockingQueue<OutboxEvent> outboxEventQueue() {
    return new ArrayBlockingQueue<>(processingConfig.getQueueCapacity());
  }
}
