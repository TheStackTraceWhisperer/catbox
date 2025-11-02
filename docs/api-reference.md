# API Reference

This document describes the RESTful API endpoints available in the Catbox system.

## Order Service API

The Order Service runs on port 8080 and provides endpoints for managing orders.

**Base URL:** `http://localhost:8080`

### Create Order

Create a new order.

**Endpoint:** `POST /api/orders`

**Request Body:**
```json
{
  "customerName": "Alice Johnson",
  "productName": "Mechanical Keyboard",
  "amount": 149.99
}
```

**Response:**
```json
{
  "id": 1,
  "customerName": "Alice Johnson",
  "productName": "Mechanical Keyboard",
  "amount": 149.99,
  "status": "PENDING",
  "createdAt": "2024-01-01T12:00:00Z"
}
```

### Get All Orders

Retrieve all orders.

**Endpoint:** `GET /api/orders`

**Response:**
```json
[
  {
    "id": 1,
    "customerName": "Alice Johnson",
    "productName": "Mechanical Keyboard",
    "amount": 149.99,
    "status": "PENDING",
    "createdAt": "2024-01-01T12:00:00Z"
  }
]
```

### Get Order by ID

Retrieve a single order by ID.

**Endpoint:** `GET /api/orders/{id}`

**Path Parameters:**
- `id` (required): The order ID

**Response:**
```json
{
  "id": 1,
  "customerName": "Alice Johnson",
  "productName": "Mechanical Keyboard",
  "amount": 149.99,
  "status": "PENDING",
  "createdAt": "2024-01-01T12:00:00Z"
}
```

### Update Order Status

Update an order's status.

**Endpoint:** `PATCH /api/orders/{id}/status`

**Path Parameters:**
- `id` (required): The order ID

**Request Body:**
```json
{
  "status": "SHIPPED"
}
```

**Valid Status Values:**
- `PENDING`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Response:**
```json
{
  "id": 1,
  "customerName": "Alice Johnson",
  "productName": "Mechanical Keyboard",
  "amount": 149.99,
  "status": "SHIPPED",
  "createdAt": "2024-01-01T12:00:00Z"
}
```

## Catbox Server API

The Catbox Server runs on port 8081 and provides endpoints for monitoring outbox events.

**Base URL:** `http://localhost:8081`

### Get All Outbox Events

Retrieve all outbox events.

**Endpoint:** `GET /api/outbox-events`

**Response:**
```json
[
  {
    "id": 1,
    "eventType": "OrderCreated",
    "payload": "{\"orderId\":1,\"customerName\":\"Alice Johnson\"}",
    "createdAt": "2024-01-01T12:00:00Z",
    "sentAt": "2024-01-01T12:00:02Z",
    "inProgressUntil": null
  }
]
```

### Get Pending Events

Retrieve only unsent outbox events.

**Endpoint:** `GET /api/outbox-events/pending`

**Response:**
```json
[
  {
    "id": 2,
    "eventType": "OrderStatusChanged",
    "payload": "{\"orderId\":1,\"status\":\"SHIPPED\"}",
    "createdAt": "2024-01-01T12:05:00Z",
    "sentAt": null,
    "inProgressUntil": null
  }
]
```

### Search Outbox Events

Paginated search for outbox events.

**Endpoint:** `GET /api/outbox-events/search`

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field and direction (e.g., `createdAt,desc`)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "eventType": "OrderCreated",
      "payload": "{\"orderId\":1}",
      "createdAt": "2024-01-01T12:00:00Z",
      "sentAt": "2024-01-01T12:00:02Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### Mark Event as Unsent

Manually mark a sent event for reprocessing.

**Endpoint:** `POST /api/outbox-events/{id}/mark-unsent`

**Path Parameters:**
- `id` (required): The outbox event ID

**Response:**
```json
{
  "id": 1,
  "eventType": "OrderCreated",
  "payload": "{\"orderId\":1}",
  "createdAt": "2024-01-01T12:00:00Z",
  "sentAt": null,
  "inProgressUntil": null
}
```

## Admin Web UI

The `catbox-server` also hosts a simple Thymeleaf-based web interface for monitoring and administrative tasks.

**URL:** `http://localhost:8081/admin`

### Features

- **View all outbox events** in a paginated table
- **Filter events** by:
  - `eventType` - Filter by specific event types
  - `aggregateType` - Filter by aggregate type
  - `aggregateId` - Filter by aggregate ID
  - `pendingOnly` - Show only unsent events
- **Sort** by various columns (ID, event type, created date, sent date)
- **Manually mark events** - Change a `Sent` event back to `Unsent` to trigger reprocessing

### Accessing the Admin UI

1. Ensure the catbox-server is running on port 8081
2. Navigate to `http://localhost:8081/admin` in your web browser
3. Use the filter and sort controls to find specific events
4. Click on an event to see details or mark it for reprocessing

The Admin UI provides a convenient way to monitor the outbox events without using API calls or database queries.

## Health and Metrics Endpoints

Both services expose Spring Boot Actuator endpoints for health checks and metrics.

### Health Check

**Endpoint:** `GET /actuator/health`

**Response:**
```json
{
  "status": "UP"
}
```

### Prometheus Metrics

**Endpoint:** `GET /actuator/prometheus`

**Response:** Prometheus-formatted metrics (plain text)

See [Monitoring](monitoring.md) for details on custom outbox metrics.
