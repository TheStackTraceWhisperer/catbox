package com.example.catbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CatboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatboxApplication.class, args);
    }
}
