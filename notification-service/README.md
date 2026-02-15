# Notification Service1

A unified notification processing service that handles multi-channel notifications for the payment platform.

## 🎯 Overview

The Notification Service provides a centralized way to send notifications through multiple channels including Email, SMS, Push Notifications, Webhooks, Console output, and UI updates.

## 🚀 Features

### Multi-Channel Support
- **Email** - SMTP with Thymeleaf templates
- **SMS** - Integration ready for SMS providers
- **Push Notifications** - Mobile app notifications
- **Webhooks** - HTTP callbacks to external systems
- **Console** - Formatted console output for debugging
- **UI** - Real-time UI notifications

### Event-Driven Processing
- **Automatic Event Handling** - Listens to payment events
- **Smart Channel Selection** - Chooses appropriate channel based on event type
- **Template Support** - Dynamic content generation
- **Retry Logic** - Automatic retry for failed notifications

### Unified API
- **REST Endpoints** - Manual notification sending
- **Statistics** - Notification metrics and analytics
- **History Tracking** - Complete audit trail
- **Status Management** - Real-time status updates

## 📋 Supported Events

### Payment Events
- `payment.settled` - Payment successfully processed
- `payment.rejected` - Payment declined
- `payment.authorized` - Payment approved
- `payment.initiated` - Payment started

### Order Events
- `order.completed` - Order finished
- `order.payment_failed` - Order payment issue

## 🔧 Configuration

### Application Properties
```properties
# Server
server.port=8083
spring.application.name=notification-service

# Database
spring.datasource.url=jdbc:h2:mem:notificationdb
spring.h2.console.enabled=true

# Axon Framework
axon.axonserver.servers=localhost:8124
axon.axonserver.token=123abc

# Email (Optional)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Channel Configuration
- **Default Channel**: CONSOLE (when no email configured)
- **Fallback**: Always falls back to console output
- **Templates**: Thymeleaf templates in `/templates/`

## 🌐 API Endpoints

### Notification Management
```http
POST   /api/notifications                    # Send notification
POST   /api/notifications/simple             # Simple notification
GET    /api/notifications/{id}               # Get notification
GET    /api/notifications/correlation/{id}   # Get by correlation ID
GET    /api/notifications/status/{status}    # Get by status
GET    /api/notifications/statistics         # Get statistics
POST   /api/notifications/retry              # Retry failed notifications
```

### UI Notifications
```http
GET    /api/notifications/ui/{recipient}     # Get UI notifications
GET    /api/notifications/ui/notification/{id} # Get specific UI notification
```

### Testing Endpoints
```http
GET    /api/notifications/test                # Service health check
POST   /api/notifications/test/payment-settled   # Test settled notification
POST   /api/notifications/test/payment-rejected  # Test rejected notification
POST   /api/notifications/test/all-channels   # Test all channels
```

## 📊 Notification Channels

### Email Channel
- **Templates**: HTML templates with Thymeleaf
- **Fallback**: Console output when SMTP not configured
- **Templates Available**:
  - `payment-settled.html` - Success notification
  - `payment-rejected.html` - Rejection notification

### SMS Channel
- **Providers**: Ready for Twilio, AWS SNS, etc.
- **Content**: Concise SMS-formatted messages
- **Character Limits**: Optimized for SMS constraints

### Push Notification Channel
- **Platforms**: iOS, Android, Web
- **Format**: Title + body structure
- **Ready for**: Firebase, APNS integration

### Webhook Channel
- **Format**: JSON payload with HTTP headers
- **Headers**: `X-Event-Type`, `X-Correlation-ID`
- **Retry**: Built-in retry logic

### Console Channel
- **Format**: Structured console output
- **Purpose**: Debugging and development
- **Features**: Color-coded, formatted output

### UI Channel
- **Storage**: In-memory (Redis in production)
- **Format**: JSON for frontend consumption
- **Features**: Severity levels, actionable notifications

## 🔄 Event Flow

```
Payment Event → NotificationEventHandler → NotificationService → Channel Processor → Recipient
```

### Automatic Processing
1. **Event Received** - Payment event from Axon Server
2. **Channel Selection** - Based on event type
3. **Content Generation** - Dynamic content creation
4. **Channel Processing** - Send through appropriate channel
5. **Status Update** - Track delivery status
6. **Retry Logic** - Handle failures

## 📈 Statistics & Monitoring

### Available Metrics
- Total notifications sent
- Success/failure rates
- Channel-specific statistics
- Event type breakdown
- Delivery time metrics
- Retry statistics

### Health Checks
- Service health: `/api/notifications/test`
- Database connectivity
- Axon Server connection
- Channel availability

## 🛠️ Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- Axon Server (localhost:8124)
- Eureka Server (localhost:8761)

### Running the Service
```bash
# Clone and build
cd notification-service
mvn clean compile

# Run the service
mvn spring-boot:run
```

### Testing
```bash
# Test service health
curl http://localhost:8083/api/notifications/test

# Test payment settled notification
curl -X POST http://localhost:8083/api/notifications/test/payment-settled

# Test all channels
curl -X POST http://localhost:8083/api/notifications/test/all-channels
```

## 🗄️ Database

### H2 Console
- **URL**: http://localhost:8083/h2-console
- **JDBC URL**: `jdbc:h2:mem:notificationdb`
- **Username**: `sa`
- **Password**: `password`

### Tables
- `notifications` - Notification records
- `token_entry` - Axon tracking tokens

## 🔐 Security Considerations

### Data Protection
- **Field Masking**: Sensitive data in logs
- **Template Sanitization**: XSS prevention
- **Input Validation**: Request validation

### Authentication
- **Service-to-Service**: API key authentication
- **User Access**: Role-based permissions
- **Webhook Security**: Signature verification

## 📝 Examples

### Send Custom Notification
```bash
curl -X POST http://localhost:8083/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "correlationId": "test-123",
    "eventType": "custom.event",
    "recipient": "user@example.com",
    "channel": "EMAIL",
    "subject": "Test Notification",
    "content": "This is a test notification"
  }'
```

### Send Simple Notification
```bash
curl -X POST "http://localhost:8083/api/notifications/simple" \
  -d "correlationId=simple-123&eventType=test.event&recipient=user@example.com&message=Hello World"
```

### Get Notification Statistics
```bash
curl http://localhost:8083/api/notifications/statistics
```

## 🚀 Production Considerations

### Scaling
- **Horizontal Scaling**: Stateless service design
- **Database**: External database (PostgreSQL/MySQL)
- **Cache**: Redis for UI notifications
- **Queue**: Message queue for high volume

### Monitoring
- **Metrics**: Prometheus/Grafana integration
- **Logging**: Structured logging with correlation IDs
- **Alerting**: Failure rate and queue depth alerts
- **Tracing**: Distributed tracing support

### Reliability
- **Circuit Breakers**: External service protection
- **Retry Policies**: Configurable retry logic
- **Dead Letter Queue**: Failed event handling
- **Health Checks**: Comprehensive health monitoring

## 🤝 Integration

### Payment Platform Integration
- **Authorization Service**: Payment status notifications
- **Settlement Service**: Settlement completion notifications
- **Order Service**: Order lifecycle notifications

### External Systems
- **Email Providers**: SMTP, SendGrid, Mailgun
- **SMS Providers**: Twilio, AWS SNS
- **Push Services**: Firebase, APNS
- **Webhook Consumers**: External system callbacks

## 📞 Support

For issues and questions:
1. Check the service logs
2. Verify Axon Server connectivity
3. Test individual channels
4. Check database connectivity
5. Review configuration settings