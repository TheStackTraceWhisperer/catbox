-- Catbox Database Initialization Script
-- This script creates all tables required for the Catbox outbox pattern implementation

-- Create orders table (used by order-service example)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'orders')
BEGIN
    CREATE TABLE orders (
        id BIGINT IDENTITY NOT NULL PRIMARY KEY,
        customer_name VARCHAR(255) NOT NULL,
        product_name VARCHAR(255) NOT NULL,
        amount NUMERIC(38,2) NOT NULL,
        status VARCHAR(255) NOT NULL,
        created_at DATETIME2(6) NOT NULL
    );
END;

-- Create outbox_events table (main outbox table)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'outbox_events')
BEGIN
    CREATE TABLE outbox_events (
        id BIGINT IDENTITY NOT NULL PRIMARY KEY,
        aggregate_type VARCHAR(255) NOT NULL,
        aggregate_id VARCHAR(255) NOT NULL,
        event_type VARCHAR(255) NOT NULL,
        correlation_id VARCHAR(255),
        payload TEXT NOT NULL,
        created_at DATETIME2(6) NOT NULL,
        sent_at DATETIME2(6),
        in_progress_until DATETIME2(6),
        permanent_failure_count INT,
        last_error TEXT
    );

    -- Create unique index on correlation_id where it's not null
    CREATE UNIQUE NONCLUSTERED INDEX UKp9ad1bboyh8ahp5qqk2wywh3d 
    ON outbox_events (correlation_id) 
    WHERE correlation_id IS NOT NULL;
END;

-- Create outbox_archive_events table (for successfully sent events)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'outbox_archive_events')
BEGIN
    CREATE TABLE outbox_archive_events (
        id BIGINT IDENTITY NOT NULL PRIMARY KEY,
        original_event_id BIGINT NOT NULL,
        aggregate_type VARCHAR(255) NOT NULL,
        aggregate_id VARCHAR(255) NOT NULL,
        event_type VARCHAR(255) NOT NULL,
        correlation_id VARCHAR(255),
        payload TEXT NOT NULL,
        created_at DATETIME2(6) NOT NULL,
        sent_at DATETIME2(6) NOT NULL,
        archived_at DATETIME2(6) NOT NULL
    );
END;

-- Create outbox_dead_letter_events table (for permanently failed events)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'outbox_dead_letter_events')
BEGIN
    CREATE TABLE outbox_dead_letter_events (
        id BIGINT IDENTITY NOT NULL PRIMARY KEY,
        original_event_id BIGINT NOT NULL,
        aggregate_type VARCHAR(255) NOT NULL,
        aggregate_id VARCHAR(255) NOT NULL,
        event_type VARCHAR(255) NOT NULL,
        payload TEXT NOT NULL,
        original_created_at DATETIME2(6) NOT NULL,
        failed_at DATETIME2(6) NOT NULL,
        final_error TEXT NOT NULL
    );
END;
