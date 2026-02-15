/**
 * Event fired when a payment transaction has been rejected by the AuthorizationService.
 * 
 * This event indicates that the payment failed validation rules and cannot proceed:
 * - rejectionReason: Detailed explanation of why the payment was rejected
 * - errorCode: Machine-readable code for programmatic handling
 * 
 * Used by: OrderService to handle payment failures and potentially trigger compensating transactions
 * Triggers: Order cancellation or retry workflows in the saga pattern
 * Purpose: Communicates payment failure and enables proper error handling in distributed transactions
 */
package com.payment.platform.core.events;

public class PaymentRejectedEvent extends PaymentEvent {
    private String rejectionReason;
    private String errorCode;
    
    public PaymentRejectedEvent() {}
    
    public PaymentRejectedEvent(String paymentId, String orderId, String rejectionReason, String errorCode) {
        this.setPaymentId(paymentId);
        this.setOrderId(orderId);
        this.setRejectionReason(rejectionReason);
        this.setErrorCode(errorCode);
        this.setTimestamp(java.time.LocalDateTime.now());
    }
    
    // Manual getters and setters
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
