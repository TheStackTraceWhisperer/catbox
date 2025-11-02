# JMeter Test Suite - Quick Reference

## Prerequisites
- Docker
- Both Order Service and Catbox Server running

## Quick Start (3 Steps)

### 1. Start Infrastructure
```bash
cd jmeter-tests
./scripts/start-infrastructure.sh
```

### 2. Start Services (in 2 separate terminals)
```bash
# Terminal 1 - Order Service
mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql

# Terminal 2 - Catbox Server  
mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql
```

### 3. Run Tests
```bash
cd jmeter-tests

# Single test
./scripts/run-test.sh order    # Order Service (50 threads, 5 min)
./scripts/run-test.sh outbox   # Outbox Service (30 threads, 5 min)
./scripts/run-test.sh stress   # Stress test (100 threads, 10 min)

# All tests
./scripts/run-all-tests.sh
```

## Test Plans

| Test | Threads | Duration | Purpose |
|------|---------|----------|---------|
| `order` | 50 | 5 min | Load test Order Service API |
| `outbox` | 30 | 5 min | Load test Outbox Service API |
| `stress` | 100 | 10 min | End-to-end stress test |

## Custom Parameters

```bash
# Syntax
./scripts/run-test.sh <test> -t <threads> -r <ramp-up> -d <duration>

# Examples
./scripts/run-test.sh order -t 100 -r 60 -d 600     # 100 threads, 10 min
./scripts/run-test.sh stress -t 200 -r 120 -d 1800  # 200 threads, 30 min
```

## Results Location

```
jmeter-tests/results/
├── order_service_<timestamp>.jtl         # Raw data
├── order_service_report_<timestamp>/     # HTML report
│   └── index.html                        # Open this in browser
├── outbox_service_<timestamp>.jtl
├── outbox_service_report_<timestamp>/
├── stress_test_<timestamp>.jtl
└── stress_test_report_<timestamp>/
```

## Key Metrics to Check

1. **Throughput**: Requests per second
2. **Response Time**: 90th, 95th, 99th percentiles
3. **Error Rate**: Should be < 1%
4. **Pending Events**: Should not grow unbounded

## Expected Performance (Local)

| Operation | Throughput | Avg Response | 95th %ile |
|-----------|-----------|--------------|-----------|
| Create Order | 50-100/s | 50-100ms | 200ms |
| Get Orders | 100-200/s | 20-50ms | 100ms |
| Update Order | 50-100/s | 50-100ms | 200ms |
| Get Pending Events | 50-100/s | 30-60ms | 150ms |

## Troubleshooting

### Services Not Running
```bash
# Check health
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

### High Error Rates
- Reduce thread count
- Increase ramp-up time
- Check application logs

### Docker Network Issues
- On Linux: Uses --network=host
- On macOS/Windows: Uses host.docker.internal
- Verify services are accessible

## Advanced Usage

### Direct Docker Command
```bash
docker run --rm \
  --network=host \
  -v "$(pwd)/testplans:/tests" \
  -v "$(pwd)/testdata:/testdata" \
  -v "$(pwd)/results:/results" \
  justb4/jmeter:5.6.3 \
  -n -t "/tests/OrderService_LoadTest.jmx" \
  -Jnum.threads=75 \
  -Jramp.up=45 \
  -Jduration=600 \
  -l "/results/custom_test.jtl" \
  -e -o "/results/custom_report"
```

### Monitor During Test
```bash
# Pending events
watch -n 2 'curl -s http://localhost:8081/api/outbox-events/pending | jq length'

# Metrics
curl http://localhost:8080/actuator/prometheus | grep outbox_events
```

## Documentation

- **Full Guide**: [README.md](README.md)
- **Implementation Details**: [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)
- **Main Project**: [../README.md](../README.md)

## Test Data

Located in `testdata/orders.csv`:
- 50 unique customer/product combinations
- Price range: $4.99 - $1,299.99
- Automatically recycled for continuous testing
