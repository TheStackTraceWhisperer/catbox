package com.example.routebox.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@SpringBootApplication(scanBasePackages = {"com.example.routebox.server"})
@EnableJpaRepositories(
    basePackages = {
      "com.example.routebox.common.repository", // For OutboxEventRepository
      "com.example.routebox.server.repository" // For local repositories
    })
@EntityScan(
    basePackages = {
      "com.example.routebox.common.entity", // For OutboxEvent
      "com.example.routebox.server.entity" // For local entities
    })
@EnableScheduling
public class RouteBoxServerApplication implements SchedulingConfigurer {

  private final TaskScheduler taskScheduler;

  // Inject the virtual thread scheduler bean
  public RouteBoxServerApplication(TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  public static void main(String[] args) {
    SpringApplication.run(RouteBoxServerApplication.class, args);
  }

  // This method tells @EnableScheduling to use our virtual thread scheduler
  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskScheduler);
  }
}
