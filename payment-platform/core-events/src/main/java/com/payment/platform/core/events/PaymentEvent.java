package com.payment.platform.core.events;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public abstract class PaymentEvent {
    protected String paymentId;
    protected String orderId;
    protected LocalDateTime timestamp;
    protected String correlationId;
}
