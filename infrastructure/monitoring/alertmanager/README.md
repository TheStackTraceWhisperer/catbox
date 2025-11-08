# Alertmanager Configuration

This directory contains the Prometheus Alertmanager configuration for the RouteBox project.

## Files

- **alertmanager.yml** - Main Alertmanager configuration including routing and notification settings
- **alert-rules.yml** - Prometheus alert rule definitions

## Alert Rules Summary

The project includes 12 pre-configured alert rules across 3 groups:

### Outbox Pattern Alerts (8 rules)

1. **HighOutboxBacklog** (Warning)
   - Triggers when pending events > 100 for 5 minutes
   - Indicates processing can't keep up with event creation

2. **CriticalOutboxBacklog** (Critical)
   - Triggers when pending events > 1000 for 10 minutes
   - Requires immediate attention

3. **StalledOutboxProcessing** (Critical)
   - Triggers when oldest event > 5 minutes old
   - May indicate routebox-server is down or stuck

4. **NoEventProcessing** (Critical)
   - Triggers when pending events exist but no publishes for 10 minutes
   - Complete processing failure

5. **HighOutboxFailureRate** (Warning)
   - Triggers when failures > 0.1 events/second for 2 minutes
   - Check Kafka connectivity

6. **SlowOutboxProcessing** (Warning)
   - Triggers when p95 processing time > 10 seconds for 5 minutes
   - Performance degradation

7. **DeadLetterQueueGrowth** (Critical)
   - Triggers on any dead letter queue entries
   - Each event requires investigation

8. **ArchiveTableLargeSize** (Warning)
   - Triggers when archive > 1 million events
   - Consider data retention policies

### Application Health Alerts (2 rules)

9. **RouteBoxServerDown** (Critical)
   - Triggers when metrics unavailable for 2 minutes
   - Application may be down

10. **OrderServiceDown** (Critical)
    - Triggers when metrics unavailable for 2 minutes
    - Application may be down

### JVM Alerts (2 rules)

11. **HighJVMMemoryUsage** (Warning)
    - Triggers when heap usage > 90% for 5 minutes
    - May need to increase heap or investigate leaks

12. **FrequentGarbageCollection** (Warning)
    - Triggers when GC runs > 10 times/second for 5 minutes
    - Memory pressure indicator

## Notification Configuration

### Email Routing

All alerts are routed to email notifications via Mailhog (for testing):

- **SMTP Server**: mailhog:1025
- **From**: alertmanager@routebox.local
- **To**: routebox-alerts@example.com

### Alert Grouping

Alerts are grouped by:
- Alert name
- Severity level

### Timing

- **Critical alerts**: 10s group wait, 30m repeat interval
- **Warning alerts**: 30s group wait, 3h repeat interval

## Testing

To test the alerting pipeline:

```bash
# 1. Start services
cd infrastructure
docker compose up -d

# 2. Validate configuration
./validate-alerting.sh

# 3. Access UIs
# Prometheus: http://localhost:9090/alerts
# Alertmanager: http://localhost:9093
# Mailhog: http://localhost:8025

# 4. See detailed testing guide
cat monitoring/TESTING.md
```

## Customization

### Modifying Alert Thresholds

Edit `alert-rules.yml` and change the `expr` or `for` values:

```yaml
- alert: HighOutboxBacklog
  expr: outbox_events_pending > 50  # Changed from 100
  for: 2m                            # Changed from 5m
```

Then reload Prometheus:
```bash
docker compose restart prometheus
# Or hot reload:
curl -X POST http://localhost:9090/-/reload
```

### Changing Email Recipients

Edit `alertmanager.yml`:

```yaml
receivers:
  - name: 'email-notifications'
    email_configs:
      - to: 'your-email@example.com'
```

Then reload Alertmanager:
```bash
docker compose restart alertmanager
# Or hot reload:
curl -X POST http://localhost:9093/-/reload
```

### Adding New Alert Channels

Alertmanager supports multiple notification channels:
- Email
- Slack
- PagerDuty
- Webhook
- OpsGenie
- VictorOps
- WeChat
- And more...

See [Alertmanager documentation](https://prometheus.io/docs/alerting/latest/configuration/) for details.

## Production Deployment

⚠️ **This configuration is for development/testing only.**

For production:
1. Replace Mailhog with a real email service
2. Configure TLS/SSL for Alertmanager
3. Set up authentication
4. Use secrets management for credentials
5. Configure multiple notification channels
6. Set up on-call rotation
7. Implement alert silencing workflows

## Resources

- [Prometheus Alerting Documentation](https://prometheus.io/docs/alerting/latest/overview/)
- [Alertmanager Configuration](https://prometheus.io/docs/alerting/latest/configuration/)
- [Alert Rule Best Practices](https://prometheus.io/docs/practices/alerting/)
- [Project Monitoring Documentation](../../docs/monitoring.md)
