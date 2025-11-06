# Virtual Threads

This document describes the Java 21 virtual threads implementation in the RouteBox project.

## Overview

The application leverages Java 21 virtual threads for concurrent processing. Virtual threads are lightweight threads managed by the JVM rather than the operating system.

## Why Virtual Threads?

### Traditional Platform Threads

- **Heavy:** Each thread consumes ~1MB of stack memory
- **Limited:** Practical limit of thousands of threads
- **OS-managed:** Context switching overhead
- **Expensive:** Thread creation and destruction costs

### Virtual Threads (Java 21+)

- **Lightweight:** Minimal memory footprint (~1KB)
- **Scalable:** Millions of threads possible
- **JVM-managed:** Efficient scheduling and context switching
- **Cheap:** Fast creation and disposal

### Benefits for Outbox Pattern

The outbox pattern characteristics when using virtual threads:

1. **High Concurrency:** Can process hundreds of events simultaneously
2. **I/O Bound:** Publishing to Kafka involves network I/O
3. **Variable Load:** Event volume fluctuates; concurrency scales dynamically
4. **Code Pattern:** Synchronous code that executes asynchronously

## Virtual Threads in RouteBox

### 1. Web Request Handling

Tomcat's protocol handler uses virtual threads for processing HTTP requests.

**Configuration:** `routebox-server/src/main/java/com/routebox/config/WebConfig.java`

```java
@Bean
public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
    return protocolHandler -> {
        protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    };
}
```

**Impact:**
- Each HTTP request runs in a virtual thread
- Supports thousands of concurrent requests
- No thread pool configuration required
- Scales based on demand

### 2. Async Task Execution

Spring's async task executor uses virtual threads for `@Async` methods.

**Configuration:** `routebox-server/src/main/java/com/routebox/config/AsyncConfig.java`

```java
@Bean
public AsyncTaskExecutor applicationTaskExecutor() {
    TaskExecutorAdapter adapter = new TaskExecutorAdapter(
        Executors.newVirtualThreadPerTaskExecutor()
    );
    adapter.setTaskDecorator(new MdcTaskDecorator());
    return adapter;
}
```

**Impact:**
- Async methods execute in virtual threads
- No fixed thread pool size
- Scales with workload
- MDC (Mapped Diagnostic Context) preserved across threads

### 3. Event Processing

Each outbox event is processed in its own virtual thread.

**Implementation:** `routebox-server/src/main/java/com/routebox/service/OutboxEventPoller.java`

```java
claimedEvents.forEach(event -> {
    Thread.ofVirtual().start(() -> {
        try {
            outboxEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Error processing event {}", event.getId(), e);
        }
    });
});
```

**Impact:**
- 100 events = 100 virtual threads spawned
- No thread pool exhaustion
- Parallel processing of all events
- Failed events don't block others

## Performance Characteristics

### Throughput

Virtual threads enable massive parallel processing:

```
Platform Threads (pool of 100):
- Max concurrent events: 100
- Additional events: Queued
- Throughput: Limited by pool size

Virtual Threads:
- Max concurrent events: Thousands
- Additional events: New thread spawned
- Throughput: Limited by CPU and I/O, not threads
```

### Resource Usage

Memory consumption comparison:

```
1000 Platform Threads:
- Memory: ~1000 MB (1MB per thread)
- OS overhead: Significant
- Context switching: Expensive

1000 Virtual Threads:
- Memory: ~1-10 MB (varies by workload)
- OS overhead: Minimal (few carrier threads)
- Context switching: JVM-managed, efficient
```

### Latency

Virtual threads reduce latency for I/O-bound operations:

- **Blocking I/O:** Thread parks instead of consuming CPU
- **No Thread Pool:** No queue wait time
- **Fast Context Switch:** JVM scheduler is more efficient than OS

## Code Patterns

### Creating Virtual Threads

**Simple Task:**
```java
Thread.ofVirtual().start(() -> {
    // Task code
});
```

**With Return Value:**
```java
Future<String> future = Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
    return "Result";
});
String result = future.get();
```

**Structured Concurrency (Java 21+):**
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> result1 = scope.fork(() -> fetchFromCluster1());
    Future<String> result2 = scope.fork(() -> fetchFromCluster2());
    
    scope.join();
    scope.throwIfFailed();
    
    return List.of(result1.resultNow(), result2.resultNow());
}
```

### Best Practices

**DO:**
- ✅ Use for I/O-bound operations (network, database, file I/O)
- ✅ Create threads as needed; they have low cost
- ✅ Use blocking APIs (they work with virtual threads)
- ✅ Use synchronized blocks sparingly (they can pin threads)

**DON'T:**
- ❌ Don't use for CPU-intensive operations
- ❌ Don't reuse virtual threads (create new ones)
- ❌ Don't use thread pools (defeats the purpose)
- ❌ Don't assume thread identity is stable

### Pinning Considerations

Virtual threads can "pin" to carrier threads in certain situations:

**Causes of Pinning:**
1. **Synchronized blocks:** JVM can't unmount thread
2. **Native methods:** Thread must complete on same carrier
3. **Foreign function calls:** Similar to native methods

**Impact:**
- Pinned virtual thread blocks its carrier thread
- Reduces available carriers for other virtual threads
- Can limit concurrency

**Solutions:**
```java
// Instead of synchronized
synchronized (lock) {
    // Critical section
}

// Use ReentrantLock
lock.lock();
try {
    // Critical section
} finally {
    lock.unlock();
}
```

## Monitoring Virtual Threads

### JVM Metrics

Monitor virtual thread usage via JMX:

```bash
# Enable JMX
java -Dcom.sun.management.jmxremote \
     -Dcom.sun.management.jmxremote.port=9999 \
     -Dcom.sun.management.jmxremote.authenticate=false \
     -jar routebox-server.jar
```

### Thread Dumps

Virtual threads appear in thread dumps:

```bash
jcmd <pid> Thread.dump_to_file -format=json /tmp/thread-dump.json
```

### Logging

Enable virtual thread debugging:

```java
System.setProperty("jdk.tracePinnedThreads", "full");
```

This logs whenever a virtual thread pins to its carrier.

## Migration from Platform Threads

If you're migrating from platform threads:

### Before (Platform Threads)
```java
@Bean
public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(100);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("async-");
    executor.initialize();
    return executor;
}
```

### After (Virtual Threads)
```java
@Bean
public AsyncTaskExecutor applicationTaskExecutor() {
    return new TaskExecutorAdapter(
        Executors.newVirtualThreadPerTaskExecutor()
    );
}
```

**Changes:**
- No pool size configuration
- No queue capacity tuning
- Simpler configuration
- Different scaling characteristics

## Requirements

### Java Version

Virtual threads require **Java 21 or higher**.

**Verify your version:**
```bash
java -version
# Should show: openjdk version "21" or higher
```

**Maven configuration:**
```xml
<properties>
    <java.version>21</java.version>
</properties>
```

### JVM Options

No special JVM options required for basic virtual threads usage.

**Optional tuning:**
```bash
# Adjust carrier thread count (default: number of processors)
-Djdk.virtualThreadScheduler.parallelism=16

# Enable pinning detection
-Djdk.tracePinnedThreads=full
```

## Testing with Virtual Threads

### Unit Tests

Virtual threads work seamlessly in unit tests:

```java
@Test
void testVirtualThreads() throws Exception {
    var executor = Executors.newVirtualThreadPerTaskExecutor();
    
    Future<String> future = executor.submit(() -> {
        return "Test result";
    });
    
    assertEquals("Test result", future.get());
}
```

### Load Testing

Virtual threads show their benefits under high load:

```bash
# Run JMeter load tests
cd jmeter-tests
./scripts/run-test.sh stress
```

Monitor:
- Throughput increases with virtual threads
- Memory usage remains stable
- No thread pool exhaustion errors

## Further Reading

**Official Documentation:**
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [Virtual Threads Guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)

**Related Features:**
- [Structured Concurrency (JEP 453)](https://openjdk.org/jeps/453)
- [Scoped Values (JEP 446)](https://openjdk.org/jeps/446)
