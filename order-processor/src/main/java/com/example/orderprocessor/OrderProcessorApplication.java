package com.example.orderprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Order Processor Application - Consumes and processes order events from Kafka
 * with deduplication using the OutboxFilter.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.orderprocessor",
    "com.example.catbox.client"
})
@EnableJpaRepositories(basePackages = "com.example.catbox.common.repository")
@EntityScan(basePackages = "com.example.catbox.common.entity")
public class OrderProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderProcessorApplication.class, args);
    }
}
