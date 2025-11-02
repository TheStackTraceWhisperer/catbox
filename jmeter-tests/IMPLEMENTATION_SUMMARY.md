# JMeter Test Suite Implementation Summary

## Overview

This document provides a comprehensive summary of the JMeter test suite implementation for the Catbox application. The test suite is designed to stress test the Order Service, Outbox Service, and the transactional outbox pattern implementation.

## What Was Implemented

### 1. Test Plans (JMX Files)

Three comprehensive JMeter test plans were created:

#### a. OrderService_LoadTest.jmx
- **Purpose**: Load testing the Order Service API endpoints
- **Thread Groups**:
  - Order Creation (50 threads, 5 min): Tests POST /api/orders with realistic order data
  - Order Read Operations (50 threads, 5 min): Tests GET /api/orders to measure read performance
  - Order Update Operations (50 threads, 5 min): Tests PATCH /api/orders/{id}/status with various status values
- **Features**:
  - CSV data-driven testing with 50 realistic customer/product combinations
  - Response assertions (HTTP 201 for creation, HTTP 200 for reads)
  - JSON path extraction for order IDs
  - Think times to simulate realistic user behavior
  - Configurable parameters (threads, ramp-up, duration)

#### b. OutboxService_LoadTest.jmx
- **Purpose**: Load testing the Outbox Service API endpoints
- **Thread Groups**:
  - Query Outbox Events (30 threads, 5 min): Tests GET /api/outbox-events and /api/outbox-events/pending
  - Search Outbox Events (30 threads, 5 min): Tests paginated search with various parameters
- **Features**:
  - Tests all outbox query endpoints
  - Validates JSON response structures
  - Tests pagination functionality
  - Configurable parameters

#### c. EndToEnd_StressTest.jmx
- **Purpose**: Comprehensive stress test simulating high concurrent load
- **Thread Groups**:
  - High Volume Order Creation (100 threads, 10 min): Continuous order creation
  - High Volume Order Updates (100 threads, 10 min): Continuous order status updates
  - Outbox Monitoring Load (50 threads, 10 min): Continuous monitoring of pending events
- **Features**:
  - Simulates realistic production load
  - Tests both services simultaneously
  - Monitors pending event accumulation
  - Configurable stress parameters (up to 200+ threads supported)
  - Generates timestamped results for comparison

### 2. Test Data

#### orders.csv
- 50 realistic customer names and product names
- Varied product prices from $4.99 to $1,299.99
- Recyclable data set for continuous load testing
- Supports unlimited test duration through data recycling

### 3. Helper Scripts

#### start-infrastructure.sh
- Automated script to start Docker Compose infrastructure
- Checks for Docker availability
- Starts Azure SQL Edge and Kafka services
- Provides next-step instructions for starting application services

#### run-test.sh
- Quick script to run individual test plans
- Supports custom parameters for threads, ramp-up, and duration
- Usage examples:
  ```bash
  ./run-test.sh order              # Default: 50 threads, 5 min
  ./run-test.sh stress -t 150 -d 900   # Custom: 150 threads, 15 min
  ```
- Automatically generates timestamped results and HTML reports

#### run-all-tests.sh
- Comprehensive script to run all three test plans sequentially
- Checks service availability before starting tests
- Provides breaks between tests to let services stabilize
- Generates complete test results with HTML reports
- Provides summary of all test results at the end

### 4. Documentation

#### jmeter-tests/README.md
Comprehensive documentation covering:
- Prerequisites (JMeter installation, Java 21, Docker)
- Detailed description of each test plan
- Default configurations and parameters
- CLI commands for running tests
- Performance benchmarks and expected results
- Assertions and validation criteria
- Troubleshooting guide
- Best practices for load testing
- CI/CD integration examples

### 5. Project Integration

#### Updated main README.md
- Added JMeter test suite to features list
- Added new "Load and Stress Testing" section
- Updated project structure to include jmeter-tests directory
- Provided quick start commands for running tests

## Test Capabilities

### Performance Testing Scenarios

1. **Basic Load Testing**:
   - 50 concurrent users creating/updating orders
   - 30 concurrent users querying outbox events
   - Sustainable for 5-10 minutes

2. **Stress Testing**:
   - 100-200 concurrent users across all operations
   - Tests system limits and breaking points
   - Monitors pending event accumulation
   - Sustainable for 10-30 minutes

3. **Endurance Testing**:
   - Can be configured for longer durations (1+ hours)
   - Monitors memory leaks and resource exhaustion
   - Validates virtual threads performance over time

### Key Metrics Measured

1. **Throughput**:
   - Requests per second for each endpoint
   - Total transactions processed

2. **Response Times**:
   - Average response time
   - 90th, 95th, 99th percentiles
   - Min/Max response times

3. **Error Rates**:
   - HTTP error codes
   - Assertion failures
   - Connection errors

4. **System Performance**:
   - Pending event accumulation
   - Event processing lag
   - Database transaction performance

### Test Validation

Each test includes multiple assertions:
- HTTP response codes (201 for POST, 200 for GET/PATCH)
- JSON structure validation
- Required field presence (order ID, event data)
- Data integrity checks

## Usage Examples

### Quick Start

```bash
# 1. Start infrastructure
cd jmeter-tests
./scripts/start-infrastructure.sh

# 2. Start services (in separate terminals)
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql
mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql

# 3. Run a single test
./scripts/run-test.sh order

# 4. Or run all tests
./scripts/run-all-tests.sh
```

### Advanced Usage

```bash
# Custom load test - 75 threads for 10 minutes
./scripts/run-test.sh order -t 75 -d 600

# Extreme stress test - 200 threads for 30 minutes
./scripts/run-test.sh stress -t 200 -r 120 -d 1800

# Direct JMeter invocation with custom parameters
jmeter -n -t testplans/EndToEnd_StressTest.jmx \
  -Jstress.threads=150 \
  -Jduration=900 \
  -l results/custom_test.jtl \
  -e -o results/custom_report
```

## Expected Results

### Performance Benchmarks (Local Development)

Based on the application architecture with Java 21 virtual threads:

| Endpoint | Expected Throughput | Avg Response | 95th Percentile |
|----------|-------------------|--------------|-----------------|
| POST /api/orders | 50-100 req/s | 50-100ms | 200ms |
| GET /api/orders | 100-200 req/s | 20-50ms | 100ms |
| PATCH /api/orders/{id}/status | 50-100 req/s | 50-100ms | 200ms |
| GET /api/outbox-events/pending | 50-100 req/s | 30-60ms | 150ms |

### Stress Test Observations

With 100 concurrent threads:
- Order creation rate: 80-120 orders/second
- Event processing: Events should be processed within 5-10 seconds
- Pending events: Should not accumulate unbounded (max ~100-200 pending)
- Error rate: Should be < 1%

## Technical Highlights

### Virtual Threads Performance

The test suite is designed to validate Java 21 virtual threads:
- Can handle 100-200+ concurrent threads efficiently
- Maintains low latency even under high concurrency
- No thread pool exhaustion issues

### Outbox Pattern Validation

Tests verify the transactional outbox pattern:
- Every order creation generates an outbox event
- Events are processed concurrently by catbox-server
- No event loss under high load
- At-least-once delivery guarantees

### Database Performance

Tests stress database operations:
- Concurrent inserts (orders + outbox events)
- Concurrent updates (order status changes)
- SELECT FOR UPDATE SKIP LOCKED performance
- Transaction isolation and consistency

## Files Created

```
jmeter-tests/
├── README.md (10KB - comprehensive documentation)
├── results/.gitignore (excludes test results from git)
├── scripts/
│   ├── run-all-tests.sh (5.3KB - run all tests sequentially)
│   ├── run-test.sh (3.3KB - run individual tests)
│   └── start-infrastructure.sh (1.3KB - start Docker services)
├── testdata/
│   └── orders.csv (1.6KB - 50 test records)
└── testplans/
    ├── EndToEnd_StressTest.jmx (25KB - comprehensive stress test)
    ├── OrderService_LoadTest.jmx (29KB - order service load test)
    └── OutboxService_LoadTest.jmx (24KB - outbox service load test)
```

## Benefits

1. **Comprehensive Coverage**: Tests all major API endpoints and operations
2. **Realistic Scenarios**: Uses actual customer/product data with realistic think times
3. **Scalability Testing**: Can easily scale from 10 to 200+ concurrent users
4. **Automated Execution**: Scripts make it easy to run tests consistently
5. **Detailed Reports**: HTML reports provide deep insights into performance
6. **CI/CD Ready**: Can be integrated into continuous integration pipelines
7. **Performance Baselines**: Establishes benchmarks for future optimization
8. **Bottleneck Identification**: Helps identify performance issues early

## Next Steps

To use the test suite:

1. **Initial Baseline**: Run tests with default parameters to establish baseline performance
2. **Gradual Load Increase**: Incrementally increase thread counts to find breaking points
3. **Long-Duration Tests**: Run endurance tests (1+ hours) to check for memory leaks
4. **Production Simulation**: Configure tests to match expected production load patterns
5. **Continuous Monitoring**: Integrate with CI/CD to catch performance regressions

## Conclusion

The JMeter test suite provides a comprehensive, production-ready framework for stress testing the Catbox application. It validates the performance of the transactional outbox pattern, tests the effectiveness of Java 21 virtual threads, and provides valuable insights into system behavior under load.
