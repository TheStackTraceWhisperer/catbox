#!/bin/bash
# Helper script to run routebox-server with proper environment

cd "$(dirname "$0")"

# Set Java 21 (adjust path as needed)
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Load environment variables from .env
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
else
    echo "Warning: .env file not found. Database password may not be set."
fi

echo "Starting routebox-server on port 8081 with azuresql profile..."
echo "Java version: $(java -version 2>&1 | head -1)"
echo ""

mvn spring-boot:run -pl routebox-server -Dspring-boot.run.profiles=azuresql
