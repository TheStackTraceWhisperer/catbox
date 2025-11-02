# Keycloak Configuration

This directory contains the Keycloak realm configuration for the Catbox application.

## Configuration Files

- `catbox-realm.json`: Realm import file with pre-configured catbox realm, user, and client

## Development vs. Production

⚠️ **IMPORTANT**: This configuration is designed for **DEVELOPMENT ONLY**.

### Security Considerations for Production

Before deploying to production, you **MUST** update the following:

1. **SSL/TLS Configuration** (`sslRequired`):
   - Current: `"none"` (allows HTTP)
   - Production: Change to `"all"` or `"external"` to enforce HTTPS

2. **User Password**:
   - Current: `catbox` user has password `catbox`
   - Production: Use strong passwords or set `"temporary": true` to force password change on first login
   - Better: Remove embedded users and create them through Keycloak admin console

3. **Client Secret**:
   - Current: `catbox-server-secret` (predictable and in source control)
   - Production: Use environment variables or secure secret management (e.g., Vault, AWS Secrets Manager)
   - Generate a cryptographically strong secret

4. **Admin Credentials**:
   - Current: Default admin/admin (set in ../compose.yaml)
   - Production: Change via environment variables before deployment

## Modifying the Configuration

1. Edit `catbox-realm.json` as needed
2. Restart Keycloak to import changes:
   ```bash
   cd .. && docker compose restart keycloak
   ```

## Access

- **Application**: http://localhost:8080/realms/catbox
- **Admin Console**: http://localhost:8080/admin
  - Username: `admin`
  - Password: `admin`
