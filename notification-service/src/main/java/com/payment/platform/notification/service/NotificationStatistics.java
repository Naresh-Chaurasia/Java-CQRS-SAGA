package com.payment.platform.notification.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatistics {
    
    private LocalDateTime generatedAt;
    private Long totalNotifications;
    private Long sentNotifications;
    private Long failedNotifications;
    private Long pendingNotifications;
    private Map<String, Long> notificationsByEventType;
    private Map<String, Long> notificationsByChannel;
    private Map<String, Long> notificationsByStatus;
    private Double averageDeliveryTime;
    private Long notificationsInLastHour;
    private Long notificationsInLast24Hours;
    
    // File-based statistics for sample implementation
    private FileNotificationService.NotificationFileStatistics fileStatistics;
    
    private String logDirectoryInfo;
    private String implementationType = "FILE_BASED";
}