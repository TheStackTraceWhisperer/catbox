#!/bin/bash
set -e

# Wait for SQL Server to start
sleep 30s

# Run the initialization script
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -d master -Q "
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'catbox')
BEGIN
    CREATE DATABASE catbox;
END
" -C -N

# Run the schema initialization
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -d catbox -i /docker-entrypoint-initdb.d/init.sql -C -N

echo "Database initialization completed successfully"
