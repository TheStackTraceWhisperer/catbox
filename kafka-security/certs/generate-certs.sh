#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Generating Kafka SSL Certificates${NC}"

# Certificate configuration
VALIDITY_DAYS=3650
KEYSTORE_PASSWORD="changeit"
TRUSTSTORE_PASSWORD="changeit"
KEY_PASSWORD="changeit"
COUNTRY="US"
STATE="CA"
ORG="Catbox"
OU="Engineering"

# Step 1: Generate CA (Certificate Authority)
echo -e "${GREEN}[1/6] Generating Certificate Authority (CA)${NC}"
keytool -genkeypair -v \
  -keystore ca-keystore.jks \
  -alias ca \
  -keyalg RSA \
  -keysize 2048 \
  -validity ${VALIDITY_DAYS} \
  -dname "CN=Catbox-CA, OU=${OU}, O=${ORG}, L=San Francisco, ST=${STATE}, C=${COUNTRY}" \
  -storepass ${KEYSTORE_PASSWORD} \
  -keypass ${KEY_PASSWORD} \
  -storetype JKS

# Export CA certificate
keytool -exportcert -v \
  -keystore ca-keystore.jks \
  -alias ca \
  -file ca-cert.pem \
  -storepass ${KEYSTORE_PASSWORD} \
  -rfc

echo -e "${GREEN}[2/6] Generating Kafka Broker Keystore${NC}"
# Step 2: Generate Kafka broker keystore
keytool -genkeypair -v \
  -keystore kafka-broker-keystore.jks \
  -alias kafka-broker \
  -keyalg RSA \
  -keysize 2048 \
  -validity ${VALIDITY_DAYS} \
  -dname "CN=kafka, OU=${OU}, O=${ORG}, L=San Francisco, ST=${STATE}, C=${COUNTRY}" \
  -ext SAN=DNS:kafka,DNS:localhost,IP:127.0.0.1 \
  -storepass ${KEYSTORE_PASSWORD} \
  -keypass ${KEY_PASSWORD} \
  -storetype JKS

# Step 3: Create certificate signing request for broker
echo -e "${GREEN}[3/6] Creating Certificate Signing Request (CSR) for Broker${NC}"
keytool -certreq -v \
  -keystore kafka-broker-keystore.jks \
  -alias kafka-broker \
  -file kafka-broker.csr \
  -storepass ${KEYSTORE_PASSWORD} \
  -ext SAN=DNS:kafka,DNS:localhost,IP:127.0.0.1

# Step 4: Sign the broker certificate with CA
echo -e "${GREEN}[4/6] Signing Broker Certificate with CA${NC}"
keytool -gencert -v \
  -keystore ca-keystore.jks \
  -alias ca \
  -infile kafka-broker.csr \
  -outfile kafka-broker-signed.pem \
  -storepass ${KEYSTORE_PASSWORD} \
  -validity ${VALIDITY_DAYS} \
  -ext SAN=DNS:kafka,DNS:localhost,IP:127.0.0.1 \
  -rfc

# Step 5: Import CA and signed certificate into broker keystore
echo -e "${GREEN}[5/6] Importing Certificates into Broker Keystore${NC}"
keytool -importcert -v \
  -keystore kafka-broker-keystore.jks \
  -alias ca \
  -file ca-cert.pem \
  -storepass ${KEYSTORE_PASSWORD} \
  -noprompt

keytool -importcert -v \
  -keystore kafka-broker-keystore.jks \
  -alias kafka-broker \
  -file kafka-broker-signed.pem \
  -storepass ${KEYSTORE_PASSWORD}

# Step 6: Create truststore with CA certificate
echo -e "${GREEN}[6/6] Creating Truststore${NC}"
keytool -importcert -v \
  -keystore kafka-truststore.jks \
  -alias ca \
  -file ca-cert.pem \
  -storepass ${TRUSTSTORE_PASSWORD} \
  -noprompt \
  -storetype JKS

# Create client truststore (same as broker truststore for mutual TLS)
cp kafka-truststore.jks kafka-client-truststore.jks

# Clean up intermediate files
rm -f kafka-broker.csr kafka-broker-signed.pem

# Create a credentials file with passwords
cat > credentials.properties << EOL
# Kafka SSL Credentials
# IMPORTANT: In production, use environment variables or secure credential management
keystore.password=${KEYSTORE_PASSWORD}
truststore.password=${TRUSTSTORE_PASSWORD}
key.password=${KEY_PASSWORD}
EOL

# Create a JAAS configuration file for SASL
# IMPORTANT: These credentials are for DEVELOPMENT ONLY
# In production, use environment variables or secure credential management
cat > kafka_server_jaas.conf << EOL
KafkaServer {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="admin"
    password="admin-secret"
    user_admin="admin-secret"
    user_producer="producer-secret"
    user_consumer="consumer-secret";
};
EOL

# Create client JAAS configuration
# IMPORTANT: These credentials are for DEVELOPMENT ONLY
cat > kafka_client_jaas.conf << EOL
KafkaClient {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="producer"
    password="producer-secret";
};
EOL

echo -e "${BLUE}✅ Certificate generation complete!${NC}"
echo ""
echo "Generated files:"
echo "  - ca-cert.pem (CA certificate)"
echo "  - ca-keystore.jks (CA keystore)"
echo "  - kafka-broker-keystore.jks (Broker keystore)"
echo "  - kafka-truststore.jks (Truststore)"
echo "  - kafka-client-truststore.jks (Client truststore)"
echo "  - credentials.properties (Passwords)"
echo "  - kafka_server_jaas.conf (Server JAAS config)"
echo "  - kafka_client_jaas.conf (Client JAAS config)"
echo ""
echo "Default passwords: ${KEYSTORE_PASSWORD}"
