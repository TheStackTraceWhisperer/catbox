# JMeter Test Suite for Catbox

This directory contains comprehensive JMeter test plans for stress testing the Catbox application, including the Order Service and Outbox pattern implementation.

## Directory Structure

```
jmeter-tests/
├── testplans/           # JMeter test plan files (.jmx)
│   ├── OrderService_LoadTest.jmx
│   ├── OutboxService_LoadTest.jmx
│   └── EndToEnd_StressTest.jmx
├── testdata/            # Test data files
│   └── orders.csv
├── scripts/             # Helper scripts
│   ├── run-all-tests.sh
│   └── start-infrastructure.sh
├── results/             # Test results (generated)
└── README.md           # This file
```

## Prerequisites

### Required Software

1. **Apache JMeter 5.5+**
   ```bash
   # Download and install JMeter
   wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz
   tar -xzf apache-jmeter-5.6.3.tgz
   export JMETER_HOME=/path/to/apache-jmeter-5.6.3
   export PATH=$PATH:$JMETER_HOME/bin
   ```

2. **Java 21** (already required for the application)

3. **Docker & Docker Compose** (for infrastructure)

### Running Services

Before running tests, ensure both services and infrastructure are running:

```bash
# 1. Start infrastructure (Azure SQL Edge and Kafka)
cd /path/to/catbox
docker compose up -d

# 2. Start Order Service (in one terminal)
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql

# 3. Start Catbox Server (in another terminal)
mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql
```

Wait 10-15 seconds for services to be fully ready before starting tests.

## Test Plans

### 1. Order Service Load Test (`OrderService_LoadTest.jmx`)

Tests the Order Service API under various load conditions.

**Thread Groups:**
- **Order Creation**: Tests POST /api/orders endpoint
- **Order Read Operations**: Tests GET /api/orders endpoint
- **Order Update Operations**: Tests PATCH /api/orders/{id}/status endpoint

**Default Configuration:**
- Threads: 50 concurrent users
- Ramp-up: 30 seconds
- Duration: 300 seconds (5 minutes)

**Running the test:**
```bash
# GUI Mode (for development/debugging)
jmeter -t testplans/OrderService_LoadTest.jmx

# CLI Mode (for actual load testing)
jmeter -n -t testplans/OrderService_LoadTest.jmx \
  -l results/order_service_$(date +%Y%m%d_%H%M%S).jtl \
  -e -o results/order_service_report_$(date +%Y%m%d_%H%M%S)

# With custom parameters
jmeter -n -t testplans/OrderService_LoadTest.jmx \
  -Jnum.threads=100 \
  -Jramp.up=60 \
  -Jduration=600 \
  -l results/order_service_stress.jtl
```

**Assertions:**
- HTTP 201 for order creation
- HTTP 200 for read operations
- Valid JSON responses with order ID

### 2. Outbox Service Load Test (`OutboxService_LoadTest.jmx`)

Tests the Outbox Service API and event retrieval performance.

**Thread Groups:**
- **Query Outbox Events**: Tests GET /api/outbox-events and /api/outbox-events/pending
- **Search Outbox Events**: Tests paginated search with various parameters

**Default Configuration:**
- Threads: 30 concurrent users
- Ramp-up: 20 seconds
- Duration: 300 seconds (5 minutes)

**Running the test:**
```bash
# GUI Mode
jmeter -t testplans/OutboxService_LoadTest.jmx

# CLI Mode
jmeter -n -t testplans/OutboxService_LoadTest.jmx \
  -l results/outbox_service_$(date +%Y%m%d_%H%M%S).jtl \
  -e -o results/outbox_service_report_$(date +%Y%m%d_%H%M%S)

# With custom parameters
jmeter -n -t testplans/OutboxService_LoadTest.jmx \
  -Jnum.threads=50 \
  -Jramp.up=30 \
  -Jduration=600 \
  -l results/outbox_service_load.jtl
```

**Assertions:**
- HTTP 200 for all GET requests
- Valid JSON array responses
- Proper pagination structure

### 3. End-to-End Stress Test (`EndToEnd_StressTest.jmx`)

Comprehensive stress test simulating high load on both services simultaneously.

**Thread Groups:**
- **High Volume Order Creation**: 100 threads creating orders continuously
- **High Volume Order Updates**: 100 threads updating order statuses
- **Outbox Monitoring Load**: 50 threads querying pending events

**Default Configuration:**
- Threads: 100 concurrent users per service
- Ramp-up: 60 seconds
- Duration: 600 seconds (10 minutes)

**Running the test:**
```bash
# GUI Mode (NOT recommended for stress testing)
jmeter -t testplans/EndToEnd_StressTest.jmx

# CLI Mode (RECOMMENDED)
jmeter -n -t testplans/EndToEnd_StressTest.jmx \
  -l results/stress_test_$(date +%Y%m%d_%H%M%S).jtl \
  -e -o results/stress_test_report_$(date +%Y%m%d_%H%M%S)

# High stress configuration
jmeter -n -t testplans/EndToEnd_StressTest.jmx \
  -Jstress.threads=200 \
  -Jramp.up=120 \
  -Jduration=1800 \
  -l results/extreme_stress.jtl
```

**What this test validates:**
- Order creation throughput under high load
- Database transaction performance with concurrent writes
- Outbox event generation and storage
- Event processing performance (monitor pending events)
- System stability under sustained load

## Test Parameters

All test plans support customization via JMeter properties:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `order.service.host` | localhost | Order Service hostname |
| `order.service.port` | 8080 | Order Service port |
| `outbox.service.host` | localhost | Outbox Service hostname |
| `outbox.service.port` | 8081 | Outbox Service port |
| `num.threads` | 50 | Number of concurrent threads |
| `ramp.up` | 30 | Ramp-up period in seconds |
| `duration` | 300 | Test duration in seconds |
| `stress.threads` | 100 | Number of threads for stress test |

## Analyzing Results

### Real-time Monitoring

While tests are running, monitor:

1. **Application Metrics (Prometheus):**
   ```bash
   # Order Service metrics
   curl http://localhost:8080/actuator/prometheus | grep outbox_events
   
   # Catbox Server metrics
   curl http://localhost:8081/actuator/prometheus | grep outbox_events
   ```

2. **Database Activity:**
   ```bash
   # Check pending events count
   curl http://localhost:8081/api/outbox-events/pending | jq length
   ```

3. **JMeter GUI (if running in GUI mode):**
   - View Results Tree: Individual request/response details
   - Summary Report: Aggregated statistics
   - Aggregate Graph: Visual performance metrics

### Post-Test Analysis

After running tests in CLI mode, JMeter generates HTML reports:

```bash
# Open the HTML report in browser
open results/order_service_report_*/index.html
```

**Key Metrics to Review:**

1. **Throughput**: Requests per second
   - Order creation: Target > 50 req/sec
   - Order reads: Target > 100 req/sec
   - Order updates: Target > 50 req/sec

2. **Response Times**:
   - 90th percentile < 500ms
   - 95th percentile < 1000ms
   - 99th percentile < 2000ms

3. **Error Rate**:
   - Target: < 0.1% for normal load
   - Target: < 1% for stress tests

4. **Outbox Processing**:
   - Events should be processed within 5-10 seconds
   - Pending event count should not grow unbounded

## Performance Benchmarks

### Expected Performance (Local Development)

| Operation | Throughput | Avg Response Time | 95th Percentile |
|-----------|------------|-------------------|-----------------|
| Create Order | 50-100 req/s | 50-100ms | 200ms |
| Get Orders | 100-200 req/s | 20-50ms | 100ms |
| Update Order Status | 50-100 req/s | 50-100ms | 200ms |
| Get Pending Events | 50-100 req/s | 30-60ms | 150ms |
| Search Events (paginated) | 40-80 req/s | 40-80ms | 180ms |

### Expected Performance (Docker Compose)

Performance may be 20-40% lower when running with Docker Compose due to containerization overhead.

### Virtual Threads Impact

The application uses Java 21 virtual threads, which should maintain performance even with:
- 200+ concurrent threads
- Thousands of active connections
- High I/O operations (database + Kafka)

## Troubleshooting

### Common Issues

1. **Connection Refused Errors**
   ```
   Solution: Ensure both services are running and healthy
   Check: curl http://localhost:8080/actuator/health
   Check: curl http://localhost:8081/actuator/health
   ```

2. **High Error Rates**
   ```
   Possible causes:
   - Database connection pool exhausted
   - Services overwhelmed
   - Network issues
   
   Solutions:
   - Reduce thread count
   - Increase ramp-up time
   - Check application logs
   ```

3. **Pending Events Growing**
   ```
   Possible causes:
   - Catbox server not processing fast enough
   - Kafka issues
   - Database locks
   
   Solutions:
   - Check catbox-server logs
   - Verify Kafka is running
   - Check database performance
   ```

4. **OutOfMemory Errors in JMeter**
   ```
   Solution: Increase JMeter heap size
   export HEAP="-Xms1g -Xmx4g -XX:MaxMetaspaceSize=512m"
   jmeter -n -t testplans/EndToEnd_StressTest.jmx ...
   ```

## Best Practices

1. **Always run load tests in CLI mode** (`-n` flag)
   - GUI mode consumes significant resources
   - CLI mode provides better performance metrics

2. **Use appropriate ramp-up times**
   - Prevents overwhelming the system immediately
   - Allows connection pools to scale
   - Recommended: 1 second per 2-3 threads

3. **Monitor system resources**
   - CPU usage
   - Memory consumption
   - Database connections
   - Disk I/O

4. **Run tests incrementally**
   - Start with low load (25 threads)
   - Gradually increase to find breaking point
   - Document the sweet spot for your environment

5. **Clean up between tests**
   ```bash
   # Stop services
   # Clear database (if needed)
   # Restart services
   # Wait for stabilization
   ```

## Continuous Integration

Example GitHub Actions workflow snippet:

```yaml
- name: Run JMeter Load Tests
  run: |
    # Install JMeter
    wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.6.3.tgz
    tar -xzf apache-jmeter-5.6.3.tgz
    export PATH=$PATH:$(pwd)/apache-jmeter-5.6.3/bin
    
    # Start services
    docker compose up -d
    mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql &
    mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql &
    sleep 20
    
    # Run tests
    cd jmeter-tests
    jmeter -n -t testplans/OrderService_LoadTest.jmx \
      -Jnum.threads=25 \
      -Jduration=60 \
      -l results/ci_test.jtl \
      -e -o results/ci_report
```

## Additional Resources

- [Apache JMeter Documentation](https://jmeter.apache.org/usermanual/index.html)
- [JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)
- [Catbox Application README](../README.md)
- [Catbox Testing Guide](../TESTING.md)

## Support

For issues or questions:
1. Check application logs in both services
2. Review JMeter test logs in `jmeter.log`
3. Verify infrastructure is healthy with `docker compose ps`
4. Check system resources (CPU, memory, disk)
