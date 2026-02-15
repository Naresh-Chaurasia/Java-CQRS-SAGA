package com.payment.platform.notification.service;

import com.payment.platform.notification.model.NotificationEntity;
import com.payment.platform.notification.model.NotificationChannel;
import com.payment.platform.notification.model.NotificationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Simplified notification processor that writes to log files.
 * Perfect for sample assignment implementation - no external dependencies.
 */
@Service
@Slf4j
public class SimpleNotificationProcessor {
    
    private final FileNotificationService fileNotificationService;
    
    public SimpleNotificationProcessor(FileNotificationService fileNotificationService) {
        this.fileNotificationService = fileNotificationService;
    }
    
    /**
     * Process notification by writing to appropriate log file
     */
    public boolean processNotification(NotificationEntity notification) {
        try {
            log.info("Processing notification {} via file logging", notification.getId());
            
            // Update status to processing
            notification.setStatus(NotificationStatus.PROCESSING);
            
            // Write to file-based notification system
            boolean success = fileNotificationService.writeNotification(notification);
            
            // Write to daily summary
            fileNotificationService.writeNotificationSummary(notification);
            
            if (success) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("Notification {} successfully written to file", notification.getId());
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to write to file");
                log.error("Notification {} failed to write to file", notification.getId());
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("Error processing notification {}: {}", notification.getId(), e.getMessage(), e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            return false;
        }
    }
    
    /**
     * Determine appropriate channel based on event type
     */
    public NotificationChannel determineChannel(String eventType, Object metadata) {
        // For file-based logging, we can use different channels for different log files
        switch (eventType) {
            case "payment.settled":
                return NotificationChannel.EMAIL; // Will go to email-YYYY-MM-DD.log
            case "payment.rejected":
                return NotificationChannel.SMS;   // Will go to sms-YYYY-MM-DD.log
            case "payment.authorized":
                return NotificationChannel.PUSH;  // Will go to push-YYYY-MM-DD.log
            case "payment.initiated":
                return NotificationChannel.UI;     // Will go to ui-YYYY-MM-DD.log
            case "order.completed":
                return NotificationChannel.WEBHOOK; // Will go to webhook-YYYY-MM-DD.log
            default:
                return NotificationChannel.CONSOLE; // Will go to console-YYYY-MM-DD.log
        }
    }
}