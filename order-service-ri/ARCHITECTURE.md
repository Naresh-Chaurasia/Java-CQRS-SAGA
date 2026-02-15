# Order Service Architecture Documentation

## Overview

This document describes the complete architecture of the Order Service and its integration within the CQRS-SAGA payment platform.

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Order Service │    │ Authorization   │    │  Settlement     │
│   (Port 8080)   │    │   Service       │    │   Service       │
│                 │    │   (Port 8081)   │    │   (Port 8082)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Axon Server   │
                    │   (Port 8124)   │
                    │  Event Bus &    │
                    │  Command Gateway│
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Eureka        │
                    │   (Port 8761)   │
                    │  Service        │
                    │  Discovery      │
                    └─────────────────┘
```

## Order Service Components

### 1. Core Components

#### OrderAggregate
- **Purpose**: Root aggregate for order management
- **Responsibilities**: 
  - Handle `CreateOrderCommand`
  - Emit `OrderCreatedEvent`
  - Process payment events (`PaymentSettledEvent`, `PaymentRejectedEvent`)
- **Location**: `command/OrderAggregate.java`

#### OrderEventHandler
- **Purpose**: Processes events and updates read model
- **Responsibilities**:
  - Handle `OrderCreatedEvent` → Save to database
  - Handle `PaymentSettledEvent` → Update order to COMPLETED
  - Handle `PaymentRejectedEvent` → Update order to PAYMENT_FAILED
- **Location**: `handler/OrderEventHandler.java`

#### OrderController
- **Purpose**: REST API endpoints
- **Endpoints**:
  - `POST /api/orders` - Create new order
  - `GET /api/orders/{orderId}` - Get specific order
  - `GET /api/orders/user/{userId}` - Get user's orders
  - `GET /api/orders` - Get all orders
- **Location**: `controller/OrderController.java`

### 2. Data Model

#### OrderEntity
```java
@Entity
public class OrderEntity {
    private String orderId;
    private String userId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### OrderStatus Enum
```java
public enum OrderStatus {
    CREATED,
    APPROVED,
    REJECTED,
    COMPLETED,
    CANCELLED,
    PROCESSING_PAYMENT,
    PAYMENT_FAILED
}
```

## Event Flow

### Complete Payment Workflow

1. **Order Creation**
   ```
   Client → POST /api/orders → CreateOrderCommand → OrderAggregate → OrderCreatedEvent
   ```

2. **Payment Initiation** (typically in OrderSaga or external service)
   ```
   PaymentInitiatedEvent → Authorization Service → PaymentAuthorizedEvent/ PaymentRejectedEvent
   ```

3. **Payment Settlement**
   ```
   PaymentAuthorizedEvent → Settlement Service → PaymentSettledEvent
   ```

4. **Order Completion**
   ```
   PaymentSettledEvent → Order Service → Order status = COMPLETED
   ```

### Event Sequence Diagram

```
Client    Order Service    Axon Server    Auth Service    Settlement Service
  |             |              |               |                   |
  |--CreateOrder------------>|               |                   |
  |             |              |               |                   |
  |<--OrderCreated-----------|               |                   |
  |             |              |               |                   |
  |--PaymentInitiated------->|               |                   |
  |             |              |               |                   |
  |             |<--PaymentAuthorizedEvent----|                   |
  |             |              |               |                   |
  |             |<--PaymentSettledEvent------------------------|
  |             |              |               |                   |
  |<--OrderCompleted---------|               |                   |
```

## Configuration

### Application Properties

```properties
# Server Configuration
server.port=8080
spring.application.name=order-service-ri

# Database Configuration
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:orderdb
spring.jpa.hibernate.ddl-auto=create-drop

# Axon Framework Configuration
axon.axonserver.servers=localhost:8124
axon.axonserver.token=123abc
axon.serializer.events=xstream
axon.serialization.xstream.allowed-types=com.payment.platform.**

# Event Processing
axon.eventhandling.processors.order-group.mode=tracking
axon.eventhandling.processors.order-group.source=eventBus

# Eureka Configuration
eureka.client.service-url.default-zone=http://localhost:8761/eureka/
```

## Integration Points

### 1. Payment Platform Events

#### PaymentSettledEvent
```java
public class PaymentSettledEvent extends PaymentEvent {
    private String settlementId;
    private LocalDateTime settlementDate;
}
```
- **Handled by**: `OrderEventHandler.on(PaymentSettledEvent)`
- **Action**: Update order status to `COMPLETED`

#### PaymentRejectedEvent
```java
public class PaymentRejectedEvent extends PaymentEvent {
    private String reason;
    private String errorCode;
}
```
- **Handled by**: `OrderEventHandler.on(PaymentRejectedEvent)`
- **Action**: Update order status to `PAYMENT_FAILED`

### 2. Service Discovery

- **Registration**: Automatic with Eureka on startup
- **Discovery**: Services can discover each other via Eureka
- **Health Checks**: Spring Boot Actuator endpoints

### 3. Event Bus

- **Technology**: Axon Framework
- **Protocol**: gRPC (Axon Server)
- **Serialization**: XStream with custom type configuration

## Testing

### Manual Testing

1. **Create Order**:
   ```bash
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{"userId":"user-123","productId":"product-456","productName":"Test Product","quantity":2,"unitPrice":99.99}'
   ```

2. **Test Complete Workflow**:
   ```bash
   curl -X POST http://localhost:8080/api/test/create-order-and-pay
   ```

3. **Check Order Status**:
   ```bash
   curl http://localhost:8080/api/orders/{orderId}
   ```

### Automated Testing

Run the test script:
```bash
./test-workflow.sh
```

## Database

### H2 Console
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:orderdb`
- **Username**: `sa`
- **Password**: `password`

### Tables
- `orders` - Order data
- `token_entry` - Axon event tracking tokens
- `domain_event_entry` - Axon event store

## Monitoring & Debugging

### 1. Logs
- **Level**: DEBUG for payment platform packages
- **Location**: Console output
- **Key Events**: Order creation, payment events, status updates

### 2. Axon Dashboard
- **URL**: http://localhost:8124
- **Features**: Event monitoring, command tracking

### 3. Eureka Dashboard
- **URL**: http://localhost:8761
- **Features**: Service registration, health status

## Deployment Considerations

### 1. Scaling
- **Stateless**: Order service can be horizontally scaled
- **Event Store**: Axon Server handles distributed event storage
- **Database**: Consider external database for production

### 2. Resilience
- **Circuit Breakers**: Consider for external service calls
- **Retry Logic**: Built into Axon Framework
- **Event Replay**: Possible with event sourcing

### 3. Security
- **Authentication**: Add Spring Security
- **Authorization**: Role-based access control
- **Event Security**: Validate event sources

## Future Enhancements

1. **Order Saga**: Implement saga pattern for complex workflows
2. **Compensation**: Add compensation actions for failed payments
3. **Notifications**: Event-driven notifications for order status changes
4. **Analytics**: Event stream for order analytics
5. **API Versioning**: Support multiple API versions
6. **Caching**: Redis for order query performance
7. **Distributed Tracing**: Zipkin/Jaeger integration