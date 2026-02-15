package com.payment.platform.authorization;
import java.time.LocalDateTime;

import com.payment.platform.core.events.PaymentEvent;

public class SimplePaymentInitiatedEvent extends PaymentEvent {
    private String amount;
    private String currency;
    private String userId;
    private String merchantId;
    private String paymentMethod;
    
    public SimplePaymentInitiatedEvent() {}
    
    // Manual getters since Lombok isn't working
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
