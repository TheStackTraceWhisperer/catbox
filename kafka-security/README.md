# Kafka Security Configuration

This directory contains SSL certificates, keystores, and SASL configuration for securing Kafka communication.

## Directory Structure

```
kafka-security/
├── certs/                          # SSL/TLS certificates and keystores
│   ├── ca-cert.pem                # CA certificate (public)
│   ├── ca-keystore.jks            # CA keystore (private)
│   ├── kafka-broker-keystore.jks  # Kafka broker keystore
│   ├── kafka-truststore.jks       # Kafka broker truststore
│   ├── kafka-client-truststore.jks # Client truststore
│   ├── credentials.properties     # Keystore/truststore passwords
│   ├── kafka_server_jaas.conf     # Server JAAS configuration
│   ├── kafka_client_jaas.conf     # Client JAAS configuration
│   └── generate-certs.sh          # Certificate generation script
└── README.md                       # This file
```

## Security Features

### 1. SSL/TLS Encryption
- **Purpose**: Encrypts data in transit between clients and Kafka brokers
- **Certificates**: Generated using self-signed CA (suitable for development)
- **Validity**: 10 years (3650 days)

### 2. SASL Authentication (SCRAM-SHA-512)
- **Purpose**: Authenticates clients before allowing connections
- **Mechanism**: SCRAM-SHA-512 (Salted Challenge Response Authentication Mechanism)
- **Users**:
  - `admin` / `admin-secret` - Administrative access
  - `producer` / `producer-secret` - Producer client access
  - `consumer` / `consumer-secret` - Consumer client access

### 3. ACLs (Access Control Lists)
- **Purpose**: Controls which users can access which topics
- **Configuration**: Configured in Kafka broker
- **Enforcement**: Enabled via `authorizer.class.name`

## Generating Certificates

To regenerate certificates (if needed):

```bash
cd kafka-security/certs
./generate-certs.sh
```

This will create:
- A self-signed Certificate Authority (CA)
- Kafka broker keystore with signed certificate
- Truststore for clients
- JAAS configuration files for SASL

## Default Credentials

**IMPORTANT**: These are development credentials. In production, use strong passwords and secure credential management.

### Keystore/Truststore Passwords
- Keystore Password: `changeit`
- Truststore Password: `changeit`
- Key Password: `changeit`

### SASL Users
- Admin: `admin` / `admin-secret`
- Producer: `producer` / `producer-secret`
- Consumer: `consumer` / `consumer-secret`

## Usage in Application

The application configuration (`application.yml`) uses Spring Boot SSL bundles to configure Kafka clients:

```yaml
spring:
  ssl:
    bundle:
      jks:
        kafka-client:
          truststore:
            location: file:kafka-security/certs/kafka-client-truststore.jks
            password: changeit

kafka:
  clusters:
    cluster-a:
      ssl:
        bundle: kafka-client
      properties:
        security.protocol: SASL_SSL
        sasl.mechanism: SCRAM-SHA-512
        sasl.jaas.config: |
          org.apache.kafka.common.security.scram.ScramLoginModule required
          username="producer"
          password="producer-secret";
```

## Production Deployment

For production use:

1. **Use proper CA-signed certificates** instead of self-signed
2. **Store credentials securely**:
   - Use environment variables
   - Use secret management tools (Vault, AWS Secrets Manager, etc.)
   - Never commit credentials to version control
3. **Rotate credentials regularly**
4. **Use strong passwords** (not the defaults)
5. **Enable mTLS** (mutual TLS) for additional security
6. **Configure proper ACLs** based on your application needs

## Security Best Practices

1. **Principle of Least Privilege**: Grant only necessary permissions via ACLs
2. **Network Segmentation**: Use firewalls to restrict Kafka access
3. **Audit Logging**: Enable Kafka audit logs to track access
4. **Regular Updates**: Keep Kafka and Java versions up to date
5. **Monitor Security Events**: Alert on authentication failures and ACL violations

## Troubleshooting

### SSL Handshake Failures
- Verify certificate validity: `keytool -list -v -keystore kafka-broker-keystore.jks`
- Check SAN (Subject Alternative Names) includes kafka, localhost, 127.0.0.1
- Ensure truststore contains CA certificate

### SASL Authentication Failures
- Verify username/password in JAAS configuration
- Check SASL mechanism matches (SCRAM-SHA-512)
- Review Kafka logs for authentication errors

### ACL Permission Denied
- List ACLs: `kafka-acls.sh --list --bootstrap-server kafka:9093`
- Verify user has proper permissions for topic operations
- Check principal format in ACL rules

## References

- [Kafka Security Documentation](https://kafka.apache.org/documentation/#security)
- [Spring Boot SSL Bundles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.ssl)
- [SASL/SCRAM Authentication](https://kafka.apache.org/documentation/#security_sasl_scram)
