package com.payment.platform.core.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentInitiatedEvent extends PaymentEvent1 {
    private String amount;
    private String currency;
    private String userId;
    private String merchantId;
    private String paymentMethod;
}
