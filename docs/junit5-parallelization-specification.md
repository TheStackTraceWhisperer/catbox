# JUnit 5 Test Parallelization Specification

## Overview

This specification outlines the strategy and implementation details for enabling JUnit 5 test parallelization in the RouteBox project to improve test execution time and developer productivity.

## Executive Summary

**Goal**: Reduce total test execution time by running independent tests concurrently.

**Current State**:
- 52 test classes (49 unit tests, 3 integration tests)
- 37 tests use Spring Boot (`@SpringBootTest`)
- 25 tests use Testcontainers
- Sequential test execution
- Total test time: ~4-5 minutes (unit tests), ~6-7 minutes (full suite with integration tests)

**Expected Benefits**:
- 30-50% reduction in test execution time for unit tests
- Faster developer feedback loop
- Better CI/CD pipeline performance
- More efficient use of multi-core CI runners

## JUnit 5 Parallel Execution Capabilities

### Execution Modes

JUnit 5 provides two levels of parallelization:

#### 1. Same Thread Execution (Default)
All tests run sequentially in the same thread.

#### 2. Concurrent Execution
Tests can run in parallel at different levels:
- **Method-level**: Test methods within a class run in parallel
- **Class-level**: Test classes run in parallel
- **Module-level**: Test modules run in parallel (controlled by Maven)

### Execution Strategies

JUnit 5 supports two parallel execution strategies:

#### Dynamic Strategy (Recommended)
- Automatically adjusts thread pool size based on available processors
- Formula: `desired parallelism × (1 + factor)`
- Default factor: 1.0 (results in 2× CPU cores)
- Configuration:
  ```properties
  junit.jupiter.execution.parallel.config.strategy=dynamic
  junit.jupiter.execution.parallel.config.dynamic.factor=1.0
  ```

#### Fixed Strategy
- Uses a fixed number of threads
- Useful for resource-constrained environments
- Configuration:
  ```properties
  junit.jupiter.execution.parallel.config.strategy=fixed
  junit.jupiter.execution.parallel.config.fixed.parallelism=4
  ```

#### Custom Strategy
- Implement `ParallelExecutionConfigurationStrategy` interface
- Allows fine-grained control over thread allocation

## Configuration Options

### 1. JUnit Platform Properties

Create `src/test/resources/junit-platform.properties` in each module:

```properties
# Enable parallel execution
junit.jupiter.execution.parallel.enabled=true

# Execution mode - run top-level classes in parallel
junit.jupiter.execution.parallel.mode.default=concurrent

# Run methods within a class sequentially (safer for state-dependent tests)
junit.jupiter.execution.parallel.mode.classes.default=concurrent

# Thread pool configuration - dynamic strategy
junit.jupiter.execution.parallel.config.strategy=dynamic
junit.jupiter.execution.parallel.config.dynamic.factor=1.0

# Alternative: Fixed strategy with explicit thread count
# junit.jupiter.execution.parallel.config.strategy=fixed
# junit.jupiter.execution.parallel.config.fixed.parallelism=4
# junit.jupiter.execution.parallel.config.fixed.max-pool-size=8
```

### 2. Maven Surefire Plugin Configuration

Update parent `pom.xml`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <!-- Existing configuration -->
        <argLine>@{surefireArgLine} -XX:+EnableDynamicAgentLoading -Xshare:off 
                 -Djava.util.logging.config.file=${project.basedir}/src/test/resources/logging.properties</argLine>
        
        <!-- Parallel execution at JVM level (optional, use with caution) -->
        <!-- <parallel>classes</parallel> -->
        <!-- <threadCount>4</threadCount> -->
        <!-- <perCoreThreadCount>true</perCoreThreadCount> -->
        
        <!-- Increase timeout for parallel execution -->
        <forkedProcessExitTimeoutInSeconds>120</forkedProcessExitTimeoutInSeconds>
        
        <!-- System properties for parallel execution -->
        <systemPropertyVariables>
            <!-- Force JUnit to respect parallel configuration -->
            <junit.jupiter.execution.parallel.enabled>true</junit.jupiter.execution.parallel.enabled>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

### 3. Granular Control with Annotations

JUnit 5 provides annotations for fine-grained control:

#### @Execution Annotation
```java
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

// Run all methods in this class concurrently
@Execution(ExecutionMode.CONCURRENT)
class FastUnitTest {
    @Test
    void test1() { /* ... */ }
    
    @Test
    void test2() { /* ... */ }
}

// Force sequential execution for a specific class
@Execution(ExecutionMode.SAME_THREAD)
class StatefulTest {
    @Test
    void test1() { /* ... */ }
    
    @Test
    void test2() { /* ... */ }
}
```

#### @ResourceLock Annotation
```java
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

@ResourceLock(value = "database", mode = ResourceAccessMode.READ_WRITE)
class DatabaseTest {
    @Test
    void testDatabaseOperation() { /* ... */ }
}

// Built-in resources
@ResourceLock(Resources.SYSTEM_PROPERTIES)
@ResourceLock(Resources.SYSTEM_OUT)
@ResourceLock(Resources.SYSTEM_ERR)
@ResourceLock(Resources.LOCALE)
```

#### @Isolated Annotation
```java
import org.junit.jupiter.api.parallel.Isolated;

// This class runs in isolation, not concurrently with any other tests
@Isolated
class IntegrationTest {
    @Test
    void complexIntegrationTest() { /* ... */ }
}
```

## Test Categories and Parallelization Strategy

### 1. Fast Unit Tests (Safe for Parallelization)

**Characteristics**:
- No external dependencies (database, Kafka, containers)
- Use mocks/stubs
- Execution time: < 1 second per test
- Stateless or properly isolated state

**Examples**:
- `OutboxMetricsServiceTest`
- `OrderEventProcessingServiceTest`

**Configuration**: Enable parallel execution
```java
@Execution(ExecutionMode.CONCURRENT)
class OutboxMetricsServiceTest {
    // Tests can run in parallel
}
```

### 2. Spring Boot Tests (Use Caution)

**Characteristics**:
- Use `@SpringBootTest` annotation
- Share application context
- May have stateful beans
- Context caching is important for performance

**Challenges**:
- Spring test context caching works best with sequential execution
- Parallel execution can cause multiple contexts to be loaded
- `@DirtiesContext` invalidates cache, causing slowdowns

**Recommendations**:
1. **Keep sequential by default** for Spring Boot tests
2. Use `@Execution(ExecutionMode.SAME_THREAD)` explicitly
3. Minimize use of `@DirtiesContext`
4. Share contexts across tests when possible

**Example**:
```java
@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
class KafkaIntegrationTest {
    // Runs sequentially to preserve Spring context cache
}
```

### 3. Testcontainers Tests (Complex Parallelization)

**Characteristics**:
- Use Docker containers (MSSQL, Kafka)
- Container startup time: 5-15 seconds
- Resource intensive (CPU, memory, disk I/O)
- Use container reuse for optimization

**Challenges**:
- Docker daemon has limited concurrent container operations
- Memory constraints on CI runners
- Port conflicts if not properly configured
- Container cleanup issues

**Current Optimization**: Container reuse (`.withReuse(true)`)
- First test: Creates container (~10-15s)
- Subsequent tests: Reuses container (~1-2s)
- Works best with sequential execution

**Recommendations**:
1. **Run sequentially** to maximize container reuse benefits
2. Use `@Isolated` or `@Execution(ExecutionMode.SAME_THREAD)`
3. Consider grouping Testcontainer tests into separate Maven profile
4. Potential for parallelization with proper Docker resource allocation

**Example**:
```java
@SpringBootTest
@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
class E2EPollerTest {
    @Container
    static MSSQLServerContainer<?> mssql = 
        new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withReuse(true);
    
    // Tests run sequentially to reuse container
}
```

### 4. Architecture Tests (Safe for Parallelization)

**Characteristics**:
- ArchUnit tests validate code structure
- No external dependencies
- CPU-bound (class scanning and analysis)
- Stateless

**Recommendations**: Enable parallel execution
```java
@Execution(ExecutionMode.CONCURRENT)
class LayeringArchitectureTest {
    // Safe to run in parallel
}
```

## Recommended Configuration for RouteBox

### Phase 1: Conservative Approach (Recommended)

Enable parallelization only for fast unit tests:

**Step 1**: Create `junit-platform.properties` for modules with pure unit tests

**routebox-common/src/test/resources/junit-platform.properties**:
```properties
# Enable parallel execution
junit.jupiter.execution.parallel.enabled=true

# Run classes in parallel, methods sequentially
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent

# Dynamic thread pool based on CPU cores
junit.jupiter.execution.parallel.config.strategy=dynamic
junit.jupiter.execution.parallel.config.dynamic.factor=1.0
```

**Step 2**: Mark Spring Boot and Testcontainers tests as sequential

```java
@SpringBootTest
@Testcontainers
@Execution(ExecutionMode.SAME_THREAD)
class E2EPollerTest {
    // Explicitly sequential
}
```

**Expected Results**:
- Fast unit tests: 30-40% time reduction
- Integration tests: No change (remain sequential)
- Overall: 15-25% total time reduction

### Phase 2: Aggressive Approach (Advanced)

Enable broader parallelization with careful resource management:

**Step 1**: Global parallel execution for all modules

**Step 2**: Use resource locks for shared resources

```java
@ResourceLock(value = "testcontainers", mode = ResourceAccessMode.READ_WRITE)
@SpringBootTest
@Testcontainers
class DatabaseTest {
    // Multiple tests can run, but Docker operations are synchronized
}
```

**Step 3**: Configure Maven Surefire for module-level parallelization

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>classesAndMethods</parallel>
        <threadCount>2</threadCount>
        <perCoreThreadCount>false</perCoreThreadCount>
    </configuration>
</plugin>
```

**Expected Results**:
- Fast unit tests: 40-60% time reduction
- Integration tests: 20-30% time reduction (with careful tuning)
- Overall: 30-50% total time reduction

**Risks**:
- Increased memory usage
- Potential for flaky tests
- CI runner resource exhaustion
- Complex debugging

## Implementation Examples

### Example 1: Pure Unit Test (Parallel)

```java
package com.example.routebox.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
class OutboxMetricsServiceTest {

    @Test
    void shouldIncrementSuccessCounter() {
        // Given
        OutboxMetricsService service = new OutboxMetricsService();
        
        // When
        service.recordSuccess();
        
        // Then
        assertThat(service.getSuccessCount()).isEqualTo(1);
    }

    @Test
    void shouldIncrementFailureCounter() {
        // Given
        OutboxMetricsService service = new OutboxMetricsService();
        
        // When
        service.recordFailure();
        
        // Then
        assertThat(service.getFailureCount()).isEqualTo(1);
    }
    
    // These tests can safely run in parallel
}
```

### Example 2: Spring Boot Test (Sequential)

```java
package com.example.routebox.server.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
class KafkaIntegrationTest {

    @Test
    void shouldSendMessageToKafka() {
        // Test implementation
        // Runs sequentially to preserve Spring context cache
    }
}
```

### Example 3: Testcontainers Test (Isolated)

```java
package com.example.routebox.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Isolated  // Ensures this test class runs completely isolated
class E2EPollerTest {

    @Container
    static MSSQLServerContainer<?> mssql = 
        new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withReuse(true);

    @Test
    void shouldPollAndProcessEvents() {
        // E2E test implementation
        // Runs in complete isolation from other tests
    }
}
```

### Example 4: Mixed Test Class with Resource Locks

```java
package com.example.routebox.server.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;

@Execution(ExecutionMode.CONCURRENT)
class OutboxServiceTest {

    @Test
    void shouldProcessEvent() {
        // Safe to run in parallel
    }

    @Test
    @ResourceLock(value = "system.properties", mode = ResourceAccessMode.READ_WRITE)
    void shouldHandleSystemPropertyChange() {
        // Only one test modifying system properties at a time
        System.setProperty("test.key", "value");
        try {
            // Test logic
        } finally {
            System.clearProperty("test.key");
        }
    }
}
```

## Performance Expectations

### Unit Tests (Pure Java, No Spring/Containers)

**Current**: ~1-2 minutes (sequential)
**With Parallelization**: ~30-60 seconds (2-4 cores)
**Speedup**: 2-4× faster

### Spring Boot Tests (No Containers)

**Current**: ~2-3 minutes (sequential, with context caching)
**With Parallelization**: ~1.5-3 minutes (limited benefit due to context management)
**Speedup**: 0-30% faster (or potentially slower if context cache is disrupted)

### Testcontainers Tests

**Current**: ~4-5 minutes (sequential, with container reuse)
**With Parallelization**: 
- Best case: ~3-4 minutes (with resource locks and careful tuning)
- Worst case: ~6-8 minutes (if container reuse is broken)
**Speedup**: Highly variable, 0-25% faster

### Overall Test Suite

**Current**: ~5-7 minutes (full build with verify)
**Expected with Conservative Approach**: ~4-6 minutes (15-25% reduction)
**Expected with Aggressive Approach**: ~3.5-5 minutes (30-40% reduction, with risks)

### CI Environment Considerations

GitHub Actions runners (ubuntu-latest):
- 2-core CPU
- 7 GB RAM
- Limited Docker resources

**Recommendation**: Use conservative approach for CI, more aggressive locally

## Trade-offs and Risks

### Benefits

1. **Faster Feedback**: Reduced test execution time
2. **Better Resource Utilization**: Use all available CPU cores
3. **Improved Developer Experience**: Faster local test runs
4. **CI Cost Reduction**: Shorter pipeline execution time

### Risks and Challenges

1. **Flaky Tests**: Race conditions become more apparent
2. **Resource Exhaustion**: Memory, CPU, Docker resources
3. **Test Interference**: Shared state, system properties, files
4. **Debugging Complexity**: Harder to reproduce failures
5. **Context Cache Invalidation**: Spring Boot tests may run slower
6. **Container Reuse Breaking**: Testcontainers optimization may be negated
7. **Increased Memory Usage**: Multiple Spring contexts loaded simultaneously

### Mitigation Strategies

1. **Start Conservative**: Enable parallelization incrementally
2. **Monitor Metrics**: Track test execution time and flakiness
3. **Use Resource Locks**: Protect shared resources explicitly
4. **Isolate Heavy Tests**: Use `@Isolated` for integration tests
5. **Test Locally**: Validate parallel execution on developer machines first
6. **CI/CD Tuning**: Adjust thread counts for CI environment
7. **Document Decisions**: Explain why tests are parallel or sequential

## Thread Safety Checklist

Before enabling parallelization, ensure tests are thread-safe:

- [ ] No shared mutable state between test methods
- [ ] No static fields modified during tests (or properly synchronized)
- [ ] No system property modifications (or use `@ResourceLock`)
- [ ] No file system operations on same files (or use unique file names)
- [ ] No singleton pattern violations
- [ ] No assumptions about test execution order
- [ ] Proper cleanup in `@AfterEach` and `@AfterAll`
- [ ] Thread-safe test fixtures and utilities

## Spring Boot Specific Considerations

### Application Context Caching

Spring Boot's test context caching is crucial for performance:

```java
// Same context configuration = context reused
@SpringBootTest(properties = "spring.profiles.active=test")
class Test1 { }

@SpringBootTest(properties = "spring.profiles.active=test")
class Test2 { }  // Reuses context from Test1

// Different configuration = new context created
@SpringBootTest(properties = "spring.profiles.active=prod")
class Test3 { }  // Creates new context
```

**With Parallel Execution**:
- Multiple threads may load contexts simultaneously
- Increased memory usage (multiple contexts in memory)
- Potential for context initialization race conditions

**Recommendation**:
1. Minimize context configuration variations
2. Use `@DirtiesContext` sparingly
3. Group tests with same context configuration
4. Consider sequential execution for Spring Boot tests

### Test Slices

Use Spring Boot test slices for faster, more focused tests:

```java
@WebMvcTest(OrderController.class)  // Only web layer
@DataJpaTest  // Only JPA layer
@JsonTest  // Only JSON serialization
```

These are lighter and more suitable for parallelization.

## Testcontainers Specific Considerations

### Container Reuse Strategy

Current RouteBox configuration:
```properties
# testcontainers.properties
testcontainers.reuse.enable=true
```

```java
@Container
static MSSQLServerContainer<?> mssql = 
    new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
        .acceptLicense()
        .withReuse(true);
```

**How it works**:
1. First test creates container (~15 seconds)
2. Subsequent tests reuse same container (~1 second)
3. Container stays running between test classes

**Parallel Execution Impact**:
- Multiple test classes may request same container simultaneously
- Testcontainers handles this with locking
- But parallel creation of different containers may exceed Docker resources

### Parallel Testcontainers Best Practices

1. **Use Singleton Containers**: Share containers across all tests
   ```java
   public abstract class AbstractIntegrationTest {
       protected static final MSSQLServerContainer<?> MSSQL = 
           new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
               .acceptLicense()
               .withReuse(true);
       
       static {
           MSSQL.start();
       }
   }
   ```

2. **Resource Locks for Docker Operations**:
   ```java
   @ResourceLock(value = "docker", mode = ResourceAccessMode.READ)
   @Testcontainers
   class DatabaseTest {
       // Prevents too many concurrent Docker operations
   }
   ```

3. **Network Isolation**: Use Testcontainers networks to avoid port conflicts

4. **Memory Limits**: Configure container memory limits
   ```java
   mssql.withCreateContainerCmdModifier(cmd -> 
       cmd.getHostConfig().withMemory(512 * 1024 * 1024L));  // 512MB
   ```

## Implementation Roadmap

### Phase 1: Analysis and Preparation (Week 1)

- [ ] Audit all test classes for thread safety
- [ ] Identify pure unit tests (safe for parallelization)
- [ ] Identify tests with shared state or resources
- [ ] Create baseline performance metrics
- [ ] Review Spring Boot test context configurations

### Phase 2: Conservative Implementation (Week 2)

- [ ] Create `junit-platform.properties` for module with pure unit tests
- [ ] Add `@Execution(ExecutionMode.SAME_THREAD)` to Spring Boot tests
- [ ] Add `@Isolated` to Testcontainers tests
- [ ] Run tests locally and verify no failures
- [ ] Measure performance improvement
- [ ] Update documentation

### Phase 3: Validation and Tuning (Week 3)

- [ ] Run parallelized tests on CI environment
- [ ] Monitor for flaky tests
- [ ] Adjust thread pool configuration if needed
- [ ] Validate container reuse still works
- [ ] Compare before/after metrics

### Phase 4: Gradual Expansion (Optional, Week 4+)

- [ ] Gradually enable parallelization for more test classes
- [ ] Implement resource locks where needed
- [ ] Consider module-level parallelization
- [ ] Optimize Spring Boot test context sharing
- [ ] Advanced Testcontainers parallelization strategies

## Monitoring and Metrics

### Key Metrics to Track

1. **Test Execution Time**
   - Total time for `mvn test`
   - Total time for `mvn verify`
   - Per-module execution time
   - Per-test-class execution time

2. **Test Stability**
   - Flaky test rate
   - Test failure patterns
   - Failure reproducibility

3. **Resource Usage**
   - Peak memory usage
   - CPU utilization
   - Docker container count
   - Disk I/O

4. **CI/CD Impact**
   - Pipeline duration
   - Success rate
   - Resource costs

### Measurement Commands

```bash
# Measure test execution time
time mvn clean test

# Measure with parallelization
time mvn clean test -Djunit.jupiter.execution.parallel.enabled=true

# Measure per-test execution time
mvn test -Djunit.platform.output.capture.stdout=true

# Monitor resource usage during tests
docker stats  # Monitor container resources
htop          # Monitor CPU/memory
```

## Troubleshooting Guide

### Problem: Tests fail when run in parallel but pass sequentially

**Diagnosis**: Test has shared state or ordering dependency

**Solutions**:
1. Add `@Execution(ExecutionMode.SAME_THREAD)` to test class
2. Use `@ResourceLock` to synchronize access to shared resources
3. Refactor test to eliminate shared state
4. Use `@BeforeEach` to properly initialize test state

### Problem: Slower execution with parallelization

**Diagnosis**: Context cache invalidation or resource contention

**Solutions**:
1. Reduce thread pool size: `dynamic.factor=0.5`
2. Check for excessive context creation (Spring Boot)
3. Verify container reuse is working (Testcontainers)
4. Use `@Execution(ExecutionMode.SAME_THREAD)` for Spring Boot tests

### Problem: Out of memory errors

**Diagnosis**: Too many Spring contexts or Docker containers in memory

**Solutions**:
1. Reduce thread count
2. Minimize Spring Boot test context variations
3. Use test slices instead of `@SpringBootTest`
4. Increase Maven memory: `export MAVEN_OPTS="-Xmx4096m"`
5. Configure container memory limits

### Problem: Docker daemon errors

**Diagnosis**: Too many concurrent container operations

**Solutions**:
1. Use `@ResourceLock` for Testcontainers tests
2. Reduce parallelism: `fixed.parallelism=2`
3. Use singleton container pattern
4. Increase Docker daemon resources

### Problem: Port conflicts

**Diagnosis**: Multiple containers trying to bind to same port

**Solutions**:
1. Use dynamic port allocation: `container.getMappedPort()`
2. Run Testcontainers tests sequentially: `@Isolated`
3. Use Testcontainers networks for isolation

## References and Resources

### Official Documentation

- [JUnit 5 User Guide - Parallel Execution](https://junit.org/junit5/docs/current/user-guide/#writing-tests-parallel-execution)
- [Maven Surefire Plugin - Parallel Test Execution](https://maven.apache.org/surefire/maven-surefire-plugin/examples/fork-options-and-parallel-execution.html)
- [Spring Boot Testing - Test Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers - Advanced Options](https://www.testcontainers.org/features/advanced_options/)

### Articles and Best Practices

- "Parallel Test Execution in JUnit 5" - Baeldung
- "Spring Boot Test Context Caching" - Spring Blog
- "Optimizing Testcontainers for CI/CD" - Testcontainers Blog

### RouteBox Specific

- [Testing Guide](testing.md) - Current testing documentation
- [Virtual Threads](virtual-threads.md) - Java 21 virtual threads usage
- [Architecture](architecture.md) - System architecture overview

## Decision Matrix

Use this matrix to decide parallelization strategy for each test:

| Test Type | External Dependencies | Execution Time | State Management | Recommended Strategy |
|-----------|----------------------|----------------|------------------|---------------------|
| Pure Unit Test | None | < 100ms | Stateless | `@Execution(CONCURRENT)` |
| Unit Test with Mocks | Mocks only | < 500ms | Stateless | `@Execution(CONCURRENT)` |
| Spring Boot Test | Spring Context | 1-5s | Shared Context | `@Execution(SAME_THREAD)` |
| Testcontainers Test | Docker + Spring | 5-15s | Container + Context | `@Isolated` |
| E2E Test | Multiple Containers | 15-30s | Complex State | `@Isolated` |
| Architecture Test | None | 1-3s | Read-only | `@Execution(CONCURRENT)` |

## Conclusion

JUnit 5 test parallelization offers significant potential for reducing test execution time in the RouteBox project. However, the benefits must be balanced against the complexity and risks introduced by concurrent test execution.

**Recommended Approach**:
1. Start with **Phase 1: Conservative Approach**
2. Enable parallelization only for pure unit tests
3. Keep Spring Boot and Testcontainers tests sequential
4. Monitor metrics and stability
5. Gradually expand if results are positive

**Expected Outcome**:
- 15-25% reduction in overall test execution time
- Maintained test stability
- Minimal changes to existing tests
- Foundation for future optimization

This specification provides a comprehensive framework for implementing test parallelization while managing the inherent complexities of the RouteBox test suite, which includes Spring Boot integration tests and Testcontainers-based tests.
