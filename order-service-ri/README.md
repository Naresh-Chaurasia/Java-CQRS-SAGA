# Order Service RI

A modern order management service built with Spring Boot, Axon Framework, and CQRS pattern.

## Features

- **CQRS Architecture**: Separate command and query responsibilities
- **Event Sourcing**: Complete audit trail of order changes
- **Event-Driven**: Integrates with payment platform events
- **Microservice**: Eureka service discovery enabled
- **REST API**: Full CRUD operations for orders

## Architecture

### Components

1. **OrderAggregate**: Root aggregate handling order commands
2. **OrderEventHandler**: Processes payment events and updates order status
3. **OrderController**: REST endpoints for order management
4. **OrderRepository**: JPA repository for order queries

### Event Flow

1. `CreateOrderCommand` → `OrderCreatedEvent`
2. `PaymentSettledEvent` → Order status updated to `COMPLETED`
3. `PaymentRejectedEvent` → Order status updated to `PAYMENT_FAILED`

## Configuration

- **Port**: 8080
- **Database**: H2 (in-memory)
- **Axon Server**: localhost:8124
- **Eureka**: localhost:8761

## API Endpoints

### Create Order
```http
POST /api/orders
Content-Type: application/json

{
  "userId": "user-123",
  "productId": "product-456",
  "productName": "Product Name",
  "quantity": 2,
  "unitPrice": 99.99
}
```

### Get Order
```http
GET /api/orders/{orderId}
```

### Get Orders by User
```http
GET /api/orders/user/{userId}
```

### Get All Orders
```http
GET /api/orders
```

## Order Statuses

- `CREATED`: Order initially created
- `PROCESSING_PAYMENT`: Payment in progress
- `COMPLETED`: Payment successful
- `PAYMENT_FAILED`: Payment rejected
- `CANCELLED`: Order cancelled

## Integration

This service integrates with:
- **Authorization Service**: Receives payment initiation events
- **Settlement Service**: Receives payment settlement events
- **Eureka**: Service registration and discovery
- **Axon Server**: Event bus and command gateway

## Running the Service

```bash
mvn spring-boot:run
```

## Testing

The service includes comprehensive event handling for payment workflows. Test the complete flow by:

1. Creating an order via REST API
2. Triggering payment authorization
3. Verifying order status updates

## Database

H2 console available at: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:orderdb`
- Username: `sa`
- Password: `password`