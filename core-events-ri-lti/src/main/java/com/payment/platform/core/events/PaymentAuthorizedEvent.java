/**
 * Event fired when a payment transaction has been successfully authorized by the AuthorizationService.
 * 
 * This event indicates that the payment has passed all validation rules and is approved:
 * - authorizationCode: Unique code generated for this successful authorization
 * - riskScore: Calculated risk assessment score from the authorization engine
 * 
 * Used by: SettlementService to process the actual payment settlement
 * Triggers: Payment settlement workflow and order processing continuation
 * Purpose: Confirms payment approval and enables downstream settlement processes
 */
package com.payment.platform.core.events;

public class PaymentAuthorizedEvent extends PaymentEvent {
    private String authorizationCode;
    private String riskScore;
    private String amount;
    
    public PaymentAuthorizedEvent() {}
    
    public PaymentAuthorizedEvent(String paymentId, String orderId, String authorizationCode, String riskScore, String amount) {
        this.setPaymentId(paymentId);
        this.setOrderId(orderId);
        this.setAuthorizationCode(authorizationCode);
        this.setRiskScore(riskScore);
        this.setAmount(amount);
        this.setTimestamp(java.time.LocalDateTime.now());
    }
    
    // Manual getters and setters
    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }
    
    public String getRiskScore() { return riskScore; }
    public void setRiskScore(String riskScore) { this.riskScore = riskScore; }
    
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}
