/**
 * Event fired when a new payment transaction is initiated in the payment processing platform.
 * 
 * This event contains all the essential information needed to process a payment:
 * - amount and currency: Transaction details
 * - userId and merchantId: Transaction participants
 * - paymentMethod: How the payment will be processed (credit card, bank transfer, etc.)
 * 
 * Used by: AuthorizationService to evaluate payment rules and approve/reject transactions
 * Triggers: AuthorizationRulesEngine evaluation and subsequent PaymentAuthorizedEvent or PaymentRejectedEvent
 * Purpose: Initiates the payment authorization workflow in the CQRS saga pattern
 */
package com.payment.platform.core.events;

public class PaymentInitiatedEvent extends PaymentEvent {
    private String amount;
    private String currency;
    private String userId;
    private String merchantId;
    private String paymentMethod;
    
    public PaymentInitiatedEvent() {}
    
    public PaymentInitiatedEvent(String paymentId, String orderId, String amount, String currency, String userId, String merchantId, String paymentMethod) {
        this.setPaymentId(paymentId);
        this.setOrderId(orderId);
        this.setAmount(amount);
        this.setCurrency(currency);
        this.setUserId(userId);
        this.setMerchantId(merchantId);
        this.setPaymentMethod(paymentMethod);
        this.setTimestamp(java.time.LocalDateTime.now());
    }
    
    // Manual getters and setters
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
