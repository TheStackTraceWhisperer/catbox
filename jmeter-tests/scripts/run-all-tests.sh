#!/bin/bash
# Script to run all JMeter tests sequentially using Docker

set -e

echo "=================================="
echo "Running JMeter Test Suite"
echo "=================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed or not in PATH"
    echo ""
    echo "Please install Docker to run JMeter tests"
    echo "Download from: https://www.docker.com/get-started"
    exit 1
fi

# Pull JMeter Docker image
echo "Pulling JMeter Docker image..."
docker pull justb4/jmeter:5.6.3
echo "Using: JMeter 5.6.3 (Docker container)"

# Navigate to jmeter-tests directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JMETER_DIR="$(dirname "$SCRIPT_DIR")"
cd "$JMETER_DIR"

# Create results directory if it doesn't exist
mkdir -p results

# Timestamp for this test run
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Configuration
ORDER_HOST=${ORDER_HOST:-localhost}
ORDER_PORT=${ORDER_PORT:-8080}
OUTBOX_HOST=${OUTBOX_HOST:-localhost}
OUTBOX_PORT=${OUTBOX_PORT:-8081}

echo ""
echo "Configuration:"
echo "  Order Service: http://${ORDER_HOST}:${ORDER_PORT}"
echo "  Outbox Service: http://${OUTBOX_HOST}:${OUTBOX_PORT}"
echo "  Results Directory: results/"
echo ""

# Function to check if service is available
check_service() {
    local host=$1
    local port=$2
    local name=$3
    
    echo "Checking $name at http://${host}:${port}/actuator/health ..."
    
    if curl -f -s "http://${host}:${port}/actuator/health" > /dev/null 2>&1; then
        echo "✓ $name is available"
        return 0
    else
        echo "✗ $name is NOT available"
        return 1
    fi
}

# Check services are running
echo "Verifying services are available..."
echo ""

if ! check_service "$ORDER_HOST" "$ORDER_PORT" "Order Service"; then
    echo ""
    echo "Error: Order Service is not running!"
    echo "Please start it with: mvn spring-boot:run -pl order-service -Dspring-boot.run.profiles=azuresql"
    exit 1
fi

if ! check_service "$OUTBOX_HOST" "$OUTBOX_PORT" "Outbox Service"; then
    echo ""
    echo "Error: Outbox Service is not running!"
    echo "Please start it with: mvn spring-boot:run -pl catbox-server -Dspring-boot.run.profiles=azuresql"
    exit 1
fi

echo ""
echo "=================================="
echo "All services are available!"
echo "=================================="
echo ""

# Determine host network settings based on OS
if [[ "$OSTYPE" == "darwin"* ]] || [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
    # On macOS/Windows, use host.docker.internal to access host services
    NETWORK_MODE=""
    ORDER_HOST="host.docker.internal"
    OUTBOX_HOST="host.docker.internal"
else
    # On Linux, use host network mode
    NETWORK_MODE="--network=host"
fi

# Test 1: Order Service Load Test
echo "-------------------------------"
echo "Test 1: Order Service Load Test"
echo "-------------------------------"
echo "Running with 50 threads for 5 minutes..."
echo ""

docker run --rm \
    $NETWORK_MODE \
    -v "$JMETER_DIR/testplans:/tests" \
    -v "$JMETER_DIR/testdata:/testdata" \
    -v "$JMETER_DIR/results:/results" \
    justb4/jmeter:5.6.3 \
    -n -t "/tests/OrderService_LoadTest.jmx" \
    -Jorder.service.host="$ORDER_HOST" \
    -Jorder.service.port="$ORDER_PORT" \
    -Jnum.threads=50 \
    -Jramp.up=30 \
    -Jduration=300 \
    -l "/results/order_service_${TIMESTAMP}.jtl" \
    -e -o "/results/order_service_report_${TIMESTAMP}"

echo ""
echo "✓ Order Service Load Test completed"
echo "  Results: results/order_service_${TIMESTAMP}.jtl"
echo "  Report: results/order_service_report_${TIMESTAMP}/index.html"
echo ""

# Wait a bit between tests
echo "Waiting 30 seconds before next test..."
sleep 30

# Test 2: Outbox Service Load Test
echo "-------------------------------"
echo "Test 2: Outbox Service Load Test"
echo "-------------------------------"
echo "Running with 30 threads for 5 minutes..."
echo ""

docker run --rm \
    $NETWORK_MODE \
    -v "$JMETER_DIR/testplans:/tests" \
    -v "$JMETER_DIR/testdata:/testdata" \
    -v "$JMETER_DIR/results:/results" \
    justb4/jmeter:5.6.3 \
    -n -t "/tests/OutboxService_LoadTest.jmx" \
    -Joutbox.service.host="$OUTBOX_HOST" \
    -Joutbox.service.port="$OUTBOX_PORT" \
    -Jnum.threads=30 \
    -Jramp.up=20 \
    -Jduration=300 \
    -l "/results/outbox_service_${TIMESTAMP}.jtl" \
    -e -o "/results/outbox_service_report_${TIMESTAMP}"

echo ""
echo "✓ Outbox Service Load Test completed"
echo "  Results: results/outbox_service_${TIMESTAMP}.jtl"
echo "  Report: results/outbox_service_report_${TIMESTAMP}/index.html"
echo ""

# Wait a bit between tests
echo "Waiting 30 seconds before next test..."
sleep 30

# Test 3: End-to-End Stress Test
echo "-------------------------------"
echo "Test 3: End-to-End Stress Test"
echo "-------------------------------"
echo "Running with 100 threads for 10 minutes..."
echo ""

docker run --rm \
    $NETWORK_MODE \
    -v "$JMETER_DIR/testplans:/tests" \
    -v "$JMETER_DIR/testdata:/testdata" \
    -v "$JMETER_DIR/results:/results" \
    justb4/jmeter:5.6.3 \
    -n -t "/tests/EndToEnd_StressTest.jmx" \
    -Jorder.service.host="$ORDER_HOST" \
    -Jorder.service.port="$ORDER_PORT" \
    -Joutbox.service.host="$OUTBOX_HOST" \
    -Joutbox.service.port="$OUTBOX_PORT" \
    -Jstress.threads=100 \
    -Jramp.up=60 \
    -Jduration=600 \
    -l "/results/stress_test_${TIMESTAMP}.jtl" \
    -e -o "/results/stress_test_report_${TIMESTAMP}"

echo ""
echo "✓ End-to-End Stress Test completed"
echo "  Results: results/stress_test_${TIMESTAMP}.jtl"
echo "  Report: results/stress_test_report_${TIMESTAMP}/index.html"
echo ""

# Summary
echo "=================================="
echo "All Tests Completed Successfully!"
echo "=================================="
echo ""
echo "Test Results Summary:"
echo "---------------------"
echo "1. Order Service Load Test:"
echo "   Report: results/order_service_report_${TIMESTAMP}/index.html"
echo ""
echo "2. Outbox Service Load Test:"
echo "   Report: results/outbox_service_report_${TIMESTAMP}/index.html"
echo ""
echo "3. End-to-End Stress Test:"
echo "   Report: results/stress_test_report_${TIMESTAMP}/index.html"
echo ""
echo "Open the HTML reports in your browser to view detailed results."
echo ""
