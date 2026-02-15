package com.payment.platform.notification.model;

/**
 * Notification processing status
 */
public enum NotificationStatus {
    PENDING,
    PROCESSING,
    SENT,
    DELIVERED,
    FAILED,
    RETRYING
}