package com.example.catbox.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@SpringBootApplication(scanBasePackages = {"com.example.catbox.server"})
@EnableJpaRepositories(basePackages = {"com.example.catbox.common.repository"})
@EntityScan(basePackages = {"com.example.catbox.common.entity"})
@EnableScheduling
public class CatboxServerApplication implements SchedulingConfigurer {

    private final TaskScheduler taskScheduler;

    // Inject the virtual thread scheduler bean
    public CatboxServerApplication(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(CatboxServerApplication.class, args);
    }
    
    // This method tells @EnableScheduling to use our virtual thread scheduler
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler);
    }
}
