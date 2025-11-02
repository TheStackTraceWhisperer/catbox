# Monitoring and Observability

This document describes the monitoring and observability features in the Catbox project.

## Overview

The application includes comprehensive custom metrics for monitoring the outbox pattern health and performance. All metrics are exposed via the Prometheus actuator endpoint at `/actuator/prometheus`.

## Custom Outbox Metrics

### Gauges

Gauges provide real-time measurements of specific values.

#### `outbox_events_pending`

**Description:** Number of pending events in the outbox (not yet sent)

**Use Case:** Monitor the backlog of events waiting to be processed

**Alert Threshold:** Consider alerting if this value exceeds 100 for extended periods

#### `outbox_events_oldest_age_seconds`

**Description:** Age of the oldest unsent event (in seconds)

**Use Case:** Detect processing delays or stalled event processing

**Alert Threshold:** Consider alerting if this value exceeds 300 seconds (5 minutes)

### Counters

Counters track cumulative totals that only increase over time.

#### `outbox_events_published_success_total`

**Description:** Total number of successful event publications

**Use Case:** Track overall throughput and success rate

**Calculation:** Divide by time window to get events/second

#### `outbox_events_published_failure_total`

**Description:** Total number of failed event publications

**Use Case:** Monitor error rate and system health

**Alert Threshold:** Alert on sustained increase in failure rate

### Histograms

Histograms track the distribution of values over time.

#### `outbox_events_processing_duration_seconds`

**Description:** Event processing duration from claim to publish

**Percentiles Tracked:**
- p50 (median) - Typical processing time
- p95 - 95% of events process faster than this
- p99 - 99% of events process faster than this

**Use Case:** Monitor processing performance and identify slowdowns

## Accessing Metrics

### Via HTTP Endpoint

View metrics directly from the catbox-server:

```bash
curl http://localhost:8081/actuator/prometheus | grep outbox_events
```

### Via Prometheus

Prometheus scrapes metrics from the application every 15 seconds (configurable in `infrastructure/monitoring/prometheus/prometheus.yml`).

Access Prometheus UI:
```
http://localhost:9090
```

Example queries:
```promql
# Current pending events
outbox_events_pending

# Event publishing rate (events per second)
rate(outbox_events_published_success_total[1m])

# Failure rate (failures per second)
rate(outbox_events_published_failure_total[1m])

# 95th percentile processing duration
histogram_quantile(0.95, rate(outbox_events_processing_duration_seconds_bucket[5m]))
```

## Grafana Dashboard

A pre-configured Grafana dashboard is available in `infrastructure/monitoring/grafana/dashboards/catbox-dashboard.json`.

### Dashboard Panels

1. **Outbox Pending Events** (Gauge)
   - Real-time count of pending events
   - Color-coded thresholds (green < 50, yellow < 100, red >= 100)

2. **Oldest Unsent Event Age** (Gauge)
   - Age of oldest pending event in seconds
   - Alert threshold at 300 seconds (5 minutes)

3. **Event Publishing Rate** (Time Series)
   - Success rate (green line)
   - Failure rate (red line)
   - Shows events/second over time

4. **Event Processing Duration** (Time Series)
   - p50 (median) - blue line
   - p95 - yellow line
   - p99 - red line
   - Shows processing latency percentiles

5. **Total Events Published** (Counter)
   - Cumulative success count
   - Cumulative failure count

### Accessing Grafana

1. Navigate to `http://localhost:3000`
2. Login with default credentials:
   - Username: `admin`
   - Password: `admin`
3. Navigate to Dashboards → Catbox Outbox Dashboard

## Log Aggregation with Loki

Loki provides centralized log storage and querying capabilities.

### Viewing Logs

**In Grafana:**
1. Navigate to Explore (compass icon in sidebar)
2. Select Loki as the data source
3. Use LogQL queries to search logs:

```logql
# All logs from catbox-server
{container_name="catbox-server"}

# Error logs only
{container_name="catbox-server"} |= "ERROR"

# Logs containing "OutboxEvent"
{container_name="catbox-server"} |= "OutboxEvent"
```

**Via Loki API:**
```bash
# Query last 1 hour of logs
curl -G -s "http://localhost:3100/loki/api/v1/query_range" \
  --data-urlencode 'query={container_name="catbox-server"}' \
  --data-urlencode "start=$(date -d '1 hour ago' +%s)000000000" \
  --data-urlencode "end=$(date +%s)000000000"
```

## Monitoring Recommendations

### Alert Configurations

Set up alerts for the following conditions:

#### High Backlog
```yaml
alert: HighOutboxBacklog
expr: outbox_events_pending > 100
for: 5m
severity: warning
description: Outbox has more than 100 pending events for 5 minutes
```

#### Stalled Processing
```yaml
alert: StalledOutboxProcessing
expr: outbox_events_oldest_age_seconds > 300
for: 2m
severity: critical
description: Oldest event is more than 5 minutes old
```

#### High Failure Rate
```yaml
alert: HighOutboxFailureRate
expr: rate(outbox_events_published_failure_total[5m]) > 0.1
for: 2m
severity: warning
description: Event publishing failure rate exceeds 10%
```

#### Slow Processing
```yaml
alert: SlowOutboxProcessing
expr: histogram_quantile(0.95, rate(outbox_events_processing_duration_seconds_bucket[5m])) > 10
for: 5m
severity: warning
description: 95th percentile processing time exceeds 10 seconds
```

### Trends to Monitor

**Publishing Throughput:**
- Track events/second over time
- Compare peak vs. average rates
- Identify capacity limits

**Processing Latency:**
- Monitor p95 and p99 percentiles
- Watch for gradual increases indicating performance degradation
- Compare processing times across different event types

**Success/Failure Ratios:**
- Calculate failure percentage: `failures / (successes + failures)`
- Normal operation should have < 1% failure rate
- Investigate sustained increases

**Backlog Growth:**
- Monitor rate of change in pending events
- Positive slope indicates publishing can't keep up with creation
- Negative slope indicates healthy processing

## Health Checks

Both applications expose health check endpoints:

```bash
# Order Service health
curl http://localhost:8080/actuator/health

# Catbox Server health
curl http://localhost:8081/actuator/health
```

**Successful Response:**
```json
{
  "status": "UP"
}
```

Use health endpoints for:
- Load balancer health checks
- Container orchestration (Kubernetes liveness/readiness probes)
- Monitoring system up/down status

## Monitoring Stack Architecture

```
┌─────────────────┐
│ catbox-server   │──── metrics ────┐
└─────────────────┘                  │
                                     │
┌─────────────────┐                  ▼
│ order-service   │──── metrics ───►┌────────────┐
└─────────────────┘                 │ Prometheus │
                                    └──────┬─────┘
┌─────────────────┐                        │
│ Application     │──── logs ─────┐        │
│ Containers      │                │        │
└─────────────────┘                ▼        │
                              ┌──────┐      │
                              │ Loki │      │
                              └───┬──┘      │
                                  │         │
                                  └────┬────┘
                                       │
                                       ▼
                                  ┌─────────┐
                                  │ Grafana │
                                  └─────────┘
```

## Configuration Files

All monitoring configuration files are located in `infrastructure/monitoring/`:

- **Prometheus:** `prometheus/prometheus.yml`
- **Grafana Dashboards:** `grafana/dashboards/`
- **Grafana Datasources:** `grafana/provisioning/datasources/`
- **Loki:** `loki/loki-config.yml`
- **Promtail:** `promtail/promtail-config.yml`
