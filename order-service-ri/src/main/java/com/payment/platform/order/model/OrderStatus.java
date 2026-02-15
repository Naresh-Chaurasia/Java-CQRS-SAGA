package com.payment.platform.order.model;

/**
 * Order status enumeration representing the lifecycle of an order
 */
public enum OrderStatus {
    CREATED,
    APPROVED,
    REJECTED,
    COMPLETED,
    CANCELLED,
    PROCESSING_PAYMENT,
    PAYMENT_FAILED
}