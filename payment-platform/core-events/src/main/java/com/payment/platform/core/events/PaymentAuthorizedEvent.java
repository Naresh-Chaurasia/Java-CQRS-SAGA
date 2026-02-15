package com.payment.platform.core.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentAuthorizedEvent extends com.payment.platform.core.events.PaymentEvent1 {
    private String authorizationCode;
    private String riskScore;
}
