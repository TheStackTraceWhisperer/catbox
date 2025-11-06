# Production Deployment Guide

This guide provides configuration information and considerations for deploying RouteBox in production environments.

## Overview

The Docker Compose setup in this repository is designed for **development and testing**. Production deployments require additional considerations for security, scalability, reliability, and observability.

## Kafka Infrastructure

### Production Kafka Configuration

For production Kafka deployments, consider the following configuration options:

#### 1. Hardware and Resources

**Configuration per broker:**
- CPU: 4-8 cores
- Memory: 8-16 GB RAM (increase based on throughput)
- Storage: SSD-backed volumes with sufficient IOPS
  - Minimum 500 GB for data retention
  - Separate volumes for data and logs
- Network: 10 Gbps network interfaces

**Scaling considerations:**
- Run at least 3 brokers for high availability
- Use replication factor of 3 for critical topics
- Set `min.insync.replicas` to 2 for durability
- Distribute brokers across availability zones

#### 2. Security Configuration

**Production configuration options:**

✅ **SSL/TLS Encryption**
- Use CA-signed certificates (not self-signed)
- Configure mutual TLS (mTLS) for client authentication
- Enable `ssl.endpoint.identification.algorithm=https`
- Use only TLS 1.2 and TLS 1.3

✅ **SASL Authentication**
- Use SCRAM-SHA-512 mechanism
- Store credentials in secure secret management (Vault, AWS Secrets Manager, etc.)
- Rotate credentials regularly (every 90 days)
- Use unique credentials per service/application

✅ **Authorization (ACLs)**
- Enable ACLs with `allow.everyone.if.no.acl.found=false`
- Follow principle of least privilege
- Grant specific permissions per user/service:
  - Producers: WRITE, DESCRIBE on specific topics
  - Consumers: READ, DESCRIBE on topics; READ on consumer groups
- Document all ACL rules

✅ **Network Security**
- Place Kafka brokers in private subnets
- Use security groups/firewalls to restrict access
- Disable PLAINTEXT listeners in production
- Consider using VPC peering or VPN for cross-region access

#### 3. Performance Tuning

**Broker configuration:**
```properties
# Replication
default.replication.factor=3
min.insync.replicas=2
replica.lag.time.max.ms=30000

# Log retention
log.retention.hours=168  # 7 days
log.retention.bytes=-1   # No size limit, time-based only
log.segment.bytes=1073741824  # 1 GB

# Performance
num.network.threads=8
num.io.threads=16
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600

# Compression
compression.type=producer  # Let producers decide

# Log cleanup
log.cleanup.policy=delete
```

**Producer configuration (in application):**
```yaml
properties:
  linger.ms: 10
  batch.size: 16384
  buffer.memory: 33554432
  compression.type: snappy
  max.in.flight.requests.per.connection: 5
  acks: all
  enable.idempotence: true
  retries: 2147483647
  request.timeout.ms: 30000
  delivery.timeout.ms: 120000
```

#### 4. Monitoring and Observability

**Metrics to monitor:**
- Broker metrics:
  - CPU and memory utilization
  - Disk I/O and utilization
  - Network throughput
  - Under-replicated partitions
  - Offline partitions
- Producer metrics:
  - Record send rate
  - Record error rate
  - Request latency
  - Buffer availability
- Consumer lag (if applicable)

**Recommended tools:**
- Prometheus + Grafana for metrics
- Kafka JMX exporter
- Confluent Control Center (if using Confluent Platform)
- APM tools (Datadog, New Relic, etc.)

**Enable JMX monitoring:**
```yaml
environment:
  KAFKA_JMX_PORT: 9999
  KAFKA_JMX_HOSTNAME: kafka-broker-1
```

#### 5. High Availability and Disaster Recovery

**Cluster setup:**
- Deploy across multiple availability zones
- Use rack awareness: `broker.rack=us-east-1a`
- Configure proper quorum for KRaft mode
- Test failover scenarios regularly

**Backup and recovery:**
- Enable topic-level replication to DR cluster
- Use MirrorMaker 2 for cross-cluster replication
- Document recovery procedures
- Test disaster recovery plan quarterly

**Monitoring and alerting:**
- Alert on broker downtime
- Alert on under-replicated partitions
- Alert on consumer lag (if applicable)
- Set up on-call rotation for Kafka operations

### Multi-Cluster Deployment

For multi-cluster deployments (geographic distribution, disaster recovery):

1. **Use MirrorMaker 2** for topic replication
2. **Configure active-active or active-passive** based on use case
3. **Use cluster-aware routing** in the application:
   ```yaml
   outbox:
     routing:
       rules:
         OrderCreated:
           clusters: [production-cluster, dr-cluster]
           strategy: at-least-one  # Succeeds if one cluster accepts
   ```
4. **Monitor replication lag** between clusters

## Database Configuration

### Azure SQL / SQL Server

**Production configuration options:**

1. **Use Azure SQL Database Managed Instance** or dedicated SQL Server
   - Not Azure SQL Edge (development only)

2. **Resource sizing:**
   - Start with: 4 vCores, 16 GB RAM
   - Scale based on workload monitoring

3. **Connection pooling:**
   ```yaml
   datasource:
     hikari:
       maximum-pool-size: 20
       minimum-idle: 5
       connection-timeout: 30000
       idle-timeout: 600000
       max-lifetime: 1800000
   ```

4. **High availability:**
   - Enable geo-replication for disaster recovery
   - Use read replicas for reporting queries
   - Configure automatic failover groups

5. **Security:**
   - Enable encryption at rest
   - Use private endpoints
   - Enable auditing and threat detection
   - Rotate database credentials regularly

6. **Backup:**
   - Configure automated backups (7-35 day retention)
   - Test restore procedures
   - Document recovery time objectives (RTO) and recovery point objectives (RPO)

## Application Configuration

### Environment Variables

**Never hardcode secrets.** Use environment variables or secret management:

```yaml
# routebox-server production configuration
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  
  kafka:
    clusters:
      production-cluster:
        bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
        properties:
          sasl.jaas.config: >
            org.apache.kafka.common.security.scram.ScramLoginModule required
            username="${KAFKA_USERNAME}"
            password="${KAFKA_PASSWORD}";
```

### JVM Configuration

**JVM settings for production:**

```bash
# Basic JVM settings
JAVA_OPTS="-Xms2g -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/routebox/heap_dump.hprof"

# JMX settings for production monitoring (with authentication and SSL)
# IMPORTANT: For production, enable JMX authentication and SSL
# Development/testing only (unsecured):
# JAVA_OPTS="$JAVA_OPTS \
#   -Dcom.sun.management.jmxremote \
#   -Dcom.sun.management.jmxremote.port=9010 \
#   -Dcom.sun.management.jmxremote.authenticate=false \
#   -Dcom.sun.management.jmxremote.ssl=false"

# Production (secured):
JAVA_OPTS="$JAVA_OPTS \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.rmi.port=9010 \
  -Dcom.sun.management.jmxremote.authenticate=true \
  -Dcom.sun.management.jmxremote.password.file=/etc/routebox/jmxremote.password \
  -Dcom.sun.management.jmxremote.access.file=/etc/routebox/jmxremote.access \
  -Dcom.sun.management.jmxremote.ssl=true \
  -Dcom.sun.management.jmxremote.registry.ssl=true \
  -Djavax.net.ssl.keyStore=/etc/routebox/keystore.jks \
  -Djavax.net.ssl.keyStorePassword=\${JMX_KEYSTORE_PASSWORD} \
  -Djavax.net.ssl.trustStore=/etc/routebox/truststore.jks \
  -Djavax.net.ssl.trustStorePassword=\${JMX_TRUSTSTORE_PASSWORD}"
```

### Logging

**Production logging configuration:**

```yaml
logging:
  structured:
    format:
      console: ecs  # Use ECS format for centralized logging
  level:
    com.example.routebox: INFO
    org.springframework: WARN
    org.hibernate: WARN
  file:
    name: /var/log/routebox/application.log
    max-size: 100MB
    max-history: 30
```

### Graceful Shutdown

Ensure proper shutdown handling:

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

## Deployment Strategies

### Container Orchestration

**Kubernetes:**

1. **Use Helm charts** for deployment management
2. **Configure resource limits and requests:**
   ```yaml
   resources:
     requests:
       memory: "2Gi"
       cpu: "1000m"
     limits:
       memory: "4Gi"
       cpu: "2000m"
   ```
3. **Set up horizontal pod autoscaling:**
   ```yaml
   apiVersion: autoscaling/v2
   kind: HorizontalPodAutoscaler
   metadata:
     name: routebox-server
   spec:
     scaleTargetRef:
       apiVersion: apps/v1
       kind: Deployment
       name: routebox-server
     minReplicas: 3
     maxReplicas: 10
     metrics:
     - type: Resource
       resource:
         name: cpu
         target:
           type: Utilization
           averageUtilization: 70
   ```
4. **Use liveness and readiness probes:**
   ```yaml
   livenessProbe:
     httpGet:
       path: /actuator/health/liveness
       port: 8081
     initialDelaySeconds: 60
     periodSeconds: 10
   readinessProbe:
     httpGet:
       path: /actuator/health/readiness
       port: 8081
     initialDelaySeconds: 30
     periodSeconds: 5
   ```

### CI/CD Pipeline

**Pipeline stages:**

1. **Build:**
   - Run unit and integration tests
   - Build Docker image
   - Tag with version and commit SHA

2. **Security scanning:**
   - Scan Docker images for vulnerabilities
   - Run SAST/DAST security tools
   - Check for exposed secrets

3. **Deploy to staging:**
   - Run smoke tests
   - Run integration tests
   - Performance testing

4. **Deploy to production:**
   - Blue/green or canary deployment
   - Monitor metrics and logs
   - Automated rollback on failure

## Security Checklist

Production security requirements:

- [ ] Use HTTPS/TLS for all network communication
- [ ] Enable Kafka SSL/SASL authentication
- [ ] Configure proper ACLs for Kafka topics
- [ ] Use secret management (Vault, AWS Secrets Manager, etc.)
- [ ] Enable database encryption at rest and in transit
- [ ] Configure network security groups/firewalls
- [ ] Enable audit logging for Kafka and database
- [ ] Implement rate limiting and DDoS protection
- [ ] Regular security patching and updates
- [ ] Penetration testing and vulnerability scanning
- [ ] Incident response plan documented and tested

## Monitoring and Alerting

### Essential Metrics

**Application metrics:**
- Outbox event processing rate
- Failed event count and rate
- Dead letter queue size
- Event processing latency
- JVM metrics (heap, GC, threads)

**Infrastructure metrics:**
- CPU and memory utilization
- Disk I/O and space
- Network throughput
- Database connection pool metrics

### Alerting Rules

**Critical alerts:**
- Application down (no health check response)
- Database connection failures
- Kafka broker down
- High error rate (>5% of requests)
- Dead letter queue growing rapidly

**Warning alerts:**
- High CPU/memory usage (>80%)
- High event processing latency (>5s p99)
- Low disk space (<20% available)
- Database connection pool exhaustion

## Cost Optimization

1. **Right-size resources** based on actual usage
2. **Use reserved instances** for predictable workloads
3. **Implement data retention policies** for Kafka and database
4. **Archive old events** to cheaper storage (S3, blob storage)
5. **Use spot instances** for non-critical workloads
6. **Monitor and optimize** database query performance

## Compliance and Auditing

For regulated industries:

1. **Enable audit logging** for all data access
2. **Implement data retention policies** per regulatory requirements
3. **Use encryption** for data at rest and in transit
4. **Document data flows** and processing activities
5. **Regular compliance audits** and certifications
6. **Data residency** considerations for multi-region deployments

## Support and Maintenance

1. **Document runbooks** for common operational tasks
2. **Establish on-call rotation** for production support
3. **Regular maintenance windows** for updates
4. **Capacity planning** and resource forecasting
5. **Post-incident reviews** and improvements

## References

- [Kafka Production Recommendations](https://kafka.apache.org/documentation/#prodconfig)
- [Spring Boot Production Features](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Azure SQL Best Practices](https://learn.microsoft.com/en-us/azure/azure-sql/database/performance-guidance)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/configuration/overview/)
