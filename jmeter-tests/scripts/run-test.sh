#!/bin/bash
# Quick script to run a single JMeter test

set -e

# Usage information
usage() {
    echo "Usage: $0 <test-name> [options]"
    echo ""
    echo "Available tests:"
    echo "  order      - Order Service Load Test"
    echo "  outbox     - Outbox Service Load Test"
    echo "  stress     - End-to-End Stress Test"
    echo ""
    echo "Options:"
    echo "  -t <threads>   Number of threads (default: varies by test)"
    echo "  -r <seconds>   Ramp-up time in seconds (default: varies by test)"
    echo "  -d <seconds>   Duration in seconds (default: varies by test)"
    echo "  -h             Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 order"
    echo "  $0 stress -t 150 -r 90 -d 900"
    echo "  $0 outbox -t 50 -d 600"
    exit 1
}

# Check if JMeter is installed
if ! command -v jmeter &> /dev/null; then
    echo "Error: JMeter is not installed or not in PATH"
    exit 1
fi

# Check arguments
if [ $# -lt 1 ]; then
    usage
fi

TEST_NAME=$1
shift

# Default values
THREADS=""
RAMP_UP=""
DURATION=""

# Parse options
while getopts "t:r:d:h" opt; do
    case $opt in
        t) THREADS=$OPTARG ;;
        r) RAMP_UP=$OPTARG ;;
        d) DURATION=$OPTARG ;;
        h) usage ;;
        \?) usage ;;
    esac
done

# Navigate to jmeter-tests directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JMETER_DIR="$(dirname "$SCRIPT_DIR")"
cd "$JMETER_DIR"

# Create results directory
mkdir -p results

# Timestamp
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Build JMeter command based on test name
case $TEST_NAME in
    order)
        TEST_FILE="testplans/OrderService_LoadTest.jmx"
        RESULT_PREFIX="order_service"
        THREADS=${THREADS:-50}
        RAMP_UP=${RAMP_UP:-30}
        DURATION=${DURATION:-300}
        PARAMS="-Jnum.threads=$THREADS -Jramp.up=$RAMP_UP -Jduration=$DURATION"
        ;;
    outbox)
        TEST_FILE="testplans/OutboxService_LoadTest.jmx"
        RESULT_PREFIX="outbox_service"
        THREADS=${THREADS:-30}
        RAMP_UP=${RAMP_UP:-20}
        DURATION=${DURATION:-300}
        PARAMS="-Jnum.threads=$THREADS -Jramp.up=$RAMP_UP -Jduration=$DURATION"
        ;;
    stress)
        TEST_FILE="testplans/EndToEnd_StressTest.jmx"
        RESULT_PREFIX="stress_test"
        THREADS=${THREADS:-100}
        RAMP_UP=${RAMP_UP:-60}
        DURATION=${DURATION:-600}
        PARAMS="-Jstress.threads=$THREADS -Jramp.up=$RAMP_UP -Jduration=$DURATION"
        ;;
    *)
        echo "Error: Unknown test name '$TEST_NAME'"
        echo ""
        usage
        ;;
esac

echo "=================================="
echo "Running JMeter Test: $TEST_NAME"
echo "=================================="
echo ""
echo "Configuration:"
echo "  Test Plan: $TEST_FILE"
echo "  Threads: $THREADS"
echo "  Ramp-up: ${RAMP_UP}s"
echo "  Duration: ${DURATION}s"
echo ""
echo "Starting test..."
echo ""

# Run JMeter test
jmeter -n -t "$TEST_FILE" \
    $PARAMS \
    -l "results/${RESULT_PREFIX}_${TIMESTAMP}.jtl" \
    -e -o "results/${RESULT_PREFIX}_report_${TIMESTAMP}"

echo ""
echo "=================================="
echo "Test Completed Successfully!"
echo "=================================="
echo ""
echo "Results:"
echo "  Data: results/${RESULT_PREFIX}_${TIMESTAMP}.jtl"
echo "  HTML Report: results/${RESULT_PREFIX}_report_${TIMESTAMP}/index.html"
echo ""
