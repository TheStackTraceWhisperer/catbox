#!/bin/bash

# Run the Order Processor Service
# This script starts the order processor which consumes and processes order events from Kafka

set -e

echo "Starting Order Processor Service..."
echo "=================================="
echo ""
echo "Prerequisites:"
echo "  - Infrastructure is running (docker compose up)"
echo "  - Database 'catbox' exists"
echo "  - Catbox Server is running (publishes events to Kafka)"
echo "  - Order Service is running (creates events)"
echo ""
echo "The Order Processor will:"
echo "  - Listen to OrderCreated and OrderStatusChanged topics"
echo "  - Deduplicate messages using OutboxFilter"
echo "  - Process events with simulated business logic"
echo "  - Run on port 8082"
echo ""
echo "Press Ctrl+C to stop"
echo ""

# Check if DB_PASSWORD is set
if [ -z "$DB_PASSWORD" ]; then
    echo "Warning: DB_PASSWORD environment variable is not set."
    echo "Using default password from application.yml"
    echo ""
fi

# Run the application with azuresql profile
mvn spring-boot:run -pl order-processor -Dspring-boot.run.profiles=azuresql
