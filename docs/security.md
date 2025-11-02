# Security Configuration

This document describes the security features available in the Catbox project.

## Kafka Security

The project supports comprehensive Kafka security features including SSL/TLS encryption, SASL authentication, and ACL-based access control.

### Security Features

1. **SSL/TLS Encryption** - Encrypts data in transit between clients and Kafka brokers
2. **SASL Authentication (SCRAM-SHA-512)** - Authenticates clients before allowing connections
3. **ACLs (Access Control Lists)** - Controls which users can access which topics

### Quick Start with Security

The Kafka broker is configured with two listeners:
- **Port 9092** - PLAINTEXT (for backward compatibility and testing)
- **Port 9093** - SASL_SSL (secure with authentication and encryption)

#### 1. Generate SSL Certificates

First-time setup requires generating SSL certificates:

```bash
cd infrastructure/kafka-security/certs
./generate-certs.sh
cd ../../..
```

This creates:
- Self-signed Certificate Authority (CA)
- Kafka broker keystore with signed certificate
- Client truststore for secure connections
- JAAS configuration files for SASL authentication

#### 2. Start Kafka with Security Enabled

```bash
cd infrastructure && docker compose up -d kafka
```

The Kafka container will start with:
- SSL/TLS enabled on port 9093
- SASL SCRAM-SHA-512 authentication configured
- ACL authorization enabled (deny by default)

#### 3. Initialize Security Configuration

After Kafka starts, run the initialization script to create SASL users and configure ACLs:

```bash
./infrastructure/kafka-security/init-kafka-security.sh
```

This script:
- Creates SCRAM-SHA-512 credentials for admin, producer, and consumer users
- Creates test topics (OrderCreated, OrderStatusChanged)
- Configures ACLs to grant appropriate permissions

#### 4. Using Secure Kafka Connection

The application is pre-configured to use the secure cluster (cluster-b) on port 9093:

```yaml
kafka:
  clusters:
    cluster-b:
      bootstrap-servers: localhost:9093
      ssl:
        bundle: kafka-client
      properties:
        security.protocol: SASL_SSL
        sasl.mechanism: SCRAM-SHA-512
        sasl.jaas.config: ...username="producer" password="producer-secret"...
```

To route events to the secure cluster, update the routing rules in `application.yml`:

```yaml
outbox:
  routing:
    rules:
      OrderCreated: cluster-b        # Routes to secure cluster
      OrderStatusChanged: cluster-b  # Routes to secure cluster
```

### Security Credentials (Development)

**IMPORTANT**: These are development credentials. In production, use strong passwords and secure credential management.

**SASL Users:**
- Admin: `admin` / `admin-secret` (superuser - full access)
- Producer: `producer` / `producer-secret` (write access to order topics)
- Consumer: `consumer` / `consumer-secret` (read access to order topics)

**Keystore/Truststore Passwords:**
- All passwords: `changeit`

### Verifying Security Configuration

Check SCRAM users:
```bash
docker exec catbox-kafka kafka-configs.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --entity-type users
```

List configured ACLs:
```bash
docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --list
```

### Troubleshooting Kafka Security

**SSL Handshake Failures:**
- Verify truststore path is correct in application.yml
- Check certificate validity: `keytool -list -v -keystore infrastructure/kafka-security/certs/kafka-client-truststore.jks`

**SASL Authentication Failures:**
- Verify username/password in JAAS configuration
- Check that SCRAM users were created: see "Verifying Security Configuration" above

**ACL Permission Denied:**
- List ACLs to verify user has proper permissions
- Ensure super users are configured correctly in infrastructure/compose.yaml

### Production Deployment

For production, refer to:
- `infrastructure/kafka-security/README.md` - Detailed security documentation
- `catbox-server/src/main/resources/application-production.yml.example` - Production configuration template

**Production Security Checklist:**
1. ✅ Use CA-signed certificates (not self-signed)
2. ✅ Store credentials in environment variables or secret management tools
3. ✅ Use strong passwords (not default "changeit")
4. ✅ Enable mutual TLS (mTLS) for additional security
5. ✅ Configure proper ACLs based on principle of least privilege
6. ✅ Enable Kafka audit logging
7. ✅ Rotate credentials regularly
8. ✅ Use network segmentation and firewalls

### Known Issues

⚠️ **Kafka Docker Image Compatibility:** The Apache Kafka Docker image currently has compatibility issues with the SSL/SASL configuration in `compose.yaml`. For testing purposes, you may need to use a simplified configuration (PLAINTEXT only) or use Kafka in a different container setup. See [KNOWN_ISSUES.md](../KNOWN_ISSUES.md) for details.

## Keycloak Security

The catbox-server supports OAuth2/OIDC authentication via Keycloak. Security is **disabled by default** and can be enabled via Spring Boot profile.

### Keycloak Setup

The Docker Compose configuration includes a Keycloak container that is automatically configured with:

- **Realm**: `catbox`
- **User**: `catbox` with password `catbox`
- **Client ID**: `catbox-server`
- **Client Secret**: `catbox-server-secret`

### Running with Security Enabled

To enable authentication, use the `secure` profile:

```bash
# Start Keycloak and other infrastructure
cd infrastructure && docker compose up -d

# Run catbox-server with security enabled (from project root)
cd .. && mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql,secure
```

### Accessing the Application

1. Navigate to `http://localhost:8081`
2. You will be redirected to Keycloak login page
3. Login with:
   - **Username**: `catbox`
   - **Password**: `catbox`
4. After successful authentication, you'll be redirected back to the application

### Keycloak Admin Console

Access the Keycloak admin console at `http://localhost:8180`:
- **Username**: `admin`
- **Password**: `admin`

From the admin console, you can:
- Add more users
- Configure additional clients
- Manage realm settings
- View user sessions and events

### Security Configuration

Security is configured via Spring profiles:

- **Default profile**: Security disabled (all requests permitted)
- **`secure` profile**: OAuth2/OIDC authentication enabled with Keycloak

The configuration allows:
- Unauthenticated access to health checks (`/actuator/health/**`) and metrics (`/actuator/prometheus`)
- All other endpoints require authentication

### Customizing Keycloak Configuration

The Keycloak realm configuration is defined in `infrastructure/keycloak/catbox-realm.json`. You can modify this file to:
- Add additional users
- Configure roles and permissions
- Set up client scopes
- Enable/disable features

After modifying the realm file, restart Keycloak:
```bash
cd infrastructure && docker compose restart keycloak
```

For more details on Keycloak configuration, see `infrastructure/keycloak/README.md`.
