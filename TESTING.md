# Testing Guide

This document describes the testing infrastructure added to the catbox project.

## Test Overview

The project now includes comprehensive testing at multiple levels:

1. **Unit Tests**: Verify individual components and configuration
2. **Integration Tests**: Test interaction with external systems (Kafka)
3. **Context Tests**: Ensure Spring application context loads correctly

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Classes
```bash
# Kafka tests only
mvn test -Dtest=KafkaTemplateConfigTest,KafkaIntegrationTest

# Service tests only
mvn test -Dtest=OrderServiceTest
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

## Test Categories

### 1. Kafka Configuration Tests (`KafkaTemplateConfigTest`)

**Purpose**: Verify Kafka beans are properly configured in the Spring context.

**What it tests**:
- KafkaTemplate bean creation
- Producer factory configuration
- Idempotent producer settings

**Type**: Integration test (uses @SpringBootTest)

**Run time**: ~2 seconds

### 2. Kafka Integration Tests (`KafkaIntegrationTest`)

**Purpose**: Test actual Kafka message publishing and consumption.

**What it tests**:
- Sending messages to Kafka topics
- Receiving messages from Kafka topics
- Message serialization/deserialization

**Type**: Integration test with EmbeddedKafka

**Technology**: Uses Spring Kafka Test's EmbeddedKafka broker

**Run time**: ~3-4 seconds

**Note**: Does not require external Kafka - starts an embedded broker

### 3. Service Tests (`OrderServiceTest`)

**Purpose**: Test business logic for order management and outbox pattern.

**What it tests**:
- Order creation with outbox event generation
- Order status updates with event publishing
- Transactional consistency between orders and events

**Type**: Integration test with transactional rollback

**Run time**: ~1 second

### 4. Application Context Test (`CatboxApplicationTests`)

**Purpose**: Verify Spring application context loads successfully.

**What it tests**:
- All beans can be created
- No configuration conflicts
- Application starts without errors

**Type**: Smoke test

**Run time**: <1 second

## Docker Compose Testing

### Manual Testing with Docker Compose

1. Start services:
```bash
docker compose up -d
```

2. Wait for services to be healthy:
```bash
docker compose ps
```

3. Run application with Azure SQL profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=azuresql
```

4. Test Kafka connectivity:
```bash
# Create a test topic
docker exec catbox-kafka kafka-topics.sh --create \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# List topics
docker exec catbox-kafka kafka-topics.sh --list \
  --bootstrap-server localhost:9092
```

5. Test Azure SQL connectivity:
```bash
# Using sqlcmd inside the container
docker exec -it catbox-azuresql /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "YourStrong@Passw0rd" \
  -Q "SELECT @@VERSION" -C -No
```

6. Stop services:
```bash
docker compose down
```

## Test Utilities and Helpers

### EmbeddedKafka Configuration

The `KafkaIntegrationTest` uses Spring Kafka Test's `@EmbeddedKafka` annotation:

```java
@EmbeddedKafka(partitions = 1, topics = {"test-outbox-events"})
```

This automatically:
- Starts an embedded Kafka broker
- Creates the specified topics
- Configures the bootstrap servers for the test
- Cleans up after the test completes

### Test Properties

Tests use `@TestPropertySource` to override application properties:

```java
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
```

This ensures tests use the embedded Kafka broker instead of localhost:9092.

### Transactional Tests

Service tests use `@Transactional` to ensure database state is rolled back:

```java
@SpringBootTest
@Transactional
class OrderServiceTest {
    // Tests here will rollback after each test method
}
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Run tests
        run: mvn clean test
        
      - name: Verify Docker Compose
        run: docker compose config --quiet
```

## Troubleshooting

### Kafka Tests Fail

**Issue**: Kafka integration tests timeout or fail to start

**Solutions**:
1. Check available ports (default: random port assigned by Spring)
2. Increase test timeout in pom.xml
3. Check Docker is not already using Kafka ports

### Azure SQL Connection Fails

**Issue**: Cannot connect to Azure SQL when using docker-compose

**Solutions**:
1. Verify container is running: `docker compose ps`
2. Check logs: `docker compose logs azuresql`
3. Wait for health check: The container needs time to initialize
4. Verify password in connection string matches compose.yaml

### Out of Memory During Tests

**Issue**: Tests fail with OutOfMemoryError

**Solutions**:
1. Increase Maven memory: `export MAVEN_OPTS="-Xmx1024m"`
2. Run tests sequentially: `mvn test -DforkCount=1`
3. Clean between test runs: `mvn clean test`

## Best Practices

1. **Keep tests focused**: Each test should verify one specific behavior
2. **Use meaningful test names**: Describe what the test verifies
3. **Clean up resources**: Ensure embedded services are properly shut down
4. **Avoid external dependencies**: Use embedded/mocked services when possible
5. **Keep tests fast**: Integration tests should complete in seconds, not minutes

## Future Enhancements

Potential improvements to the test infrastructure:

1. Add Testcontainers for Azure SQL testing
2. Add consumer tests for Kafka message handling
3. Add performance/load tests for outbox polling
4. Add mutation testing for critical business logic
5. Add contract testing for API endpoints
