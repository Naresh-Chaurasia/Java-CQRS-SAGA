package com.payment.platform.core.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentRejectedEvent extends PaymentEvent1 {
    private String rejectionReason;
    private String errorCode;
}
