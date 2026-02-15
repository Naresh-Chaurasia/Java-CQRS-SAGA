package com.payment.platform.authorization.events;

import java.time.LocalDateTime;

public class PaymentAuthorizedEvent {
    protected String paymentId;
    protected String orderId;
    protected LocalDateTime timestamp;
    protected String correlationId;
    private String authorizationCode;
    private String riskScore;
    
    public PaymentAuthorizedEvent() {}
    
    // Manual getters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }
    
    public String getRiskScore() { return riskScore; }
    public void setRiskScore(String riskScore) { this.riskScore = riskScore; }
}
