package com.payment.platform.order.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompletedEvent {
    private String orderId;
    private String userId;
    private String paymentId;
    private String settlementId;
    private LocalDateTime completedAt;
}