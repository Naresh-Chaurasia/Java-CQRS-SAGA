# Payment Reconciliation Service

A critical component of the payment processing platform that ensures data consistency between payments, authorizations, and settlements.

## 🎯 Purpose

The reconciliation service provides:
- **Automated Reconciliation**: Scheduled jobs to compare payments vs settlements
- **Event-Driven Updates**: Real-time ledger updates via Axon Framework
- **Mismatch Detection**: Identifies and reports payment inconsistencies
- **Observability**: Structured logging and metrics for monitoring
- **Manual Operations**: REST API for ad-hoc reconciliation tasks

## 🏗️ Architecture

### Data Flow
```
PaymentService → PaymentInitiatedEvent → ReconciliationEventHandler → PaymentLedger
AuthorizationService → PaymentAuthorizedEvent → ReconciliationEventHandler → PaymentLedger
SettlementService → PaymentSettledEvent → ReconciliationEventHandler → PaymentLedger
```

### Reconciliation Logic
1. **Event Capture**: Listens to all payment-related events
2. **Ledger Updates**: Maintains payment state in database
3. **Scheduled Analysis**: Runs reconciliation jobs at configured intervals
4. **Mismatch Detection**: Identifies inconsistencies between payments and settlements
5. **Reporting**: Generates detailed reconciliation reports

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- Axon Server running on localhost:8124
- Other payment services (Payment, Authorization, Settlement)

### Running the Service
```bash
# Build the service
./mvnw clean compile

# Run the service
./mvnw spring-boot:run

# Or run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing
```bash
# Run the test script
./test-reconciliation.sh

# Run unit tests
./mvnw test

# Run integration tests
./mvnw verify
```

## 📊 API Endpoints

### Health & Status
- `GET /api/reconciliation/health` - Service health check
- `GET /api/reconciliation/stats` - Reconciliation statistics

### Reconciliation Operations
- `POST /api/reconciliation/full` - Trigger full reconciliation
- `POST /api/reconciliation/date-range` - Reconcile specific date range
- `POST /api/reconciliation/order/{orderId}` - Reconcile specific order

### Monitoring
- `GET /actuator/metrics` - Service metrics
- `GET /actuator/health` - Detailed health information
- `GET /actuator/info` - Service information

## 🔧 Configuration

### Application Properties
```properties
# Server Configuration
server.port=8084
spring.application.name=reconciliation-service

# Axon Framework
axon.axonserver.servers=localhost:8124
axon.axonserver.token=123abc

# Reconciliation Settings
reconciliation.batch.size=100
reconciliation.schedule.cron=0 0 2 * * ? # Daily at 2 AM
reconciliation.retry.max-attempts=3
reconciliation.retry.delay-ms=5000

# Database
spring.datasource.url=jdbc:h2:mem:reconciliationdb
spring.jpa.hibernate.ddl-auto=create-drop
```

### Environment Variables
- `RECONCILIATION_SCHEDULE_ENABLED` - Enable/disable scheduled jobs
- `RECONCILIATION_BATCH_SIZE` - Number of records processed per batch
- `RECONCILIATION_CRON_EXPRESSION` - Custom cron schedule

## 📈 Reconciliation Types

### 1. Missing Settlement ID
**Condition**: Payment marked as SETTLED but missing settlement ID
**Severity**: HIGH
**Action**: Alert operations team for manual investigation

### 2. Status Mismatch
**Condition**: Settlement ID present but payment not marked as SETTLED
**Severity**: MEDIUM
**Action**: Update payment status automatically

### 3. Stuck Authorization
**Condition**: Payment in AUTHORIZED status for over 24 hours
**Severity**: MEDIUM
**Action**: Alert for potential settlement issues

## 🔍 Monitoring & Observability

### Structured Logging
```json
{
  "timestamp": "2024-02-15T10:30:00Z",
  "level": "INFO",
  "service": "reconciliation-service",
  "reconciliationId": "abc-123",
  "paymentId": "pay-456",
  "mismatchType": "MISSING_SETTLEMENT_ID",
  "severity": "HIGH"
}
```

### Key Metrics
- `reconciliation.total.count` - Total reconciliations performed
- `reconciliation.mismatch.count` - Number of mismatches found
- `reconciliation.duration` - Time taken for reconciliation
- `payment.ledger.size` - Total entries in payment ledger

### Health Checks
- Database connectivity
- Axon Server connection
- Scheduled job status
- Error rate monitoring

## 🛠️ Troubleshooting

### Common Issues

#### 1. No Events Received
**Symptoms**: Empty payment ledger, no reconciliation data
**Causes**: 
- Axon Server not running
- Event handler not registered
- Network connectivity issues

**Solutions**:
```bash
# Check Axon Server connection
curl http://localhost:8124/v1/overview

# Check event handler logs
grep "ReconciliationEventHandler" logs/application.log
```

#### 2. Reconciliation Always Shows "COMPLETED"
**Symptoms**: No mismatches detected despite potential issues
**Causes**:
- No payment events received
- All payments properly matched
- Logic errors in mismatch detection

**Solutions**:
```bash
# Check payment ledger entries
curl http://localhost:8084/h2-console

# Trigger manual reconciliation
curl -X POST http://localhost:8084/api/reconciliation/full
```

#### 3. High Memory Usage
**Symptoms**: OutOfMemoryError, slow performance
**Causes**:
- Large batch sizes
- Memory leaks in event processing
- Inefficient database queries

**Solutions**:
```properties
# Reduce batch size
reconciliation.batch.size=50

# Enable GC logging
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps
```

## 🔄 Development Workflow

### Adding New Reconciliation Rules
1. Update `analyzeEntry()` method in `ReconciliationService`
2. Add new mismatch type constants
3. Update test cases
4. Document the new rule

### Testing Event Handlers
```bash
# Send test events via Axon Server
curl -X POST http://localhost:8124/v1/events \
  -H "Content-Type: application/json" \
  -d '{
    "payload": {
      "type": "PaymentInitiatedEvent",
      "paymentId": "test-123",
      "orderId": "order-456"
    }
  }'
```

## 📚 Related Services

- **PaymentService**: Initiates payments and emits events
- **AuthorizationService**: Authorizes payments and emits events
- **SettlementService**: Settles payments and emits events
- **NotificationService**: Sends notifications based on reconciliation results

## 🔒 Security Considerations

- Sensitive payment data is masked in logs
- API endpoints should be protected with authentication
- Database access is restricted to service accounts
- Event data is validated before processing

## 📈 Performance Tuning

### Database Optimization
```sql
-- Add indexes for better query performance
CREATE INDEX idx_payment_status ON payment_ledger(payment_status);
CREATE INDEX idx_reconciliation_status ON payment_ledger(reconciliation_status);
CREATE INDEX idx_created_at ON payment_ledger(created_at);
```

### JVM Tuning
```bash
# Recommended JVM settings for production
-Xms512m -Xmx2g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+PrintGCDetails
```

## 📞 Support

For issues and questions:
1. Check application logs
2. Review reconciliation reports
3. Verify service health endpoints
4. Contact the platform team with correlation IDs
