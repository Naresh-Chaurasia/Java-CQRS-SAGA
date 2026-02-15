/**
 * Event fired when a payment transaction has been successfully settled with the payment processor.
 * 
 * This event represents the final step in the payment lifecycle:
 * - settlementId: Unique identifier from the payment processor/bank
 * - settlementDate: When the actual money transfer occurred
 * 
 * Used by: OrderService to confirm order completion and NotificationService to send confirmations
 * Triggers: Order fulfillment, shipping, and customer notification workflows
 * Purpose: Marks successful completion of payment processing and enables order fulfillment
 */
package com.payment.platform.core.events;

public class PaymentSettledEvent extends PaymentEvent {
    private String settlementId;
    private java.time.LocalDateTime settlementDate;
    
    public PaymentSettledEvent() {}
    
    public PaymentSettledEvent(String paymentId, String orderId, String settlementId, java.time.LocalDateTime settlementDate) {
        this.setPaymentId(paymentId);
        this.setOrderId(orderId);
        this.setSettlementId(settlementId);
        this.setSettlementDate(settlementDate);
        this.setTimestamp(java.time.LocalDateTime.now());
    }
    
    // Manual getters and setters
    public String getSettlementId() { return settlementId; }
    public void setSettlementId(String settlementId) { this.settlementId = settlementId; }
    
    public java.time.LocalDateTime getSettlementDate() { return settlementDate; }
    public void setSettlementDate(java.time.LocalDateTime settlementDate) { this.settlementDate = settlementDate; }
}
