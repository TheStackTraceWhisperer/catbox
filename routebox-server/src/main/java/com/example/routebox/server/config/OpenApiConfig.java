package com.example.routebox.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration for OpenAPI documentation. */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI routeBoxServerOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("RouteBox Server API")
                .description(
                    "Outbox processor application for managing and publishing outbox events to Kafka")
                .version("1.0.0"));
  }
}
