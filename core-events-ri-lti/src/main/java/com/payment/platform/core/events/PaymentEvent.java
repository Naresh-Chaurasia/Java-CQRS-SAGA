/**
 * Base abstract class for all payment-related events in the CQRS event-driven architecture.
 * 
 * This class provides common fields that all payment events should have:
 * - paymentId: Unique identifier for the payment transaction
 * - orderId: Reference to the order that triggered this payment
 * - timestamp: When the event occurred
 * - correlationId: Used to track related events across the saga
 * 
 * Used by: PaymentAuthorizedEvent, PaymentRejectedEvent, PaymentSettledEvent, PaymentInitiatedEvent
 * Purpose: Provides consistent event structure and enables event correlation in distributed transactions
 */
package com.payment.platform.core.events;

import java.time.LocalDateTime;

public abstract class PaymentEvent {
    protected String paymentId;
    protected String orderId;
    protected LocalDateTime timestamp;
    protected String correlationId;
    
    public PaymentEvent() {}
    
    // Manual getters and setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
