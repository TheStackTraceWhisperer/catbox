#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Initializing Kafka Security Configuration${NC}"
echo ""

# Wait for Kafka to be ready
echo -e "${YELLOW}Waiting for Kafka to be ready...${NC}"
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker exec catbox-kafka kafka-broker-api-versions.sh --bootstrap-server localhost:9092 &>/dev/null; then
        echo -e "${GREEN}✅ Kafka is ready${NC}"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "Attempt $RETRY_COUNT/$MAX_RETRIES - Kafka not ready yet, waiting..."
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo -e "${YELLOW}⚠️  Kafka not responding on PLAINTEXT listener, but continuing with setup${NC}"
fi

# Create SCRAM credentials
echo -e "${BLUE}Creating SCRAM-SHA-512 credentials${NC}"

# Admin user
echo -e "${GREEN}Creating admin user...${NC}"
docker exec catbox-kafka kafka-configs.sh \
  --bootstrap-server localhost:9092 \
  --alter \
  --add-config 'SCRAM-SHA-512=[password=admin-secret]' \
  --entity-type users \
  --entity-name admin

# Producer user
echo -e "${GREEN}Creating producer user...${NC}"
docker exec catbox-kafka kafka-configs.sh \
  --bootstrap-server localhost:9092 \
  --alter \
  --add-config 'SCRAM-SHA-512=[password=producer-secret]' \
  --entity-type users \
  --entity-name producer

# Consumer user
echo -e "${GREEN}Creating consumer user...${NC}"
docker exec catbox-kafka kafka-configs.sh \
  --bootstrap-server localhost:9092 \
  --alter \
  --add-config 'SCRAM-SHA-512=[password=consumer-secret]' \
  --entity-type users \
  --entity-name consumer

# Create test topics
echo -e "${BLUE}Creating test topics${NC}"
docker exec catbox-kafka kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic OrderCreated \
  --partitions 3 \
  --replication-factor 1 \
  --if-not-exists

docker exec catbox-kafka kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic OrderStatusChanged \
  --partitions 3 \
  --replication-factor 1 \
  --if-not-exists

# Configure ACLs
echo -e "${BLUE}Configuring ACLs${NC}"

# Producer ACLs - allow producer user to write to order topics
echo -e "${GREEN}Granting producer permissions...${NC}"
docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:producer \
  --operation Write \
  --operation Describe \
  --operation Create \
  --topic OrderCreated

docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:producer \
  --operation Write \
  --operation Describe \
  --operation Create \
  --topic OrderStatusChanged

# Consumer ACLs - allow consumer user to read from order topics
echo -e "${GREEN}Granting consumer permissions...${NC}"
docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:consumer \
  --operation Read \
  --operation Describe \
  --topic OrderCreated

docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:consumer \
  --operation Read \
  --operation Describe \
  --topic OrderStatusChanged

# Consumer group ACLs
docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:consumer \
  --operation Read \
  --group '*'

# List all ACLs
echo -e "${BLUE}Current ACL Configuration:${NC}"
docker exec catbox-kafka kafka-acls.sh \
  --bootstrap-server localhost:9092 \
  --list

# Verify SCRAM users
echo -e "${BLUE}Verifying SCRAM users:${NC}"
docker exec catbox-kafka kafka-configs.sh \
  --bootstrap-server localhost:9092 \
  --describe \
  --entity-type users

echo ""
echo -e "${GREEN}✅ Kafka security initialization complete!${NC}"
echo ""
echo "Security Configuration Summary:"
echo "  - SSL/TLS: Enabled on port 9093"
echo "  - SASL Mechanism: SCRAM-SHA-512"
echo "  - ACLs: Enabled (deny by default)"
echo ""
echo "Users created:"
echo "  - admin (superuser)"
echo "  - producer (write access to order topics)"
echo "  - consumer (read access to order topics)"
echo ""
echo "To connect securely, use:"
echo "  - Bootstrap server: localhost:9093"
echo "  - Security protocol: SASL_SSL"
echo "  - SASL mechanism: SCRAM-SHA-512"
echo "  - SSL truststore: kafka-security/certs/kafka-client-truststore.jks"
