package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.order", "com.example.routebox.client"})
@EnableJpaRepositories(
    basePackages = {"com.example.order.repository", "com.example.routebox.common.repository"})
@EntityScan(basePackages = {"com.example.order.entity", "com.example.routebox.common.entity"})
public class OrderServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class, args);
  }
}
