package com.example.catbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.catbox", "com.example.order"})
@EnableJpaRepositories(basePackages = {"com.example.catbox.repository", "com.example.order.repository"})
@EntityScan(basePackages = {"com.example.catbox.entity", "com.example.order.entity"})
@EnableScheduling
public class CatboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatboxApplication.class, args);
    }
}
