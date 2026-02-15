/**
 * Domain model representing a payment transaction in the payment processing platform.
 * 
 * This class encapsulates the complete state of a payment throughout its lifecycle:
 * - Transaction details: paymentId, orderId, amount, currency
 * - Participants: userId, merchantId, paymentMethod
 * - State tracking: status, createdAt, updatedAt
 * 
 * Used by: All payment services for state management and persistence
 * Status flow: INITIATED → AUTHORIZED/REJECTED → SETTLED/FAILED
 * Purpose: Provides a consistent domain model for payment state across the CQRS architecture
 */
package com.payment.platform.core.model;

import java.time.LocalDateTime;

public class Payment {
    private String paymentId;
    private String orderId;
    private String amount;
    private String currency;
    private String userId;
    private String merchantId;
    private PaymentStatus status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Payment() {}
    
    public Payment(String paymentId, String orderId, String amount, String currency, String userId, String merchantId, PaymentStatus status, String paymentMethod) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.userId = userId;
        this.merchantId = merchantId;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Manual getters and setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

/**
 * Enumeration representing the possible states of a payment transaction.
 * 
 * Status Flow:
 * INITIATED → AUTHORIZED/REJECTED → SETTLED/FAILED
 * 
 * INITIATED: Payment created and sent for authorization
 * AUTHORIZED: Payment approved by authorization service
 * REJECTED: Payment denied by authorization service
 * SETTLED: Payment successfully processed with payment provider
 * FAILED: Payment processing failed during settlement
 */
enum PaymentStatus {
    INITIATED, AUTHORIZED, REJECTED, SETTLED, FAILED
}
