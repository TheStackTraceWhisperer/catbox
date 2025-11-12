package com.example.routebox.server.swagger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class SwaggerUiIntegrationTest {
  @Container
  static final MSSQLServerContainer<?> mssql =
      new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
    registry.add("spring.datasource.username", mssql::getUsername);
    registry.add("spring.datasource.password", mssql::getPassword);
    registry.add(
        "spring.datasource.driver-class-name",
        () -> "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    registry.add(
        "spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.SQLServerDialect");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    registry.add("spring.threads.virtual.enabled", () -> "true");
  }
@Autowired private MockMvc mockMvc;

  @Test
  void swaggerUiPageShouldBeAccessible() throws Exception {
    // /swagger-ui.html redirects to /swagger-ui/index.html
    mockMvc
        .perform(get("/swagger-ui.html"))
        .andExpect(status().is3xxRedirection())
        .andExpect(
            result -> {
              String redirectUrl = result.getResponse().getRedirectedUrl();
              assertThat(redirectUrl).contains("/swagger-ui/index.html");
            });
  }

  @Test
  void swaggerUiIndexPageShouldBeAccessible() throws Exception {
    mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk()).andReturn();
  }

  @Test
  void apiDocsShouldBeAccessible() throws Exception {
    MvcResult result = mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andReturn();

    String content = result.getResponse().getContentAsString();
    assertThat(content).contains("RouteBox Server API");
    assertThat(content).contains("/api/outbox-events");
  }
}
