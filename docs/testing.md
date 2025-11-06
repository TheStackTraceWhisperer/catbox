# Testing Guide

Testing documentation for the RouteBox transactional outbox pattern implementation.

## Overview

The RouteBox project includes a multi-layered testing strategy:

1. **Unit Tests** - Fast, isolated component tests
2. **Integration Tests** - Tests with real dependencies (database, Kafka)
3. **End-to-End Tests** - Complete workflow validation from order creation to Kafka delivery
4. **Architecture Tests** - Automated enforcement of design rules using ArchUnit
5. **Performance Tests** - JMeter load and stress testing

## Prerequisites

- **Java 21** - Required for building and running tests (enforced by Maven)
- **Docker** - Required for Testcontainers integration tests
- **Maven 3.6+** - Build tool

## Running Tests

### Quick Start

```bash
# Run all unit tests
mvn clean test

# Run all tests including integration tests
mvn clean verify

# Run tests for a specific module
mvn test -pl routebox-server
mvn test -pl order-service
```

### Running Specific Test Classes

```bash
# Run specific test class
mvn test -Dtest=OrderServiceTest

# Run multiple test classes
mvn test -Dtest=OrderServiceTest,OutboxServiceTest

# Run tests matching a pattern
mvn test -Dtest=*Integration*
```

### Running with Coverage

```bash
# Generate coverage reports
mvn clean verify

# View aggregated coverage report
open coverage-report/target/site/jacoco-aggregate/index.html

# View module-specific coverage
open routebox-server/target/jacoco-ut/index.html
open routebox-server/target/jacoco-it/index.html
```

## Test Categories

### 1. Unit Tests

**Purpose**: Verify individual components in isolation.

**Examples**:
- `OutboxMetricsServiceTest` - Tests metrics collection logic
- `OutboxEventClaimTest` - Tests event claiming logic
- `OutboxFailureHandlerTest` - Tests failure handling

**Characteristics**:
- Fast execution (milliseconds)
- No external dependencies
- Use mocks/stubs for dependencies
- Located in `src/test/java` with Test suffix

### 2. Integration Tests with Testcontainers

**Purpose**: Test interactions with real databases and Kafka.

**Technology**: Uses Testcontainers to spin up actual Docker containers for:
- Azure SQL Server (mcr.microsoft.com/mssql/server:2022-latest)
- Kafka (confluentinc/cp-kafka:7.9.1)

**Examples**:

#### Database Integration Tests
```java
@SpringBootTest
@Testcontainers
class OrderServiceTest {
    @Container
    static MSSQLServerContainer<?> mssql = 
        new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense();
    
    // Tests create orders and verify outbox events in real database
}
```

**What it tests**:
- Transactional consistency between orders and outbox events
- Database constraints and relationships
- JPA entity mappings
- SQL queries and repository methods

#### Kafka Integration Tests
```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"test-outbox-events"})
class KafkaIntegrationTest {
    // Tests message publishing to Kafka
}
```

**What it tests**:
- Kafka message serialization/deserialization
- Dynamic KafkaTemplate factory
- Multi-cluster routing configuration
- Message delivery and consumption

**Note**: Integration tests using Testcontainers require Docker to be running.

### 3. End-to-End Tests

**Purpose**: Validate complete workflows from order creation through Kafka publication.

**Example**: `E2EPollerTest`, `E2EPollerMultiClusterTest`

```java
@SpringBootTest(classes = RouteBoxServerApplication.class)
@Testcontainers
class E2EPollerTest {
    @Container
    static MSSQLServerContainer<?> mssql = ...
    
    @Container
    static KafkaContainer kafka = ...
    
    // Tests complete flow:
    // 1. Create outbox event in database
    // 2. Poller claims event (SELECT FOR UPDATE SKIP LOCKED)
    // 3. Event is published to Kafka
    // 4. Event status is updated in database
}
```

**What it tests**:
- Complete outbox pattern implementation
- Event claiming with pessimistic locking
- Virtual thread-based concurrent processing
- At-least-once delivery guarantees
- Multi-cluster routing strategies

**Run time**: ~10-15 seconds (includes container startup)

### 4. Architecture Tests (ArchUnit)

**Purpose**: Enforce architectural rules and design patterns automatically.

**Location**: `routebox-archunit` module

**Test Classes**:
- `LayeringArchitectureTest` - Enforces layered architecture (controller → service → repository)
- `PackageDependencyTest` - Prevents circular dependencies
- `NamingConventionTest` - Enforces consistent naming patterns
- `SpringAnnotationTest` - Validates Spring annotation usage
- `TransactionBoundaryTest` - Ensures proper @Transactional usage
- `EntityRepositoryPatternTest` - Validates entity-repository patterns

**Example**:
```java
@Test
void servicesShouldNotDependOnControllers() {
    noClasses()
        .that().resideInAPackage("..service..")
        .should().dependOnClassesThat()
        .resideInAPackage("..controller..")
        .check(importedClasses);
}
```

**Benefits**:
- Prevents architectural drift
- Catches design violations in CI
- Documents architecture through executable tests
- Enforces best practices consistently

**Running**:
```bash
mvn test -pl routebox-archunit
```

### 5. Performance and Load Tests

**Purpose**: Validate system performance under load.

**Technology**: Apache JMeter 5.6.3 (via Docker)

**Location**: `jmeter-tests/` directory

**Test Plans**:
1. `OrderService_LoadTest.jmx` - Order creation, read, and update operations
2. `OutboxService_LoadTest.jmx` - Outbox event processing throughput
3. `EndToEnd_StressTest.jmx` - Complete system stress test

**Running**:
```bash
cd jmeter-tests

# Run specific test
./scripts/run-test.sh order      # Order service load test
./scripts/run-test.sh outbox     # Outbox processing test
./scripts/run-test.sh stress     # End-to-end stress test

# View results
ls -lh results/
```

**Test Configuration**:
- 50-500 concurrent users (configurable)
- 5-30 minute test duration
- Ramp-up periods for realistic load simulation
- CSV data sets for varied test data

See [jmeter-tests/README.md](../jmeter-tests/README.md) for detailed documentation.

## Code Coverage

### Coverage Configuration

The project uses **JaCoCo Maven Plugin** with separate reports for unit and integration tests:

- **Unit Test Coverage**: `target/jacoco-ut/`
  - Includes all tests except `*IT.java` and `*IntegrationTest.java`
  - Generated during `mvn test` phase

- **Integration Test Coverage**: `target/jacoco-it/`
  - Includes tests matching `*IT.java` or `*IntegrationTest.java`
  - Generated during `mvn verify` phase

- **Aggregated Coverage**: `coverage-report/target/site/jacoco-aggregate/`
  - Combines coverage from all modules
  - Provides project-wide coverage metrics

### Viewing Coverage Reports

```bash
# Generate all coverage reports
mvn clean verify

# Open aggregated report (all modules)
open coverage-report/target/site/jacoco-aggregate/index.html

# Open module-specific reports
open routebox-server/target/jacoco-ut/index.html       # Unit test coverage
open routebox-server/target/jacoco-it/index.html       # Integration test coverage
open order-service/target/jacoco-ut/index.html
```

Coverage reports are available in HTML, XML, and CSV formats.

## Test Technologies and Frameworks

### Core Frameworks
- **JUnit 5** - Test framework
- **AssertJ** - Fluent assertions
- **Mockito** - Mocking framework
- **Spring Boot Test** - Spring testing support

### Integration Testing
- **Testcontainers** - Docker containers for integration tests
  - Azure SQL Server containers
  - Kafka containers
- **Spring Kafka Test** - Embedded Kafka for lighter tests
- **Awaitility** - Asynchronous testing support

### Architecture Testing
- **ArchUnit** - Architecture testing framework

### Performance Testing
- **Apache JMeter** - Load and performance testing

## Test Utilities and Patterns

### Testcontainers Setup

Example from `OrderServiceTest`:

```java
@Container
static MSSQLServerContainer<?> mssql = 
    new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
        .acceptLicense();

@DynamicPropertySource
static void sqlProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", 
        () -> mssql.getJdbcUrl() + ";encrypt=true;trustServerCertificate=true");
    registry.add("spring.datasource.username", mssql::getUsername);
    registry.add("spring.datasource.password", mssql::getPassword);
}
```

**Benefits**:
- Real database testing without manual setup
- Isolated test environments
- Automatic cleanup
- Consistent across all environments

### EmbeddedKafka Configuration

For lighter Kafka testing without full containers:

```java
@EmbeddedKafka(partitions = 1, topics = {"test-outbox-events"})
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class KafkaIntegrationTest {
    // Tests use embedded Kafka automatically
}
```

### Transactional Tests

Service tests use `@Transactional` for automatic rollback:

```java
@SpringBootTest
@Transactional
class OrderServiceTest {
    // Database state is rolled back after each test
    // Ensures test isolation
}
```

### Testing Virtual Threads

Tests verify virtual thread usage for concurrent event processing:

```java
@Test
void shouldProcessEventsInVirtualThreads() {
    // Verify events are processed concurrently
    // Check thread names contain "virtual"
}
```

## Docker Compose Testing

### Manual Integration Testing

For testing the full infrastructure stack:

```bash
# 1. Start infrastructure
cd infrastructure && docker compose up -d

# 2. Wait for services to be healthy
docker compose ps

# 3. Start applications
# Terminal 1 - Order Service
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql

# Terminal 2 - RouteBox Server
mvn spring-boot:run -pl routebox-server -Dspring-boot.run.profiles=azuresql

# 4. Test manually
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName": "Alice", "productName": "Widget", "amount": 99.99}'

# 5. Verify in Kafka
docker exec catbox-kafka kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic outbox-events \
  --from-beginning

# 6. Stop services
cd infrastructure && docker compose down
```

## Continuous Integration

### GitHub Actions

The project includes CI workflows that run tests automatically with Docker layer caching enabled for faster builds:

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          
      - name: Run tests with coverage
        run: mvn clean verify
        
      - name: Upload coverage reports
        uses: codecov/codecov-action@v3
```

#### Docker Layer Caching

To improve build times, the CI workflow implements Docker layer caching for Testcontainers images:

**Cached Images**:
- `mcr.microsoft.com/mssql/server:2022-latest` - MS SQL Server for integration tests
- `confluentinc/cp-kafka:7.9.1` - Kafka for integration tests

**How it Works**:
1. Images are loaded from GitHub Actions cache if available
2. Missing images are pulled from container registries
3. All images are saved to cache for subsequent builds
4. Cache is automatically invalidated when POM files change

**Benefits**:
- Significantly faster builds on cache hits (no image download time)
- Reduced network usage and registry rate limiting
- More reliable builds with pre-warmed container images

## Troubleshooting

### Docker Issues

**Testcontainers fails to start**

```bash
# Check Docker is running
docker ps

# Check Docker disk space
docker system df

# Clean up if needed
docker system prune -a
```

### Kafka Test Failures

**EmbeddedKafka timeout**

- Increase test timeout in `@Test` annotation
- Check available memory (Kafka requires ~512MB)
- Verify no port conflicts

**Testcontainers Kafka fails**

- Ensure Docker has sufficient resources
- Check Docker Desktop settings (4GB RAM minimum)
- Review container logs: `docker logs <container-id>`

### Database Test Failures

**SQL Server container fails to start**

```bash
# Check license acceptance
# Ensure .acceptLicense() is called on container

# Verify SQL Server image
docker pull mcr.microsoft.com/mssql/server:2022-latest

# Check Docker resources (SQL Server needs ~2GB RAM)
```

**Connection timeout**

- SQL Server container takes 15-30 seconds to be ready
- Tests use automatic waiting with Testcontainers
- Check Docker container logs for initialization errors

### Performance Issues

**Tests are slow**

```bash
# Skip integration tests for faster feedback
mvn test -DskipITs

# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m"

# For advanced parallel test execution, see:
# docs/junit5-parallelization-specification.md
```

**Note**: For comprehensive strategies to improve test execution time through JUnit 5 parallel execution, refer to the [JUnit 5 Parallelization Specification](junit5-parallelization-specification.md).

### Coverage Report Issues

**Missing coverage data**

```bash
# Ensure you run verify, not just test
mvn clean verify

# Check JaCoCo execution files exist
ls -la target/jacoco-ut/jacoco.exec
ls -la target/jacoco-it/jacoco.exec
```

## Best Practices

### Writing Tests

1. **Follow AAA Pattern**: Arrange, Act, Assert
   ```java
   @Test
   void shouldCreateOrderWithOutboxEvent() {
       // Arrange
       CreateOrderRequest request = new CreateOrderRequest(...);
       
       // Act
       Order order = orderService.createOrder(request);
       
       // Assert
       assertThat(order).isNotNull();
       assertThat(outboxEvents).hasSize(1);
   }
   ```

2. **Use Meaningful Test Names**: Describe what the test verifies
   - Good: `shouldCreateOrderWithOutboxEventInSameTransaction`
   - Bad: `testOrder1`

3. **Keep Tests Focused**: One test should verify one behavior

4. **Use AssertJ**: For readable, fluent assertions
   ```java
   assertThat(events)
       .hasSize(1)
       .first()
       .extracting(OutboxEvent::getEventType)
       .isEqualTo("OrderCreated");
   ```

5. **Clean Up Resources**: Use `@AfterEach` for cleanup when needed

### Integration Tests

1. **Use Testcontainers**: For real database/Kafka testing
2. **Reuse Containers**: Use static containers to avoid repeated startup
3. **Use @DynamicPropertySource**: To configure Spring properties from containers
4. **Test Transactions**: Verify transactional boundaries
5. **Use Awaitility**: For asynchronous operations
   ```java
   await().atMost(10, SECONDS)
       .until(() -> outboxRepository.findAll().size() == 1);
   ```

### Performance Tests

1. **Establish Baselines**: Record initial performance metrics
2. **Test Realistic Scenarios**: Use production-like data volumes
3. **Monitor Resources**: CPU, memory, database connections
4. **Ramp Up Gradually**: Simulate realistic user growth
5. **Test Failure Scenarios**: Network failures, database slowdowns

### Architecture Tests

1. **Document Rules**: Explain why each rule exists
2. **Run in CI**: Catch violations early
3. **Update Tests**: As architecture evolves
4. **Be Pragmatic**: Allow exceptions when justified

## Additional Resources

- **[JUnit 5 Parallelization Specification](junit5-parallelization-specification.md)** - Comprehensive guide for parallel test execution
- **[JMeter Testing](../jmeter-tests/README.md)** - Detailed performance testing guide
- **[Architecture Documentation](architecture.md)** - System design and patterns
- **[Quick Start Guide](quick-start.md)** - Setup and running the application
- **[Testcontainers Documentation](https://www.testcontainers.org/)** - Container testing
- **[ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)** - Architecture testing

## Test Performance Optimization

### Overview
This section summarizes CI build performance optimizations made to reduce build times from 10-12 minutes to approximately 8-9 minutes.

### Changes Implemented

#### 1. GitHub Actions Workflow Optimizations
- **Upgraded Maven caching**: Changed from separate `actions/cache@v3` to built-in `cache: 'maven'` in `setup-java@v4`
  - This is more efficient and better maintained
  - Automatically handles Maven-specific caching patterns
  
- **Consolidated Maven commands**: Combined two separate Maven runs into one
  - Before: `mvn clean package` followed by `mvn verify -B`
  - After: `mvn clean verify -B`
  - Eliminates duplicate compilation and packaging work
  - Saves approximately 1-2 minutes per build

#### 2. Testcontainers Reuse Configuration
- **Added `testcontainers.properties`** in both `routebox-server` and `order-service` test resources
  - Enables container reuse: `testcontainers.reuse.enable=true`
  
- **Updated all Testcontainer instances** to enable reuse with `.withReuse(true)`:
  - `E2EPollerTest`: MSSQL + Kafka containers
  - `E2EPollerMultiClusterTest`: MSSQL + 2 Kafka containers
  - `OrderServiceTest`: MSSQL container
  - `OrderServiceFailureTest`: MSSQL container
  - 11 additional test classes with MSSQL containers

### Performance Results

#### Test Execution Time Improvements
| Test Class | Before | After | Improvement |
|------------|--------|-------|-------------|
| E2EPollerTest | 54.32s | 22.21s | **59% faster** (-32s) |
| E2EPollerMultiClusterTest | 21.30s | 20.45s | 4% faster (-0.85s) |
| OrderServiceTest | 13.36s | 14.46s | Within margin |
| **Total Test Time** | **5:55** | **4:43** | **20% faster** (-72s) |

#### Expected CI Build Improvements
- Maven dependency caching: ~10-20s improvement on cache hits
- Single Maven run: ~1-2 minutes savings
- Testcontainers reuse: ~1 minute savings
- **Total estimated savings: 3-4 minutes per CI run**

### How Testcontainers Reuse Works

When `testcontainers.reuse.enable=true` is set and containers are marked with `.withReuse(true)`:

1. **First test run**: Containers are created and started normally
2. **Subsequent test runs**: 
   - Testcontainers looks for existing containers with matching configuration
   - If found, reuses the existing container instead of creating a new one
   - Significantly reduces the overhead of container startup time

This is especially beneficial for:
- Local development (fast test-debug cycles)
- CI environments with Docker layer caching
- Tests using heavy containers like MSSQL Server

### Slowest Tests Identified

The following tests use Testcontainers and are the slowest in the suite:

1. **E2EPollerTest** (22.21s) - Full end-to-end test with MSSQL + Kafka
2. **E2EPollerMultiClusterTest** (20.45s) - Multi-cluster routing test with MSSQL + 2 Kafka instances
3. **OrderServiceTest** (14.46s) - Service integration test with MSSQL

These tests are integration tests by nature but follow the current naming convention. They are critical for validating the outbox pattern implementation and multi-cluster routing.

### Future Optimization Opportunities

1. **Consider marking E2E tests as integration tests**
   - Rename to `*IT.java` or `*IntegrationTest.java`
   - Would allow running unit tests separately in CI for faster feedback
   - Integration tests could run in a separate job or stage

2. **Parallel test execution**
   - Maven Surefire supports parallel execution
   - Would need careful testing with Testcontainers to avoid resource contention
   - May provide marginal benefit given current test suite size

3. **Docker layer caching**
   - GitHub Actions supports Docker layer caching
   - Could further reduce Testcontainers startup time
   - Requires configuration in workflow

4. **Test optimization**
   - Some tests could potentially use lighter databases (H2, in-memory)
   - However, tests using MSSQL-specific features need to remain as-is
   - Consider mocking external dependencies where appropriate

### Recommendations

1. **Monitor CI build times** over the next few builds to validate improvements
2. **Keep Testcontainers reuse enabled** as it provides significant benefits
3. **Consider splitting test stages** if build times remain problematic:
   - Fast unit tests (< 5 min target)
   - Slower integration tests (separate job)
4. **Document the importance of container reuse** for developers

## Summary

The RouteBox project has comprehensive test coverage across multiple levels:

- ✅ **29 test classes** covering unit, integration, and E2E scenarios
- ✅ **Testcontainers** for realistic database and Kafka testing  
- ✅ **ArchUnit** for automated architecture validation
- ✅ **JMeter** for performance and load testing
- ✅ **JaCoCo** for code coverage tracking and reporting
- ✅ **Virtual thread testing** for concurrent processing validation
- ✅ **Performance optimizations** reducing CI build time by 20-30%

This multi-layered approach ensures reliability, performance, and maintainability of the transactional outbox pattern implementation.
