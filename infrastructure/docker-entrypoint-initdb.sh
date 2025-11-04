#!/bin/bash
set -e

# Function to wait for SQL Server to be ready
wait_for_sqlserver() {
    local retries=30
    local wait_time=2
    
    echo "Waiting for SQL Server to start..."
    for i in $(seq 1 $retries); do
        if /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -Q "SELECT 1" -C -N &>/dev/null; then
            echo "SQL Server is ready!"
            return 0
        fi
        echo "Attempt $i/$retries: SQL Server not ready yet, waiting ${wait_time}s..."
        sleep $wait_time
    done
    
    echo "ERROR: SQL Server failed to start within expected time"
    return 1
}

# Wait for SQL Server to be ready
if ! wait_for_sqlserver; then
    exit 1
fi

# Create database if it doesn't exist
echo "Creating database if not exists..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -d master -C -N -Q "
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'routebox')
BEGIN
    CREATE DATABASE routebox;
END
"

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create database"
    exit 1
fi

# Run the schema initialization
echo "Running schema initialization from init.sql..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -d routebox -i /docker-entrypoint-initdb.d/init.sql -C -N

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to initialize database schema"
    exit 1
fi

echo "Database initialization completed successfully"
