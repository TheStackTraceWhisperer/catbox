# RouteBox UI Screenshots

This folder contains screenshots of the RouteBox administrative web interface and infrastructure services.

## Infrastructure Services

### Kafka UI Dashboard
**File:** `kafka-ui-dashboard.png`

The Kafka UI web interface showing both configured Kafka clusters:
- **cluster-a** - Primary Kafka cluster (ports 9092 PLAINTEXT, 9093 SASL_SSL)
- **cluster-c** - Secondary Kafka cluster for multi-cluster testing (port 9095)
- **Dashboard view**: Shows cluster status, broker count, partitions, topics, and throughput metrics
- **Access**: http://localhost:8090

### Prometheus Dashboard
**File:** `prometheus-dashboard.png`

The Prometheus metrics collection and query interface:
- **Query interface**: PromQL expression editor for querying metrics
- **Visualization**: Table and graph views for metric data
- **Access**: http://localhost:9090

### Grafana Dashboard
**File:** `grafana-dashboard.png`

The Grafana visualization platform:
- **Welcome page**: Getting started guide and tutorials
- **Pre-configured**: Connected to Prometheus data source
- **Dashboard management**: Create and manage visualization dashboards
- **Default credentials**: admin/admin
- **Access**: http://localhost:3000

### Alertmanager Dashboard
**File:** `alertmanager-dashboard.png`

The Alertmanager alert routing and notification interface:
- **Active alerts**: Shows firing alerts (RouteBoxServerDown, OrderServiceDown)
- **Alert grouping**: Organized by receiver (email-notifications)
- **Silence management**: Create and manage alert silences
- **Access**: http://localhost:9094

### Mailhog Email Interface
**File:** `mailhog-dashboard.png`

The Mailhog email testing interface:
- **Captured emails**: Shows alert notification emails sent by Alertmanager
- **Email preview**: View email content without sending to real addresses
- **SMTP server**: Listening on port 1025
- **Web UI Access**: http://localhost:8025

### Keycloak Login
**File:** `keycloak-login.png`

The Keycloak identity and access management interface:
- **Authentication**: OAuth2/OIDC provider for securing RouteBox services
- **Admin console**: Manage realms, users, and clients
- **Default credentials**: admin/admin
- **Access**: http://localhost:8180

## Admin Pages

### Outbox Events Page
**File:** `outbox-admin-page.png`

The main admin page showing the outbox events table with:
- **Filtering options**: Filter by Event Type, Aggregate Type, Aggregate ID
- **Sorting controls**: Sort by Created At, Sent At, or Event Type in ascending/descending order
- **Pagination**: Configurable page size (10, 20, 50, 100 items)
- **Status indicators**: 
  - **Sent** (green) - Event successfully published to Kafka
  - **In Progress** (blue) - Event currently being processed
  - **Pending** (yellow) - Event waiting to be processed
- **Event details**: ID, Event Type, Aggregate Type, Aggregate ID, Created At, Sent At, In Progress Until
- **Actions**: Mark Unsent button to reprocess sent events

Example events shown:
- OrderCreated events for various orders
- OrderStatusChanged event for order status updates

### Processed Messages Page
**File:** `processed-messages-page.png`

The processed messages admin page showing successfully consumed messages with:
- **Filtering options**: Filter by Consumer Group and Correlation ID
- **Sorting controls**: Sort by Processed At, Correlation ID, or Consumer Group
- **Message tracking**: Tracks which consumer groups have processed which events
- **Deduplication support**: Uses correlation ID to prevent duplicate processing
- **Message details**: 
  - ID, Correlation ID, Consumer Group
  - Event Type, Aggregate Type, Aggregate ID
  - Processed At timestamp
- **Actions**: Mark Unprocessed button to allow reprocessing

Example consumer groups shown:
- `order-service-consumer` - Main order service consumer
- `inventory-service-consumer` - Inventory management consumer

## Accessing the UI

When the routebox-server is running, access the admin interface at:
- **Outbox Events**: `http://localhost:8081/admin`
- **Processed Messages**: `http://localhost:8081/admin/processed-messages`

## Features Demonstrated

1. **Event Lifecycle Management**: Track events from creation through publishing
2. **Status Monitoring**: Visual indicators for event processing status
3. **Filtering & Search**: Find specific events by type, aggregate, or ID
4. **Consumer Tracking**: See which services have processed which events
5. **Reprocessing**: Ability to mark events as unsent or unprocessed for retry
6. **Pagination**: Handle large volumes of events efficiently
