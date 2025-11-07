package com.example.routebox.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.example.routebox.test.listener.SharedTestcontainers;

@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class CatboxApplicationTests {

  static {
    SharedTestcontainers.ensureInitialized();
  }

  @Test
  void contextLoads() {
    // This test verifies that the application context loads successfully
  }
}
