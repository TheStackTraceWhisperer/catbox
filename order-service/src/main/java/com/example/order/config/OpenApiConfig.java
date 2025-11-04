package com.example.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration for OpenAPI documentation. */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI orderServiceOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Order Service API")
                .description("Business logic application for managing orders with outbox pattern")
                .version("1.0.0"));
  }
}
