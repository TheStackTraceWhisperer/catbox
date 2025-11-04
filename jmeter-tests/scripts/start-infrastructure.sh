#!/bin/bash
# Script to start infrastructure for JMeter tests

set -e

echo "=================================="
echo "Starting Catbox Infrastructure"
echo "=================================="

# Check if docker compose is available
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed or not in PATH"
    exit 1
fi

# Navigate to project root (one level above jmeter-tests)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
cd "$PROJECT_ROOT"

echo ""
echo "1. Starting Docker Compose services (Azure SQL Edge & Kafka)..."
pushd infrastructure > /dev/null
docker compose up -d
popd > /dev/null

echo ""
echo "2. Waiting for services to be healthy..."
sleep 10

# Check if services are running
echo ""
echo "3. Checking service health..."
pushd infrastructure > /dev/null
docker compose ps
popd > /dev/null

echo ""
echo "=================================="
echo "Infrastructure is ready!"
echo "=================================="
echo ""
echo "Next steps:"
echo "1. Start Order Service:"
echo "   mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql"
echo ""
echo "2. Start Catbox Server:"
echo "   mvn spring-boot:run -pl routebox-server -Dspring-boot.run.profiles=azuresql"
echo ""
echo "3. Wait 15 seconds for services to start"
echo ""
echo "4. Run JMeter tests from jmeter-tests/scripts/run-all-tests.sh"
echo ""
