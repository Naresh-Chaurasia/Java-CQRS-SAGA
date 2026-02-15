package com.payment.platform.core.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
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
}

enum PaymentStatus {
    INITIATED, AUTHORIZED, REJECTED, SETTLED, FAILED
}
