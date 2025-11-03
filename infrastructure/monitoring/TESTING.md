# Testing the Alerting Pipeline

This document provides step-by-step instructions for testing the complete alerting pipeline in the Catbox project.

## Prerequisites

- Docker and Docker Compose installed
- All infrastructure services running

## Starting the Infrastructure

1. Navigate to the infrastructure directory:
   ```bash
   cd infrastructure
   ```

2. Set the database password:
   ```bash
   export DB_PASSWORD='YourStrong!Passw0rd'
   ```

3. Start all services:
   ```bash
   docker compose up -d
   ```

4. Wait for all services to be healthy (this may take 1-2 minutes):
   ```bash
   docker compose ps
   ```

   All services should show "healthy" or "running" status.

## Verifying the Monitoring Stack

### 1. Prometheus

Access Prometheus at http://localhost:9090

**Verify Prometheus is scraping targets:**
- Navigate to Status → Targets
- You should see:
  - `prometheus` (1/1 up)
  - `order-service` (may show 0/1 if not running - this is expected)
  - `catbox-server` (may show 0/1 if not running - this is expected)

**Verify alert rules are loaded:**
- Navigate to Status → Rules
- You should see 3 rule groups:
  - `outbox_alerts` (8 rules)
  - `application_health` (2 rules)
  - `jvm_alerts` (2 rules)

**View current alerts:**
- Navigate to Alerts
- All alerts should be in "Inactive" state initially

### 2. Alertmanager

Access Alertmanager at http://localhost:9093

**Initial state:**
- The main page should show "No alerts"
- Navigate to Status to see configuration details

### 3. Mailhog

Access Mailhog at http://localhost:8025

**Initial state:**
- Should show empty inbox (no messages)
- This is where alert emails will appear

## Testing Alerts

### Test 1: Application Down Alert

This test verifies that alerts fire when an application is unavailable.

1. **Check initial state:**
   - In Prometheus (http://localhost:9090/alerts), verify all alerts are "Inactive"

2. **Wait for alert to fire:**
   - Since catbox-server and order-service are not running, the `CatboxServerDown` and `OrderServiceDown` alerts should fire
   - Wait 2-3 minutes for the alert to transition from Pending to Firing

3. **Verify in Prometheus:**
   - Navigate to http://localhost:9090/alerts
   - You should see `CatboxServerDown` and/or `OrderServiceDown` in "Firing" state

4. **Verify in Alertmanager:**
   - Navigate to http://localhost:9093
   - The alerts should appear in the Alertmanager UI

5. **Check email in Mailhog:**
   - Navigate to http://localhost:8025
   - Within 10-30 seconds of the alert firing, an email should appear
   - Click on the email to view the alert details

### Test 2: Manual Alert Testing with Prometheus

You can manually test the alerting system using Prometheus' query interface:

1. Navigate to http://localhost:9090/graph

2. Execute a query that would trigger an alert, for example:
   ```promql
   up{job="catbox-server"}
   ```

3. This will show 0 (down) if catbox-server is not running

4. Check the Alerts page to see the corresponding alert

### Test 3: Testing with Running Applications

To test alerts based on application metrics:

1. **Start the applications:**
   ```bash
   # Terminal 1 - Create database
   docker exec catbox-azuresql /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -Q "CREATE DATABASE catbox" -C -No
   
   # Terminal 2 - Start catbox-server
   export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
   export PATH=$JAVA_HOME/bin:$PATH
   cd /path/to/catbox
   mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql
   
   # Terminal 3 - Start order-service
   mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql
   ```

2. **Verify applications are being scraped:**
   - In Prometheus, go to Status → Targets
   - Both `catbox-server` and `order-service` should now show "UP"

3. **The application down alerts should resolve:**
   - The `CatboxServerDown` and `OrderServiceDown` alerts should transition to resolved
   - Mailhog should receive "resolved" email notifications

4. **Create events to test metric-based alerts:**
   ```bash
   # Create an order to generate outbox events
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{"customerName": "Test", "productName": "Widget", "amount": 99.99}'
   ```

5. **Monitor the metrics:**
   ```promql
   # In Prometheus, query:
   outbox_events_pending
   outbox_events_published_success_total
   ```

## Expected Alert Email Format

When an alert fires, you should receive an email in Mailhog with:

**Subject:** `[Catbox FIRING] <AlertName>`

**Body:** HTML formatted with:
- Alert name
- Status (FIRING or RESOLVED)
- Severity (critical or warning)
- Application name
- Description
- Summary
- Start time

## Customizing for Your Environment

### Changing Alert Email Recipients

Edit `infrastructure/monitoring/alertmanager/alertmanager.yml`:

```yaml
receivers:
  - name: 'email-notifications'
    email_configs:
      - to: 'your-email@example.com'  # Change this
```

Then restart Alertmanager:
```bash
docker compose restart alertmanager
```

### Modifying Alert Thresholds

Edit `infrastructure/monitoring/alertmanager/alert-rules.yml`:

```yaml
# Example: Change high backlog threshold from 100 to 50
- alert: HighOutboxBacklog
  expr: outbox_events_pending > 50  # Changed from 100
  for: 5m
```

Then reload Prometheus configuration:
```bash
docker compose restart prometheus
# Or use hot reload:
curl -X POST http://localhost:9090/-/reload
```

## Troubleshooting

### Alerts not firing

1. **Check Prometheus is evaluating rules:**
   - Navigate to http://localhost:9090/rules
   - Verify rules are loaded

2. **Check alert expression manually:**
   - Go to http://localhost:9090/graph
   - Execute the alert's `expr` query
   - See if it returns results

3. **Check Prometheus logs:**
   ```bash
   docker compose logs prometheus
   ```

### Emails not appearing in Mailhog

1. **Check Alertmanager is sending:**
   ```bash
   docker compose logs alertmanager
   ```

2. **Verify Mailhog is running:**
   ```bash
   docker compose ps mailhog
   ```

3. **Check Alertmanager configuration:**
   - Navigate to http://localhost:9093/#/status
   - Verify SMTP configuration

### Services won't start

1. **Check all ports are available:**
   ```bash
   netstat -tuln | grep -E '9090|9093|8025|1025'
   ```

2. **Check Docker logs:**
   ```bash
   docker compose logs -f
   ```

3. **Verify configuration files:**
   ```bash
   # Check YAML syntax
   python3 -c "import yaml; yaml.safe_load(open('monitoring/prometheus/prometheus.yml'))"
   python3 -c "import yaml; yaml.safe_load(open('monitoring/alertmanager/alertmanager.yml'))"
   python3 -c "import yaml; yaml.safe_load(open('monitoring/alertmanager/alert-rules.yml'))"
   ```

## Production Considerations

⚠️ **Important:** This setup is designed for **DEVELOPMENT AND TESTING ONLY**.

For production:
1. Replace Mailhog with a real email server (SendGrid, AWS SES, etc.)
2. Configure proper email authentication
3. Set up secure TLS/SSL for Alertmanager
4. Use strong passwords and authentication
5. Configure proper DNS and firewall rules
6. Set up alert routing to multiple channels (email, Slack, PagerDuty, etc.)
7. Implement alert silencing and maintenance windows
8. Monitor the monitoring stack itself

## Summary

This testing guide covers:
- ✅ Starting the complete monitoring infrastructure
- ✅ Verifying Prometheus, Alertmanager, and Mailhog
- ✅ Testing application down alerts
- ✅ Testing metric-based alerts
- ✅ Customizing alert configuration
- ✅ Troubleshooting common issues

For more information, see:
- [Monitoring Documentation](../docs/monitoring.md)
- [Docker Setup](../docs/docker-setup.md)
- [Infrastructure README](../infrastructure/README.md)
