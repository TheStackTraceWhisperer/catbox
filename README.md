# Catbox - Spring Boot WebMVC Application with Transactional Outbox Pattern

A Spring Boot 3.5.7 WebMVC application demonstrating the transactional outbox pattern using Java 21, Spring Data JPA, and virtual threads.

## Features

- **Spring Boot 3.5.7** with WebMVC
- **Java 21** with virtual threads for improved concurrency
- **Spring Data JPA** for database access
- **Transactional Outbox Pattern** for reliable event publishing
- **H2 In-Memory Database** for easy local development
- **RESTful API** for order management

## Architecture

The application implements the transactional outbox pattern to ensure reliable message delivery:

1. **Order Creation/Update**: When an order is created or updated, both the order and an outbox event are saved in the same database transaction.
2. **Event Publishing**: A scheduled background task polls the outbox table for pending events and publishes them to external systems.
3. **Guaranteed Delivery**: Since both the business entity and outbox event are saved in a single transaction, we ensure at-least-once delivery.

## Prerequisites

- Java 21 or higher
- Maven 3.6+

## Building the Application

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Create Order
```bash
POST /api/orders
Content-Type: application/json

{
  "customerName": "John Doe",
  "productName": "Laptop",
  "amount": 999.99
}
```

### Get All Orders
```bash
GET /api/orders
```

### Get Order by ID
```bash
GET /api/orders/{id}
```

### Update Order Status
```bash
PATCH /api/orders/{id}/status
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

### Get All Outbox Events
```bash
GET /api/outbox-events
```

### Get Pending Outbox Events
```bash
GET /api/outbox-events/pending
```

## Example Usage

1. Create an order:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Johnson",
    "productName": "Mechanical Keyboard",
    "amount": 149.99
  }'
```

2. View all orders:
```bash
curl http://localhost:8080/api/orders
```

3. View outbox events:
```bash
curl http://localhost:8080/api/outbox-events
```

4. Update order status:
```bash
curl -X PATCH http://localhost:8080/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
```

## Virtual Threads

The application is configured to use Java 21 virtual threads for:
- Web request handling (Tomcat)
- Async task execution

This provides better scalability and resource utilization compared to traditional thread pools.

## Database

The application uses H2 in-memory database. You can access the H2 console at:
```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:catboxdb`
- Username: `sa`
- Password: (empty)

## Transactional Outbox Pattern

The `OutboxEventPublisher` service runs a scheduled task every 5 seconds to:
1. Fetch pending outbox events from the database
2. Publish them to external systems (simulated)
3. Mark them as processed

In a production environment, you would integrate this with a real message broker like Kafka or RabbitMQ.

## Testing

Run the tests with:
```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/catbox/
│   │   ├── CatboxApplication.java          # Main application class
│   │   ├── config/
│   │   │   └── VirtualThreadConfiguration.java  # Virtual threads config
│   │   ├── controller/
│   │   │   └── OrderController.java        # REST API endpoints
│   │   ├── entity/
│   │   │   ├── Order.java                  # Order entity
│   │   │   └── OutboxEvent.java            # Outbox event entity
│   │   ├── repository/
│   │   │   ├── OrderRepository.java        # Order repository
│   │   │   └── OutboxEventRepository.java  # Outbox repository
│   │   └── service/
│   │       ├── OrderService.java           # Order business logic
│   │       └── OutboxEventPublisher.java   # Outbox publisher
│   └── resources/
│       └── application.yml                 # Application configuration
└── test/
    └── java/com/example/catbox/
        ├── CatboxApplicationTests.java     # Context load test
        └── service/
            └── OrderServiceTest.java       # Service tests
```

## License

This project is open source and available under the MIT License.