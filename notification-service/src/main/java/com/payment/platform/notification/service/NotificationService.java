package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationRequest;
import com.payment.platform.notification.model.NotificationChannel;
import com.payment.platform.notification.model.NotificationStatus;

import java.util.List;

/**
 * Unified notification processing service
 */
public interface NotificationService {
    
    /**
     * Send a notification through specified channel
     */
    NotificationEntity sendNotification(NotificationRequest request);
    
    /**
     * Send notification with automatic channel selection based on event type
     */
    NotificationEntity sendNotification(String correlationId, String eventType, String recipient, Object eventData);
    
    /**
     * Retry failed notifications
     */
    void retryFailedNotifications();
    
    /**
     * Get notification by ID
     */
    NotificationEntity getNotification(String id);
    
    /**
     * Get notifications by correlation ID
     */
    List<NotificationEntity> getNotificationsByCorrelationId(String correlationId);
    
    /**
     * Get notifications by status
     */
    List<NotificationEntity> getNotificationsByStatus(NotificationStatus status);
    
    /**
     * Get notification statistics
     */
    NotificationStatistics getStatistics();
    
    /**
     * Mark notification as delivered
     */
    void markAsDelivered(String notificationId);
    
    /**
     * Mark notification as failed
     */
    void markAsFailed(String notificationId, String errorMessage);
}