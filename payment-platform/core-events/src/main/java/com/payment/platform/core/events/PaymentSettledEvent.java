package com.payment.platform.core.events;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentSettledEvent extends PaymentEvent1 {
    private String settlementId;
    private java.time.LocalDateTime settlementDate;
}
